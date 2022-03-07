package com.invoprep.model;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Store {
    private final String storeName;
    private final String initials;
    private String manager;

    public Store(String storeName, String initials, String manager) {
        this.storeName = storeName;
        this.initials = initials;
        this.manager = manager;
    }

    public Sheet writeComboSheet(Sheet sheet, List<PurchaseInvoice> list) {
        Row row;

        for (PurchaseInvoice invoice : list) {
            row = sheet.createRow(sheet.getLastRowNum() + 1);

            row.createCell(1).setCellValue(invoice.getDate());
            row.createCell(2).setCellValue(invoice.getVendor());
            row.createCell(3).setCellValue(invoice.getDescription());
            row.createCell(4).setCellValue(invoice.getAmount());
            row.createCell(5).setCellValue(invoice.getRemark());

            var items = invoice.getItemList();

            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    row = sheet.createRow(sheet.getLastRowNum() + 1);
                }

                row.createCell(0).setCellValue(invoice.getInvoiceRef());
                Item item = items.get(i);

                row.createCell(6).setCellValue(item.getCodeString());
                row.createCell(7).setCellValue(item.getDescription());
                row.createCell(8).setCellValue(item.getQuantity());
                row.createCell(9).setCellValue(item.getUnitCost());
                row.createCell(10).setCellValue(item.getTotalCost());
                row.createCell(11).setCellValue(item.getSellingPrice());
            }
        }

        return sheet;
    }

    static void fillItems(String itemsFileNamePrefix, List<PurchaseInvoice> invoiceList) {
        System.out.println("Starting fill " + "-".repeat(20));
        for (PurchaseInvoice invoice : invoiceList) {
            System.out.println(invoice);
            String fileName = itemsFileNamePrefix + invoice.getDocumentNumber() + ".xlsx";
            var file = Path.of(fileName).toFile();
            if (file.exists()) {
                Sheet sheet = getFirstSheet( getWorkbook(file));
                invoice.fillItems(sheet);
            }
        }
    }

    public static Workbook getWorkbook(File file) {
        try {
            var workbook =  WorkbookFactory.create(file);
            System.out.println("Created workbook: " + file.getName());
            if (workbook != null) {
                return workbook;
            } else {
                throw new RuntimeException("Workbook was null.");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Could not get workbook from file.");
        }
    }

    private static Sheet getFirstSheet(Workbook workbook) {
        return workbook.getSheetAt(0);
    }

    public List<PurchaseInvoice> getInvoiceList(Sheet sheet) {
        var myList = new ArrayList<PurchaseInvoice>();
        for (Row row : sheet) {
            if (row.getRowNum() > 0) {

                var date = row.getCell(0).getNumericCellValue();
                var docNum = (int) row.getCell(2).getNumericCellValue();
                var vendor = row.getCell(4).getStringCellValue();
                var description = row.getCell(5).getStringCellValue();
                var amount = row.getCell(6).getNumericCellValue();

                var invoice = new PurchaseInvoice(date, docNum, vendor, description, amount);
                myList.add(invoice);
//                System.out.printf("%s %s %s %s %,.2f %n", getJavaDate(date), docNum, vendor, description, amount);
            }
        }
        return myList;
    }

    public String getManager() {
        return manager;
    }

    public String getInitials() {
        return initials;
    }

    public Object getStoreName() {
        return storeName;
    }
}
