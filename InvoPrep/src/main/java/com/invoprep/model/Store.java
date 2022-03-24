package com.invoprep.model;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** */
public class Store {
    private final String storeName;
    private final String initials;
    private final String manager;
    private final String filePrefix;
    private final String dataRoot;
    private static final String fileExt = ".xlsx";

    private static final String INVOICE_LIST_FILENAME = "_COMPRASTIPODOC_All" + fileExt;
    private static final String INVOICE_FILENAME_INFIX = "_A1_CF_";
    private final String fileSuffix = fileExt;

    private final Map<String, String> vendorAccNames;

    /** Returns new instance of Store */
    public Store(String storeName, String initials, String manager, String filePrefix, String dataRoot) {
        this.storeName = storeName;
        this.initials = initials;
        this.manager = manager;
        this.filePrefix = filePrefix;
        this.dataRoot = dataRoot;

        vendorAccNames = getVendorNameAccMapping();
    }


    private Map<String, String> getVendorNameAccMapping() {
        String mappingFileName = Path.of(dataRoot, "mapping" + fileExt).toString();
        Map<String, String> nameMapping = new HashMap<>();

        var workbook = getWorkbook((Path.of("VendorNameMapping.xlsx").toFile()));

        var sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            var name = row.getCell(1).getStringCellValue();
            var value = row.getCell(2).getStringCellValue();

            if (!(name.isBlank() || value.isBlank())) {
                nameMapping.put(name, value);
            }
        }

        sheet.getHeader();

        return nameMapping;
    }

    /**  */
    public Sheet writeComboSheet(Sheet sheet, List<PurchaseInvoice> invoiceList) {
        long start = System.currentTimeMillis();

        var workbook = sheet.getWorkbook();

        var currencyCellStyle = workbook.createCellStyle();
        currencyCellStyle.setDataFormat((short) 0x4);

        var dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat((short) 0xf);
        dateCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        dateCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        var columnHeadings = List.of(
                "Ref", "Date", "Vendor", "Description", "Item Code", "Item Name", "Quantity",
                "Unit Cost", "Total Cost", "Invoice Amount", "Selling Price", "Remark");
        writeTableHeaders(row, columnHeadings);

        for (PurchaseInvoice invoice : invoiceList) {
            row = sheet.createRow(sheet.getLastRowNum() + 1);

            writeInvoiceData(invoice, row);

            var items = invoice.getItemList();

            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    row = sheet.createRow(sheet.getLastRowNum() + 1);
                }

                Item item = items.get(i);
                writeItemData(invoice, row, item);

            }
        }

        for (int i = 0; i < 13; i++) {
            sheet.autoSizeColumn(i);
        }

        long end = System.currentTimeMillis();
        System.out.printf("Writing sheet took %d milliseconds%n", end - start);
        return sheet;
    }

    /** */
    void writeInvoiceData(PurchaseInvoice invoice, Row row) {
        Cell cell = row.createCell(1, CellType.STRING);
        var invoiceDate = invoice.getDate();
//        var dateString = "%02d-%02d-%d".formatted(
//                invoiceDate.getDayOfMonth(), invoiceDate.getMonthValue(), invoiceDate.getYear());
        cell.setCellValue(invoiceDate);
//        cell.setCellStyle(dateCellStyle);


        cell = row.createCell(2, CellType.STRING);
        var vendorName = getVendorAccName(invoice.getVendor());
        cell.setCellValue(vendorName);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue(invoice.getDescription());

        cell = row.createCell(9, CellType.NUMERIC);
        cell.setCellValue(invoice.getAmount());
//        cell.setCellStyle(currencyCellStyle);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue(invoice.getRemark());
    }

    /** */
    void writeItemData(PurchaseInvoice invoice, Row row, Item item) {
        Cell cell = row.createCell(0, CellType.STRING);
        cell.setCellValue(invoice.getInvoiceRef());

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue(item.getCodeString());

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue(item.description());

        cell = row.createCell(6, CellType.NUMERIC);
        cell.setCellValue(item.quantity());

        cell = row.createCell(7, CellType.NUMERIC);
        cell.setCellValue(item.unitCost());
//        cell.setCellStyle(currencyCellStyle);

        cell = row.createCell(8, CellType.NUMERIC);
        cell.setCellValue(item.totalCost());
//        cell.setCellStyle(currencyCellStyle);

        cell = row.createCell(10, CellType.NUMERIC);
        cell.setCellValue(item.sellingPrice());
//        cell.setCellStyle(currencyCellStyle);
    }


    private String getVendorAccName(String vendor) {
        System.out.println(vendor + " -> " + vendorAccNames.get(vendor));
        return vendorAccNames.get(vendor) != null ? vendorAccNames.get(vendor) : vendor;
    }

    /** Returns */
    static Row writeTableHeaders(Row row, List<String> headers) {
        for (int i = 0; i < headers.size(); i++) {
            row.createCell(i).setCellValue(headers.get(i));
        }
        return row;
    }

    /** Returns */
    void fillItems(List<PurchaseInvoice> invoiceList) {
        for (PurchaseInvoice invoice : invoiceList) {
            String fileName = getInvoiceFileName(invoice.getDocumentNumber());
            var file = Path.of(fileName).toFile();
            if (file.exists()) {
                Sheet sheet = getFirstSheet( getWorkbook(file));
                invoice.fillItems(sheet);
            }
        }
    }

    /** Returns */
    public static Workbook getWorkbook(File file) {
        try (var workbook =  WorkbookFactory.create(file)) {
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

    /** Returns */
    private static Sheet getFirstSheet(Workbook workbook) {
        return workbook.getSheetAt(0);
    }

    /** Returns */
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

    /** Returns the contents of a <code>CellType.NUMERIC</code> and <code>CellType.STRING</code>, as String */
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

    /** Returns the manager's name */
    public String getManager() {
        return manager;
    }

    /** Returns the Store initials */
    public String getInitials() {
        return initials;
    }

    /** Returns the storeName */
    public Object getStoreName() {
        return storeName;
    }

    /** Returns the filePrefix */
    public String getFilePrefix() {
        return filePrefix.toUpperCase();
    }

    /** Returns the invoiceListFileName */
    public String getInvoiceListFilename() {
        return Path.of(getStoreDirectory(),
                getFilePrefix() + INVOICE_LIST_FILENAME).toString();
    }

    /** Returns the dataRoot path as String */
    public String getDataRoot() {
        return dataRoot;
    }

    /** Returns itemListFileNameInfix */
    public String getINVOICE_FILENAME_INFIX() {
        return INVOICE_FILENAME_INFIX;
    }

    /** Returns String of the Path of itemFileName created by concatenating
     * Store dataRoot, initials, filePrefix, itemListFileNameInfix, docNumber and fileSuffix */
    public String getInvoiceFileName(int docNumber) {
        return Path.of(getStoreDirectory(),
                getFilePrefix() + getINVOICE_FILENAME_INFIX() + docNumber + fileSuffix).toString();
    }

    public String getStoreDirectory() {
        return Path.of(getDataRoot(), getInitials() + "_CF").toString();
    }
}
