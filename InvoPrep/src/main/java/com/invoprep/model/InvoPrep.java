package com.invoprep.model;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** InvoPrep launcher */
public class InvoPrep {
    public static Store STORE_RDA = new Store("RosaDojo, Benfica", "RDA", "Fanuel Z.", "RS", "data");
    public static Store STORE_EA2 = new Store("EriAbraha-2, Benfica", "EA2", "Fanuel Z.", "EA", "data");

    /** */
    public static void main(String[] args) {
//        Store store = STORE_EA2;
         Store store = STORE_RDA;

        var rootPath = Path.of(store.getDataRoot(), store.getInitials(), store.getFilePrefix());

        System.out.println("Started InvoPrep with " + store.getStoreName());

        var file = new File(store.getInvoiceListFileName());

        Sheet sheet;

        List<PurchaseInvoice> invoiceList;
        try (var workbook = Store.getWorkbook(file)) {
            if (workbook.getNumberOfSheets() == 0) {
                System.out.println("Workbook does not have any sheets.");
            }

            sheet = workbook.getSheetAt(0);
            System.out.println("Opened " + sheet.getSheetName());
            invoiceList = store.getInvoiceList(sheet);
            store.fillItems(invoiceList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Workbook workbook;
        Path outputPath = Path.of(rootPath + "_output_file.xlsx");
//        File outFile = outputPath.toFile();
        try {
//            if (outFile.exists() && outFile.length() > 0) {
//                workbook = WorkbookFactory.create(outFile);
//            } else {
                workbook = WorkbookFactory.create(true);
//            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("Could not initialize workbook.");
        }

        var sheetName = store.getInitials() + "_ItemInvoices";


        Sheet outSheet = workbook.getSheet(sheetName);
        if (outSheet == null) {
            outSheet = workbook.createSheet(sheetName);
        }

        store.writeComboSheet(outSheet, invoiceList);
        sheet.getHeader().setCenter(sheetName);
        sheet.getHeader().setRight(LocalDate.now().toString());

        try (workbook; var fos = new FileOutputStream("new_file")) {
            workbook.write(fos);

            if (outputPath.toFile().exists()) {
                Files.delete(outputPath);
            }
            Files.move(Path.of("new_file"), outputPath);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
