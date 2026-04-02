package com.main.icrsbackend.service;

import com.main.icrsbackend.model.communicationandtasks.CommunicationQuestion;
import com.main.icrsbackend.model.technicaltest.TechnicalQuestion;
import com.main.icrsbackend.repository.communicationandtasksrepository.CommunicationQuestionRepository;
import com.main.icrsbackend.repository.technicaltestrepository.TechnicalQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AdminImportService {

    private final TechnicalQuestionRepository technicalQuestionRepository;
    private final CommunicationQuestionRepository communicationQuestionRepository;

    private static final List<String> TECHNICAL_HEADERS = List.of(
            "stream", "skill", "level", "set_number", "question",
            "option1", "option2", "option3", "option4",
            "correct_answer", "difficulty", "question_type"
    );

    private static final List<String> COMMUNICATION_HEADERS = List.of(
            "stream", "level", "set_number", "question",
            "option1", "option2", "option3", "option4",
            "correct_answer", "difficulty", "question_type"
    );

    public String upload(String type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a valid Excel file.");
        }

        String normalizedType = normalizeType(type);
        if (!normalizedType.equals("technical") && !normalizedType.equals("communication")) {
            throw new RuntimeException("Invalid import type. Only technical and communication are allowed.");
        }

        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".xlsx")) {
            throw new RuntimeException("Only .xlsx files are allowed.");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            validateHeader(normalizedType, sheet.getRow(0));

            return switch (normalizedType) {
                case "technical" -> saveTechnical(sheet);
                case "communication" -> saveCommunication(sheet);
                default -> throw new RuntimeException("Invalid import type.");
            };

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Excel upload failed: " + e.getMessage());
        }
    }

    public byte[] generateTemplate(String type) {
        String normalizedType = normalizeType(type);
        if (!normalizedType.equals("technical") && !normalizedType.equals("communication")) {
            throw new RuntimeException("Invalid template type. Only technical and communication are allowed.");
        }

        List<String> headers = getHeaders(normalizedType);
        List<String> sampleRow = getSampleRow(normalizedType);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Template");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            Row dataRow = sheet.createRow(1);
            for (int i = 0; i < sampleRow.size(); i++) {
                dataRow.createCell(i).setCellValue(sampleRow.get(i));
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Template generation failed: " + e.getMessage());
        }
    }

    private void validateHeader(String type, Row headerRow) {
        if (headerRow == null) {
            throw new RuntimeException("Excel file is empty.");
        }

        List<String> expected = getHeaders(type);
        List<String> actual = new ArrayList<>();

        for (int i = 0; i < expected.size(); i++) {
            actual.add(getCellValue(headerRow.getCell(i)).trim().toLowerCase());
        }

        if (!actual.equals(expected)) {
            throw new RuntimeException(
                    "Invalid Excel format. Expected header: " + String.join(", ", expected)
            );
        }
    }

    private String saveTechnical(Sheet sheet) {
        int saved = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row, TECHNICAL_HEADERS.size())) continue;

            String stream = getCellValue(row.getCell(0));
            String skill = getCellValue(row.getCell(1));
            String level = getCellValue(row.getCell(2));
            Integer setNumber = getIntegerCellValue(row.getCell(3));
            String question = getCellValue(row.getCell(4));
            String option1 = getCellValue(row.getCell(5));
            String option2 = getCellValue(row.getCell(6));
            String option3 = getCellValue(row.getCell(7));
            String option4 = getCellValue(row.getCell(8));
            String correctAnswer = getCellValue(row.getCell(9));
            String difficulty = getCellValue(row.getCell(10));
            String questionType = getCellValue(row.getCell(11));

            validateRequired(i, Map.of(
                    "stream", stream,
                    "skill", skill,
                    "level", level,
                    "question", question,
                    "correct_answer", correctAnswer
            ));

            if (setNumber == null) {
                throw new RuntimeException("Row " + (i + 1) + ": set_number is required and must be numeric.");
            }

            TechnicalQuestion q = TechnicalQuestion.builder()
                    .stream(stream.trim())
                    .skill(skill.trim())
                    .level(level.trim())
                    .setNumber(setNumber)
                    .question(question.trim())
                    .option1(option1.trim())
                    .option2(option2.trim())
                    .option3(option3.trim())
                    .option4(option4.trim())
                    .correctAnswer(correctAnswer.trim())
                    .difficulty(isBlank(difficulty) ? level.trim() : difficulty.trim())
                    .questionType(isBlank(questionType) ? "MCQ" : questionType.trim())
                    .build();

            technicalQuestionRepository.save(q);
            saved++;
        }

        return "Technical questions uploaded successfully. Total saved: " + saved;
    }

    private String saveCommunication(Sheet sheet) {
        int saved = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row, COMMUNICATION_HEADERS.size())) continue;

            String stream = getCellValue(row.getCell(0));
            String level = getCellValue(row.getCell(1));
            Integer setNumber = getIntegerCellValue(row.getCell(2));
            String question = getCellValue(row.getCell(3));
            String option1 = getCellValue(row.getCell(4));
            String option2 = getCellValue(row.getCell(5));
            String option3 = getCellValue(row.getCell(6));
            String option4 = getCellValue(row.getCell(7));
            String correctAnswer = getCellValue(row.getCell(8));
            String difficulty = getCellValue(row.getCell(9));
            String questionType = getCellValue(row.getCell(10));

            validateRequired(i, Map.of(
                    "stream", stream,
                    "level", level,
                    "question", question,
                    "correct_answer", correctAnswer
            ));

            if (setNumber == null) {
                throw new RuntimeException("Row " + (i + 1) + ": set_number is required and must be numeric.");
            }

            CommunicationQuestion q = CommunicationQuestion.builder()
                    .stream(stream.trim())
                    .level(level.trim())
                    .setNumber(setNumber)
                    .question(question.trim())
                    .option1(option1.trim())
                    .option2(option2.trim())
                    .option3(option3.trim())
                    .option4(option4.trim())
                    .correctAnswer(correctAnswer.trim())
                    .difficulty(isBlank(difficulty) ? level.trim() : difficulty.trim())
                    .questionType(isBlank(questionType) ? "MCQ" : questionType.trim())
                    .build();

            communicationQuestionRepository.save(q);
            saved++;
        }

        return "Communication questions uploaded successfully. Total saved: " + saved;
    }

    private List<String> getHeaders(String type) {
        return switch (type) {
            case "technical" -> TECHNICAL_HEADERS;
            case "communication" -> COMMUNICATION_HEADERS;
            default -> throw new RuntimeException("Invalid import type.");
        };
    }

    private List<String> getSampleRow(String type) {
        return switch (type) {
            case "technical" -> List.of(
                    "Programming", "Java", "Beginner", "1",
                    "What is JVM?", "Java Variable Method", "Java Virtual Machine",
                    "Joint Virtual Memory", "None", "Java Virtual Machine",
                    "Beginner", "MCQ"
            );
            case "communication" -> List.of(
                    "Grammar", "Beginner", "1",
                    "Choose the correct sentence",
                    "He go to office", "He goes to office",
                    "He going office", "He gone office",
                    "He goes to office", "Beginner", "MCQ"
            );
            default -> throw new RuntimeException("Invalid import type.");
        };
    }

    private String normalizeType(String type) {
        return type == null ? "" : type.trim().toLowerCase();
    }

    private void validateRequired(int rowIndex, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (isBlank(entry.getValue())) {
                throw new RuntimeException("Row " + (rowIndex + 1) + ": " + entry.getKey() + " is required.");
            }
        }
    }

    private boolean isRowEmpty(Row row, int expectedCells) {
        for (int i = 0; i < expectedCells; i++) {
            if (!isBlank(getCellValue(row.getCell(i)))) {
                return false;
            }
        }
        return true;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                if (value == (long) value) yield String.valueOf((long) value);
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        double value = cell.getNumericCellValue();
                        if (value == (long) value) yield String.valueOf((long) value);
                        yield String.valueOf(value);
                    } catch (Exception ex) {
                        yield "";
                    }
                }
            }
            case BLANK -> "";
            default -> "";
        };
    }

    private Integer getIntegerCellValue(Cell cell) {
        if (cell == null) return null;

        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> (int) cell.getNumericCellValue();
                case STRING -> Integer.parseInt(cell.getStringCellValue().trim());
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}