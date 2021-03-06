package com.invoprep.model;

import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

/**
 *
 */
public record Item(String itemCode, String description, double quantity, double unitCost,
                   double totalCost, double sellingPrice) {

    public static Item getItem(Row row) {
        var itemCode = row.getCell(0).getStringCellValue();
        var description = row.getCell(1).getStringCellValue();
        var quantity = row.getCell(2).getNumericCellValue();
        var unitCost = row.getCell(3).getNumericCellValue();
        var totalCost = row.getCell(8).getNumericCellValue();
        var sellingPrice = row.getCell(14).getNumericCellValue();

        if (itemCode == null || itemCode.equals("")) {
            return null;
        }

        return new Item(itemCode, description, quantity, unitCost, totalCost, sellingPrice);
    }

    @Override
    public String toString() {
        return String.format("itemCode=%s, description=%s, quantity=%s, unitCost=%s, " +
                        "totalCost=%s, sellingPrice=%s", itemCode, description, quantity,
                unitCost, totalCost, sellingPrice);
    }

    public String getCodeString() {
        return String.format("%s%04d", itemCodePrefix(), Integer.valueOf(itemCode()));
    }

    // TODO
    private char itemCodePrefix() {
        return 'B';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Item) obj;
        return Objects.equals(this.itemCode, that.itemCode) &&
                Objects.equals(this.description, that.description) &&
                Double.doubleToLongBits(this.quantity) == Double.doubleToLongBits(that.quantity) &&
                Double.doubleToLongBits(this.unitCost) == Double.doubleToLongBits(that.unitCost) &&
                Double.doubleToLongBits(this.totalCost) == Double.doubleToLongBits(that.totalCost) &&
                Double.doubleToLongBits(this.sellingPrice) == Double.doubleToLongBits(that.sellingPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemCode, description, quantity, unitCost, totalCost, sellingPrice);
    }

}
