package com.mednet.controller;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.mednet.entity.Prefix;
import com.mednet.service.PrefixService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/prefix")
@CrossOrigin(origins = "*")
public class PrefixController {

    private final PrefixService prefixService;

    @Autowired
    public PrefixController(PrefixService prefixService) {
        this.prefixService = prefixService;
    }

    // ================= PDF GENERATION =================
    @GetMapping("/pdf")
    public void generatePDF(HttpServletResponse response) throws IOException {

        List<Prefix> prefixes = prefixService.findAll();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=MedNet_Admin_Report.pdf");

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("MEDNET LABS ADMIN PANEL")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY));

            document.add(new Paragraph("123 Healthcare Avenue, Mumbai - 400001")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            document.add(new Paragraph("Report Generated On: " + timestamp)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setItalic());

            document.add(new Paragraph("\n"));

            float[] columnWidths = {1, 3, 2, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.useAllAvailableWidth();

            String[] headers = {"ID", "Prefix Name", "Gender", "Prefix Category"};

            for (String header : headers) {
                table.addHeaderCell(
                        new com.itextpdf.layout.element.Cell()
                                .add(new Paragraph(header).setBold())
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setTextAlignment(TextAlignment.CENTER)
                );
            }

            for (Prefix p : prefixes) {
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(p.getId()))));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(p.getPrefixName())));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(p.getGender())));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(p.getPrefixOf())));
            }

            document.add(table);

            document.add(new Paragraph("\n--- End of Official Record ---")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));
        }
    }

    // ================= EXCEL DOWNLOAD =================
    @GetMapping("/download")
    public void downloadExcel(HttpServletResponse response) throws IOException {

        List<Prefix> prefixes = prefixService.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("MedNet Prefixes");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Prefix Name", "Gender", "Prefix Of"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Prefix p : prefixes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getPrefixName());
                row.createCell(1).setCellValue(p.getGender());
                row.createCell(2).setCellValue(p.getPrefixOf());
            }

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=prefixes_export.xlsx");

            workbook.write(response.getOutputStream());
        }
     }

    // ================= EXCEL UPLOAD =================
    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file) {

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue;

                Prefix prefix = new Prefix();

                prefix.setPrefixName(getCellValue(row.getCell(0)));
                prefix.setGender(getCellValue(row.getCell(1)));
                prefix.setPrefixOf(getCellValue(row.getCell(2)));

                prefixService.save(prefix);
            }

            return "Upload Successful";

        } catch (Exception e) {
            return "Upload Failed: " + e.getMessage();
        }
    }

    private String getCellValue(Cell cell) {

        if (cell == null) return " ";

        CellType type = cell.getCellType();

        switch (type) {

            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((int) cell.getNumericCellValue());

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                return cell.getCellFormula();

            default:
                return "";
        }
    }

    // ================= CRUD =================
    @PostMapping
    public Prefix create(@RequestBody Prefix prefix) {
        return prefixService.save(prefix);
    }

    @GetMapping
    public List<Prefix> getAll() {
        return prefixService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        prefixService.delete(id);
    }
}
