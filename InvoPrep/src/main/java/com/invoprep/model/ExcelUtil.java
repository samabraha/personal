package com.invoprep.model;


import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.invoprep.model.Store.fillItems;

public class ExcelUtil {
    public static Store store = new Store("EriAbraha-2, Benfica", "EA2", "Fanuel Z.");

    public static void main(String[] args) throws IOException {

//        Store store = new Store("EA2");


        String listFileName = store.getInitials() + "/EA_COMPRASTIPODOC_All.xlsx";
        String itemsFileNamePrefix = store.getInitials() + "/EA_A1_CF_";

        System.out.println("Started InvoProp");

        var file = new File(listFileName);

        var wb = Store.getWorkbook(file);
        var sheet = wb.getSheetAt(0);
        System.out.println("Opened " + sheet.getSheetName());

        var list = store.getInvoiceList(sheet);

        fillItems(itemsFileNamePrefix, list);

        wb.close();

        Workbook workbook = WorkbookFactory.create(true);
        var sheetName = store.getInitials() + "_ItemInvoices";

        workbook.createSheet(sheetName);

        store.writeComboSheet(workbook.getSheet(sheetName), list).getHeader()
                .setCenter(sheetName);
        workbook.write(new FileOutputStream(store.getInitials() + "_output_file.xlsx"));
    }

}
