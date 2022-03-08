package com.invoprep.model;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** */
public class Store {
    private final String storeName;
    private final String initials;
    private final String manager;
    private final String filePrefix;
    private final String dataRoot;

    private final String invoiceListFileName = "_COMPRASTIPODOC_All.xlsx";
    private String itemListFileNameInfix = "_A1_CF_";
    private String fileSuffix = ".xlsx";

    /** */
    public Store(String storeName, String initials, String manager, String filePrefix, String dataRoot) {
        this.storeName = storeName;
        this.initials = initials;
        this.manager = manager;
        this.filePrefix = filePrefix;
        this.dataRoot = dataRoot;
    }

    /** */
    public Sheet writeComboSheet(Sheet sheet, List<PurchaseInvoice> invoiceList) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        var columnHeadings = List.of("Ref", "Date", "Vendor", "Description",
                "Item Code", "Item Name", "Quantity", "Unit Cost", "Total Cost", "Invoice Amount", "Selling Price", "Remark");
        writeTableHeaders(row, columnHeadings);

        for (PurchaseInvoice invoice : invoiceList) {
            row = sheet.createRow(sheet.getLastRowNum() + 1);

            row.createCell(0).setCellValue(invoice.getInvoiceRef());
            row.createCell(1).setCellValue(invoice.getDate());
            row.createCell(2).setCellValue(invoice.getVendor());
            row.createCell(3).setCellValue(invoice.getDescription());
            row.createCell(9).setCellValue(invoice.getAmount());
            row.createCell(11).setCellValue(invoice.getRemark());

            var items = invoice.getItemList();

            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    row = sheet.createRow(sheet.getLastRowNum() + 1);
                }

                Item item = items.get(i);

                row.createCell(4).setCellValue(item.getCodeString());
                row.createCell(5).setCellValue(item.description());
                row.createCell(6).setCellValue(item.quantity());
                row.createCell(7).setCellValue(item.unitCost());
                row.createCell(8).setCellValue(item.totalCost());
                row.createCell(10).setCellValue(item.sellingPrice());
            }
        }

        return sheet;
    }

    /** */
    static Row writeTableHeaders(Row row, List<String> headers) {
        for (int i = 0; i < headers.size(); i++) {
            row.createCell(i).setCellValue(headers.get(i));
        }
        return row;
    }

    /** */
    void fillItems(List<PurchaseInvoice> invoiceList) {
        for (PurchaseInvoice invoice : invoiceList) {
            String fileName = getItemFileName(invoice.getDocumentNumber());
            var file = Path.of(fileName).toFile();
            if (file.exists()) {
                Sheet sheet = getFirstSheet( getWorkbook(file));
                invoice.fillItems(sheet);
            }
        }
    }

    /** */
    public static Workbook getWorkbook(File file) {
        try (var workbook =  WorkbookFactory.create(file)) {
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

    /** */
    private static Sheet getFirstSheet(Workbook workbook) {
        return workbook.getSheetAt(0);
    }

    /** */
    public List<PurchaseInvoice> getInvoiceList(Sheet sheet) {
        List<PurchaseInvoice> invoiceList = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() > 0) {

                var date = row.getCell(0).getDateCellValue();
                var docNum = (int) row.getCell(2).getNumericCellValue();
                var vendor = getCellValue(row, 4);
                var description = getCellValue(row, 5);
                var amount = row.getCell(6).getNumericCellValue();

                var invoice = new PurchaseInvoice(this, date, docNum, vendor, description, amount);
                invoiceList.add(invoice);
            }
        }
        return invoiceList;
    }

    /** */
    public String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        String value = "";
        if (cell == null) {
            return value;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            value = String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            value = cell.getStringCellValue();
        } else {
            System.out.println("Cell either empty or unknown cell type.");
        }

        return value;
    }

    /** */
    public String getManager() {
        return manager;
    }

    /** */
    public String getInitials() {
        return initials;
    }

    /** */
    public Object getStoreName() {
        return storeName;
    }

    /** */
    public String getFilePrefix() {
        return filePrefix.toUpperCase();
    }

    /** */
    public String getInvoiceListFileName() {
        return Path.of(dataRoot, getInitials(), filePrefix + invoiceListFileName).toString();
    }

    /** */
    public String getDataRoot() {
        return dataRoot;
    }

    /** */
    public String getItemListFileNameInfix() {
        return itemListFileNameInfix;
    }

    /** */
    public String getItemFileName(int docNumber) {
        return Path.of(getDataRoot(), getInitials(), filePrefix + getItemListFileNameInfix() + docNumber + fileSuffix).toString();
    }
}
