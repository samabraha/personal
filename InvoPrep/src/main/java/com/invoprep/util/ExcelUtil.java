package com.invoprep.util;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class ExcelUtil {
    public static void main(String[] args) throws IOException {
        String listFileName = "RS_COMPRASTIPODOC_05-03-22_All.xlsx";
        String itemsFileNamePrefix = "RS_A1_CF_";

        System.out.println("Started InvoProp");

        var file = new File(listFileName);

        var wb = getWorkbook(file);
        if (wb != null) {
            var sheet = wb.getSheetAt(0);
            System.out.println("Opened " + sheet.getSheetName());

            var list = getInvoiceList(sheet);

            fillItems(itemsFileNamePrefix, list);

            System.out.println(list);
            wb.close();
            return;
        }

        System.out.println("WB was null");
    }

    private static void fillItems(String itemsFileNamePrefix, List<PurchaseInvoice> invoiceList)  {
        for (PurchaseInvoice invoice : invoiceList) {
            Sheet sheet = getSheet(itemsFileNamePrefix + invoice.getDocumentNumber() + ".xlsx");
            invoice.fillItems(sheet);
        }
    }

    private static Sheet getSheet(String fileName) throws IOException {
        try {
            return WorkbookFactory.create(new File(fileName)).getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<PurchaseInvoice> getInvoiceList(Sheet sheet) {
        var list = new ArrayList<PurchaseInvoice>();
        for (Row row : sheet) {
            if (row.getRowNum() > 0) {

                var date = row.getCell(0).getNumericCellValue();
                var docNum = (int) row.getCell(2).getNumericCellValue();
                var vendor = row.getCell(4).getStringCellValue();
                var description = row.getCell(5).getStringCellValue();
                var amount = row.getCell(6).getNumericCellValue();

                var invoice = new PurchaseInvoice(date, docNum, vendor, description, amount);
                list.add(invoice);
//                System.out.printf("%s %s %s %s %,.2f %n", getJavaDate(date), docNum, vendor, description, amount);

            }
        }
        return list;
    }

    public static Workbook getWorkbook(File file) {
        try {
            var workbook =  WorkbookFactory.create(file);
            System.out.println("Created workbook.");
            if (workbook != null) {
                return workbook;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }



}
