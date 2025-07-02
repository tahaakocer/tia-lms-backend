package com.tia.lms_backend.service;

import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.GeneralException;
import jakarta.ws.rs.core.Response;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.security.SecureRandom;

@Service
@Log4j2
public class KeycloakService {

    private final Keycloak keycloakClient;
    private final EmailService emailService;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public KeycloakService(Keycloak keycloakClient, EmailService emailService) {
        this.keycloakClient = keycloakClient;
        this.emailService = emailService;
    }

    public String createKeycloakUser(String tckn, String email, String password) {
        log.info("Creating Keycloak user with realm: {}, clientId: {}", realm, clientId);
        RealmResource realmResource = this.keycloakClient.realm(realm);
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> existingUsers = usersResource.searchByUsername(tckn, true);
        if (!existingUsers.isEmpty()) {
            log.error("User {} already exists", tckn);
            throw new EntityAlreadyExistsException("User with this TCKN already exists");
        }

        List<UserRepresentation> existingEmails = usersResource.searchByEmail(email, true);
        if (!existingEmails.isEmpty()) {
            log.error("User with email {} already exists", email);
            throw new EntityAlreadyExistsException("User with this email already exists");
        }

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(tckn);
        user.setEmail(email);
        user.setEmailVerified(true);

        Response response = usersResource.create(user);
        String userId;

        if (response.getStatus() == 201) {
            userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("User created successfully with ID: {}", userId);
        } else {
            List<UserRepresentation> createdUsers = usersResource.searchByUsername(tckn, true);
            if (!createdUsers.isEmpty()) {
                userId = createdUsers.get(0).getId();
                log.warn("Kullanıcı oluşturuldu ancak beklenmeyen durum kodu alındı: {}. Kullanıcı ID: {}", response.getStatus(), userId);
            } else if (response.getStatus() == 409) {
                log.error("User with TCKN {} already exists in keycloak", tckn);
                throw new EntityAlreadyExistsException("User with this TCKN already exists in keycloak");
            } else {
                String errorMessage = response.readEntity(String.class);
                log.error("Failed to create keycloak user: {} - {}", response.getStatusInfo(), errorMessage);
                throw new GeneralException("Failed to create keycloak user: " + response.getStatusInfo() + " - " + errorMessage);
            }
        }

        String finalPassword = password;
        if (password == null || password.isEmpty()) {
            finalPassword = generateRandomPassword(8);
            log.info("Password is empty, generating random password: {}", finalPassword);
        }

        setUserPassword(usersResource.get(userId), finalPassword);

        // 🔑 EMPLOYEE rolünü ata
        assignRoleToUser(realmResource, userId, "EMPLOYEE");

        this.emailService.sendUserCredentialsEmail(email, tckn, finalPassword);
        log.info("User {} created and assigned EMPLOYEE role successfully", userId);
        return userId;
    }

    private void setUserPassword(UserResource userResource, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        userResource.resetPassword(credential);
    }

    private void assignRoleToUser(RealmResource realmResource, String userId, String roleName) {
        RolesResource rolesResource = realmResource.roles();
        RoleRepresentation role = rolesResource.get(roleName).toRepresentation();
        RoleMappingResource roleMappingResource = realmResource.users().get(userId).roles();
        RoleScopeResource realmRoleScope = roleMappingResource.realmLevel();
        realmRoleScope.add(List.of(role));
        log.info("Rol {} kullanıcıya atandı: {}", roleName, userId);
    }

    private String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}