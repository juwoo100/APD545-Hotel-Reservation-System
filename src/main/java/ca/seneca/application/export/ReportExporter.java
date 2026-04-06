package com.hotel.export;

import com.hotel.model.ReservationEntity;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.opencsv.CSVWriter;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Cross-cutting concern: exports reservation data to CSV or PDF.
 */
public class ReportExporter {

    private ReportExporter() {}

    // ── CSV ────────────────────────────────────────────────────────────────

    public static void exportRevenueCSV(List<ReservationEntity> reservations, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{"Res. ID", "Guest", "Room", "Check-In", "Check-Out",
                "Nights", "Room Subtotal", "Add-Ons", "Discount", "Tax", "Total", "Paid", "Balance", "Status"});
            for (ReservationEntity r : reservations) {
                writer.writeNext(new String[]{
                    String.valueOf(r.getId()),
                    r.getGuest().getFullName(),
                    r.getRoom().getRoomNumber() + " (" + r.getRoom().getRoomType() + ")",
                    r.getCheckInDate().toString(),
                    r.getCheckOutDate().toString(),
                    String.valueOf(r.getNights()),
                    String.format("%.2f", r.getRoomSubtotal()),
                    r.getAddOns() != null ? r.getAddOns() : "",
                    String.format("%.2f", r.getDiscountAmount()),
                    String.format("%.2f", r.getTax()),
                    String.format("%.2f", r.getTotalAmount()),
                    String.format("%.2f", r.getPaidAmount()),
                    String.format("%.2f", r.getBalance()),
                    r.getStatus().toString()
                });
            }
        }
    }

    public static void exportOccupancyCSV(List<ReservationEntity> reservations, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{"Room Number", "Room Type", "Check-In", "Check-Out", "Nights", "Guest", "Status"});
            for (ReservationEntity r : reservations) {
                writer.writeNext(new String[]{
                    r.getRoom().getRoomNumber(),
                    r.getRoom().getRoomType().toString(),
                    r.getCheckInDate().toString(),
                    r.getCheckOutDate().toString(),
                    String.valueOf(r.getNights()),
                    r.getGuest().getFullName(),
                    r.getStatus().toString()
                });
            }
        }
    }

    // ── PDF ────────────────────────────────────────────────────────────────

    public static void exportRevenuePDF(List<ReservationEntity> reservations, String filePath) throws DocumentException, IOException {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
        doc.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font cellFont  = FontFactory.getFont(FontFactory.HELVETICA, 8);

        doc.add(new Paragraph("Sheraton Hotel — Revenue Report", titleFont));
        doc.add(new Paragraph("Generated: " + LocalDate.now(), cellFont));
        doc.add(Chunk.NEWLINE);

        double totalRevenue = reservations.stream().mapToDouble(ReservationEntity::getTotalAmount).sum();
        double totalPaid    = reservations.stream().mapToDouble(ReservationEntity::getPaidAmount).sum();
        doc.add(new Paragraph("Total Revenue: $" + String.format("%.2f", totalRevenue) +
            "   |   Total Collected: $" + String.format("%.2f", totalPaid), headerFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});

        for (String h : new String[]{"ID","Guest","Room","Check-In","Check-Out","Total","Paid","Status"}) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
        for (ReservationEntity r : reservations) {
            table.addCell(new Phrase(String.valueOf(r.getId()), cellFont));
            table.addCell(new Phrase(r.getGuest().getFullName(), cellFont));
            table.addCell(new Phrase(r.getRoom().getRoomNumber(), cellFont));
            table.addCell(new Phrase(r.getCheckInDate().toString(), cellFont));
            table.addCell(new Phrase(r.getCheckOutDate().toString(), cellFont));
            table.addCell(new Phrase("$" + String.format("%.2f", r.getTotalAmount()), cellFont));
            table.addCell(new Phrase("$" + String.format("%.2f", r.getPaidAmount()), cellFont));
            table.addCell(new Phrase(r.getStatus().toString(), cellFont));
        }
        doc.add(table);
        doc.close();
    }
}
