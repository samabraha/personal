package com.invoprep.util;

import com.invoprep.model.Invoice;
import com.invoprep.model.PurchaseInvoice;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.poi.ss.usermodel.DateUtil.getJavaDate;

public class ExcelUtil {
    public static void main(String[] args) throws IOException {
        String listFileName = "RS_COMPRASTIPODOC_05-03-22_All.xlsx";
        String itemsFileNamePrefix = "RS_A1_CF_";

        System.out.println("Started InvoProp");

        var file = new File(listFileName);

        var wb = getWorkbook(file);
        if (wb != null) {
            var sheet = wb.getSheetAt(0);
            System.out.println(sheet.getSheetName());


            wb.close();
            return;
        }

        System.out.println("WB was null");
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
                System.out.printf("%s %s %s %s %,.2f %n", getJavaDate(date), docNum, vendor, description, amount);

            } else {
                System.out.println("Date Doc Num  Vendor  Descr  amount");
            }
        }
    }

    public static List<PurchaseInvoice> getCompleteInvoice(Sheet invoiceList, Sheet itemList) {
        var invoices = getInvoiceList(invoiceList);


        return null;
    }

    public static List<PurchaseInvoice> fillItems(Sheet itemSheet) {

        return fillItems(new ArrayList<PurchaseInvoice>(), itemSheet);
    }

    private static List<PurchaseInvoice> fillItems(ArrayList<PurchaseInvoice> invoices, Sheet itemSheet) {
        return null;
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
