package ca.seneca.application.export;

import ca.seneca.application.model.Payment;
import ca.seneca.application.model.Reservation;
import ca.seneca.application.model.ReservationRoom;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.opencsv.CSVWriter;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Cross-cutting concern: exports reservation data to CSV or PDF.
 */
public class ReportExporter {

    private ReportExporter() {}

    // ── CSV ────────────────────────────────────────────────────────────────

    public static void exportRevenueCSV(List<Reservation> reservations, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{
                    "Reservation ID", "Guest", "Rooms", "Check-In", "Check-Out",
                "Nights", "Subtotal", "Discount", "Total", "Paid", "Balance", "Status"
            });
            for (Reservation r : reservations) {
                writer.writeNext(new String[]{
                        String.valueOf(r.getReservationId()),
                        getGuestName(r),
                        getRoomSummary(r),
                        safeDate(r.getCheckInDate()),
                        safeDate(r.getCheckOutDate()),
                        String.valueOf(getNights(r)),
                        money(r.getSubTotal()),
                        money(r.getDiscountAmount()),
                        money(r.getTotalAmount()),
                        money(getPaidAmount(r)),
                        money(getBalance(r)),
                        r.getStatus() != null ? r.getStatus().toString() : ""
                });
            }
        }
    }
// --- CSV: Occupancy -------------------------------------------------------
    public static void exportOccupancyCSV(List<Reservation> reservations, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{
                    "Reservation ID", "Room Number", "Room Type", "Check-In", "Check-Out", "Nights", "Guest", "Status"
            });
            for (Reservation r : reservations) {
                if (r.getReservationRooms() != null || r.getReservationRooms().isEmpty()) {
                    writer.writeNext(new String[]{
                            String.valueOf(r.getReservationId()),
                            "", "",
                            safeDate(r.getCheckInDate()),
                            safeDate(r.getCheckOutDate()),
                            getGuestName(r),
                            r.getStatus() != null ? r.getStatus().toString() : ""
                    });
                    continue;
                }
                for (ReservationRoom rr : r.getReservationRooms()) {
                    writer.writeNext(new String[]{
                            String.valueOf(r.getReservationId()),
                            rr.getRoom() != null ? rr.getRoom().getRoomNumber() : "",
                            rr.getRoom() != null && rr.getRoom().getRoomType() != null ? rr.getRoom().getRoomType().toString() : "",
                            safeDate(r.getCheckInDate()),
                            safeDate(r.getCheckOutDate()),
                            getGuestName(r),
                            r.getStatus() != null ? r.getStatus().toString() : ""
                    });
                }
            }
        }
    }

    // ── PDF ────────────────────────────────────────────────────────────────

    public static void exportRevenuePDF(List<Reservation> reservations, String filePath)
            throws DocumentException, IOException {

        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
        doc.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        doc.add(new Paragraph("Hotel Reservation Revenue Report", titleFont));
        doc.add(new Paragraph("Generated: " + LocalDate.now(), cellFont));
        doc.add(Chunk.NEWLINE);

        double totalRevenue = reservations.stream()
                .map(Reservation::getTotalAmount)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        double totalPaid = reservations.stream()
                .mapToDouble(ReportExporter::getPaidAmount)
                .sum();

        doc.add(new Paragraph(
                "Total Revenue: $" + String.format("%.2f", totalRevenue) +
                        "   |   Total Collected: $" + String.format("%.2f", totalPaid),
                headerFont
        ));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2.3f, 2.0f, 1.4f, 1.4f, 1.3f, 1.3f, 1.4f});

        for (String h : new String[]{"ID", "Guest", "Rooms", "Check-In", "Check-Out", "Total", "Paid", "Status"}) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (Reservation r : reservations) {
            table.addCell(new Phrase(String.valueOf(r.getReservationId()), cellFont));
            table.addCell(new Phrase(getGuestName(r), cellFont));
            table.addCell(new Phrase(getRoomSummary(r), cellFont));
            table.addCell(new Phrase(safeDate(r.getCheckInDate()), cellFont));
            table.addCell(new Phrase(safeDate(r.getCheckOutDate()), cellFont));
            table.addCell(new Phrase("$" + String.format("%.2f", safeDouble(r.getTotalAmount())), cellFont));
            table.addCell(new Phrase("$" + String.format("%.2f", getPaidAmount(r)), cellFont));
            table.addCell(new Phrase(r.getStatus() != null ? r.getStatus().toString() : "", cellFont));
        }

        doc.add(table);
        doc.close();
    }

    // ---Helpers--------------------------------------------------------
    private static String getGuestName(Reservation reservation) {
        if (reservation.getGuest() == null) return "";
        String first = reservation.getGuest().getFirstName() != null ? reservation.getGuest().getFirstName() : "";
        String last = reservation.getGuest().getLastName() != null ? reservation.getGuest().getLastName() : "";
        return (first + " " + last).trim();
    }

    private static String getRoomSummary(Reservation reservation) {
        if (reservation.getReservationRooms() == null || reservation.getReservationRooms().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (ReservationRoom rr : reservation.getReservationRooms()) {
            if (rr.getRoom() == null) continue;

            if (sb.length() > 0) sb.append(", ");

            sb.append(rr.getRoom().getRoomNumber());
            if (rr.getRoom().getRoomType() != null) {
                sb.append(" (").append(rr.getRoom().getRoomType()).append(")");
            }
        }
        return sb.toString();
    }

    private static long getNights(Reservation reservation) {
        if (reservation.getCheckInDate() == null || reservation.getCheckOutDate() == null) return 0;
        return ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
    }

    private static double getPaidAmount(Reservation reservation) {
        if (reservation.getPayments() == null) return 0.0;

        double total = 0.0;
        for (Payment payment : reservation.getPayments()) {
            if (payment.getAmount() != null) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    private static double getBalance(Reservation reservation) {
        return safeDouble(reservation.getTotalAmount()) - getPaidAmount(reservation);
    }

    private static String safeDate(LocalDate date) {
        return date != null ? date.toString() : "";
    }

    private static double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    private static String money(Double value) {
        return String.format("%.2f", safeDouble(value));
    }

    private static String money(double value) {
        return String.format("%.2f", value);
    }
}
