package com.tia.lms_backend.service;

import com.tia.lms_backend.model.Course;
import com.tia.lms_backend.model.Enrollment;
import com.tia.lms_backend.model.User;
import com.tia.lms_backend.repository.CourseRepository;
import com.tia.lms_backend.repository.EnrollmentRepository;
import com.tia.lms_backend.repository.UserCourseContentRepository;
import com.tia.lms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExcelService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserCourseContentRepository userCourseContentRepository;

    public byte[] exportUsersToExcel() throws IOException {
        List<User> users = userRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Çalışanlar");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Ad");
        headerRow.createCell(2).setCellValue("Soyad");
        headerRow.createCell(3).setCellValue("E-Posta");
        headerRow.createCell(4).setCellValue("TC Kimlik");
        headerRow.createCell(5).setCellValue("Doğum Tarihi");
        headerRow.createCell(6).setCellValue("Departman");
        headerRow.createCell(7).setCellValue("Takım");
        headerRow.createCell(8).setCellValue("Rol");
        headerRow.createCell(9).setCellValue("Ünvan");
        headerRow.createCell(10).setCellValue("Eğitim");
        headerRow.createCell(11).setCellValue("Kayıt Tarihi");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int rowIdx = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(user.getId().toString());
            row.createCell(1).setCellValue(user.getName());
            row.createCell(2).setCellValue(user.getLastName());
            row.createCell(3).setCellValue(user.getEmail());
            row.createCell(4).setCellValue(user.getTckn());
            row.createCell(5).setCellValue(user.getBirthDate());
            row.createCell(6).setCellValue(user.getDepartment() != null ? user.getDepartment().getName() : "");
            row.createCell(7).setCellValue(user.getTeam() != null ? user.getTeam().getName() : "");
            row.createCell(8).setCellValue(user.getRole() != null ? user.getRole().getName() : "");
            row.createCell(9).setCellValue(user.getTitle());
            row.createCell(10).setCellValue(user.getEducation());
            row.createCell(11).setCellValue(
                    user.getCreatedDate() != null ? user.getCreatedDate().format(formatter) : ""
            );
        }

        for (int i = 0; i <= 11; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    public byte[] exportCoursesToExcel() throws IOException {
        List<Course> courses = courseRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Kurslar");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Kurs Adı");
        headerRow.createCell(2).setCellValue("Eğitmen");
        headerRow.createCell(3).setCellValue("Açıklama");
        headerRow.createCell(4).setCellValue("Kategori");
        headerRow.createCell(5).setCellValue("Süre (dk)");
        headerRow.createCell(6).setCellValue("Zorunlu mu?");
        headerRow.createCell(7).setCellValue("İçerik Sayısı");
        headerRow.createCell(8).setCellValue("Kayıt Tarihi");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int rowIdx = 1;
        for (Course course : courses) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(course.getId().toString());
            row.createCell(1).setCellValue(course.getName());
            row.createCell(2).setCellValue(course.getInstructor());
            row.createCell(3).setCellValue(course.getDescription());
            row.createCell(4).setCellValue(course.getCourseCategory() != null ? course.getCourseCategory().getName() : "");
            row.createCell(5).setCellValue(course.getDurationMinutes());
            row.createCell(6).setCellValue(course.isMandatory() ? "Evet" : "Hayır");
            row.createCell(7).setCellValue(course.getCourseContents() != null ? course.getCourseContents().size() : 0);
            row.createCell(8).setCellValue(
                    course.getCreatedDate() != null ? course.getCreatedDate().format(formatter) : ""
            );
        }

        for (int i = 0; i <= 8; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    public byte[] exportEnrollmentsWithProgressToExcel() throws IOException {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Kullanıcı Kurs İlerleme");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Kullanıcı Adı");
        headerRow.createCell(1).setCellValue("Soyadı");
        headerRow.createCell(2).setCellValue("E-Posta");
        headerRow.createCell(3).setCellValue("Kurs Adı");
        headerRow.createCell(4).setCellValue("Toplam İçerik");
        headerRow.createCell(5).setCellValue("Tamamlanan İçerik");
        headerRow.createCell(6).setCellValue("Tamamlama Yüzdesi (%)");
        headerRow.createCell(7).setCellValue("Durum");
        headerRow.createCell(8).setCellValue("Başlama Tarihi");
        headerRow.createCell(9).setCellValue("Bitiş Tarihi");
        headerRow.createCell(10).setCellValue("Tamamlama Tarihi");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int rowIdx = 1;

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getCourse() == null || enrollment.getUser() == null) continue;

            Course course = enrollment.getCourse();

            int totalContents = course.getCourseContents() != null ? course.getCourseContents().size() : 0;

            long completedContents = userCourseContentRepository.countCompletedByUserAndCourse(
                    enrollment.getUser().getId(),
                    course.getId(),
                    com.tia.lms_backend.model.enums.Status.COMPLETED
            );

            double percentage = totalContents > 0 ? ((double) completedContents / totalContents) * 100 : 0;

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(enrollment.getUser().getName());
            row.createCell(1).setCellValue(enrollment.getUser().getLastName());
            row.createCell(2).setCellValue(enrollment.getUser().getEmail());
            row.createCell(3).setCellValue(course.getName());
            row.createCell(4).setCellValue(totalContents);
            row.createCell(5).setCellValue(completedContents);
            row.createCell(6).setCellValue(String.format("%.2f", percentage));
            row.createCell(7).setCellValue(enrollment.getStatus().name());
            row.createCell(8).setCellValue(enrollment.getStartDate() != null ? enrollment.getStartDate().format(formatter) : "");
            row.createCell(9).setCellValue(enrollment.getDeadlineDate() != null ? enrollment.getDeadlineDate().format(formatter) : "");
            row.createCell(10).setCellValue(enrollment.getCompletionDate() != null ? enrollment.getCompletionDate().format(formatter) : "");
        }

        for (int i = 0; i <= 10; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}
