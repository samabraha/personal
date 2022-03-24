package com.invoprep.model;


import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import java.util.List;

/** InvoPrep launcher */
public class InvoPrep {

    public static void main(String[] args){
        System.out.println("Starting...");

        String name = "Fanuel Z.";
        String dataRoot = Path.of("F:/EACG-0033/SAC").toString();
        String mappingFilename = "VendorNameMapping";

        Store STORE_RDA = new Store(
                "RosaDojo, Benfica", "RDA", name, "RS", dataRoot, mappingFilename);
        processWorkbooks(STORE_RDA);

        Store STORE_EA2 = new Store(
                "EriAbraha-2, Benfica", "EA2", name, "EA", dataRoot, mappingFilename);
        processWorkbooks(STORE_EA2);

        System.out.println("Thank you for using InvoPrep.");
    }

    private static void processWorkbooks(Store store) {
        var rootPath = Path.of(store.getStoreDirectory());

        System.out.println("Processing " + store.getStoreName() + " files.");

        var file = new File(store.getInvoiceListFilename());
        Sheet sheet;

        try (var workbook = Store.getWorkbook(file)) {
            if (workbook.getNumberOfSheets() == 0) {
                System.out.println("Workbook does not have any sheets.");
            }

            sheet = workbook.getSheetAt(0);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        List<PurchaseInvoice> invoiceList = store.getInvoiceList(sheet);
        System.out.printf("Processing %d invoices.%n", invoiceList.size());
        store.fillItems(invoiceList);
        System.out.println("Done");
        Workbook workbook;

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

        String tmpFileName = "new_file";
        try (workbook; var fos = new FileOutputStream(tmpFileName)) {
            workbook.write(fos);

            var outputFilename = "%s_%s_%s.xlsx".formatted(store.getFilePrefix(), "CPL", LocalDate.now());
            Path outputPath = Path.of(rootPath.toString(), outputFilename);
            if (outputPath.toFile().exists()) {
                Files.delete(outputPath);
            }

            fos.close();
            Files.move(Path.of(tmpFileName), outputPath);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
