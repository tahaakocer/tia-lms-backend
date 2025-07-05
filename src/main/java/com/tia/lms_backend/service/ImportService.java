package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.dto.request.CreateEmployeeRequest;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.model.Department;
import com.tia.lms_backend.model.Team;
import com.tia.lms_backend.repository.DepartmentRepository;
import com.tia.lms_backend.repository.TeamRepository;
import com.tia.lms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ImportService {

    private final EmployeeService employeeService;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public List<UserDto> importUsersFromExcel(MultipartFile file) {

        List<String> validationErrors = new ArrayList<>();
        List<Row> validRows = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getCellString(row.getCell(0));
                String lastName = getCellString(row.getCell(1));
                String email = getCellString(row.getCell(2));
                String tckn = getCellString(row.getCell(3));
                String birthDate = getCellString(row.getCell(4));
                String departmentName = getCellString(row.getCell(5));
                String teamName = getCellString(row.getCell(6)); // yeni
                String title = getCellString(row.getCell(7));
                String education = getCellString(row.getCell(8));

                int excelRow = i + 1;

                // Boş alan kontrolü
                if (isBlank(name)) validationErrors.add("Row " + excelRow + ": Name is empty");
                if (isBlank(lastName)) validationErrors.add("Row " + excelRow + ": LastName is empty");
                if (isBlank(email)) validationErrors.add("Row " + excelRow + ": Email is empty");
                if (isBlank(tckn)) validationErrors.add("Row " + excelRow + ": TCKN is empty");
                if (isBlank(birthDate)) validationErrors.add("Row " + excelRow + ": BirthDate is empty");
                if (isBlank(departmentName)) validationErrors.add("Row " + excelRow + ": Department is empty");
                if (isBlank(title)) validationErrors.add("Row " + excelRow + ": Title is empty");
                if (isBlank(education)) validationErrors.add("Row " + excelRow + ": Education is empty");

                // Department var mı
                if (departmentName != null && departmentRepository.findByName(departmentName).isEmpty()) {
                    validationErrors.add("Row " + excelRow + ": Department '" + departmentName + "' not found");
                }

                // Takım varsa kontrol et
                if (!isBlank(teamName) && teamRepository.findByName(teamName).isEmpty()) {
                    validationErrors.add("Row " + excelRow + ": Team '" + teamName + "' not found");
                }

                // TCKN formatı
                if (tckn != null && !tckn.matches("\\d{11}")) {
                    validationErrors.add("Row " + excelRow + ": Invalid TCKN format");
                }

                // Email formatı
                if (email != null && !email.matches("^.+@.+\\..+$")) {
                    validationErrors.add("Row " + excelRow + ": Invalid email format");
                }

                // DB duplicate kontrolü
                if (tckn != null && userRepository.existsByTckn(tckn)) {
                    validationErrors.add("Row " + excelRow + ": TCKN already exists in DB");
                }

                if (email != null && userRepository.existsByEmail(email)) {
                    validationErrors.add("Row " + excelRow + ": Email already exists in DB");
                }

                validRows.add(row);
            }

            if (!validationErrors.isEmpty()) {
                String message = "Excel import failed! Errors:\n" + String.join("\n", validationErrors);
                throw new GeneralException(message);
            }

            // Kayıt başlasın
            List<UserDto> importedUsers = new ArrayList<>();

            for (Row row : validRows) {
                String name = getCellString(row.getCell(0));
                String lastName = getCellString(row.getCell(1));
                String email = getCellString(row.getCell(2));
                String tckn = getCellString(row.getCell(3));
                String birthDate = getCellString(row.getCell(4));
                String departmentName = getCellString(row.getCell(5));
                String teamName = getCellString(row.getCell(6));
                String title = getCellString(row.getCell(7));
                String education = getCellString(row.getCell(8));

                Department department = departmentRepository.findByName(departmentName).orElseThrow();
                Team team = null;
                if (!isBlank(teamName)) {
                    team = teamRepository.findByName(teamName).orElse(null);
                }

                CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                        .name(name)
                        .lastName(lastName)
                        .email(email)
                        .tckn(tckn)
                        .birthDate(birthDate)
                        .title(title)
                        .education(education)
                        .departmentId(department.getId().toString())
                        .profilePicture(null)
                        .teamId(team != null ? team.getId().toString() : null)
                        .build();

                UserDto dto = employeeService.registerEmployeeWithoutDefaultProfilePicture(request);
                importedUsers.add(dto);
            }

            return importedUsers;

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            log.error("Import failed", e);
            throw new RuntimeException("Excel import failed", e);
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
