package com.example.splitwallet.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.compose.ui.text.font.Font;

import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.User;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PdfReportGenerator {
    private static final com.itextpdf.text.Font TITLE_FONT = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
    private static final com.itextpdf.text.Font SUBTITLE_FONT = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
    private static final com.itextpdf.text.Font NORMAL_FONT = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);
    private static final com.itextpdf.text.Font BOLD_FONT = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);

    public static File generateExpenseReport(Context context, String groupName,
                                             List<Expense> expenses,
                                             Map<String, User> members,
                                             Map<String, Double> balances) {
        // Create time-stamped filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "ExpenseReport_" + timeStamp + ".pdf";

        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "reports");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists()) dir.mkdirs();
        File pdfFile = new File(dir, fileName);

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Add title
            addTitle(document, "Expense Report - " + groupName);

            // Add summary section
            addSummarySection(document, balances);

            // Add expenses table
            addExpensesTable(document, expenses, members);

            document.close();
            return pdfFile;
        } catch (DocumentException | IOException e) {
            Log.e("PDF Generation", "Error generating PDF", e);
            return null;
        }
    }

    private static void addTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20f);
        document.add(p);
    }

    private static void addSummarySection(Document document, Map<String, Double> balances)
            throws DocumentException {
        Paragraph summary = new Paragraph("Summary of Balances", SUBTITLE_FONT);
        summary.setSpacingAfter(10f);
        document.add(summary);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);

        // Table headers
        addTableHeaderCell(table, "Participant");
        addTableHeaderCell(table, "Balance");

        // Add balance rows
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            addTableCell(table, entry.getKey());
            addTableCell(table, String.format(Locale.getDefault(), "%.2f ₽", entry.getValue()));
        }

        document.add(table);
    }

    private static void addExpensesTable(Document document, List<Expense> expenses,
                                         Map<String, User> members) throws DocumentException {
        Paragraph expensesTitle = new Paragraph("All Expenses", SUBTITLE_FONT);
        expensesTitle.setSpacingAfter(10f);
        document.add(expensesTitle);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // Table headers
        addTableHeaderCell(table, "Title");
        addTableHeaderCell(table, "Amount");
        addTableHeaderCell(table, "Paid By");
        addTableHeaderCell(table, "Date");
        addTableHeaderCell(table, "Description");

        // Add expense rows
        for (Expense expense : expenses) {
            addTableCell(table, expense.getName());
            addTableCell(table, String.format(Locale.getDefault(), "%.2f ₽", expense.getAmount()));

            User paidBy = members.get(expense.getUserWhoCreatedId());
            String paidByName = paidBy != null ? paidBy.getName() : "Unknown";
            addTableCell(table, paidByName);

            addTableCell(table, expense.getDate().toString());
            addTableCell(table, expense.getDescription() != null ? expense.getDescription() : "");
        }

        document.add(table);
    }

    private static void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BOLD_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setPadding(5f);
        table.addCell(cell);
    }
}