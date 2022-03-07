package com.invoprep.model;

public class Item {
    private final String itemCode;
    private final String description;
    private final double quantity;
    private final double unitCost;
    private final double totalCost;
    private final double sellingPrice;

    public Item(String itemCode, String description, double quantity,
                double unitCost, double totalCost, double sellingPrice) {

        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.sellingPrice = sellingPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getDescription() {
        return description;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    @Override
    public String toString() {
        return String.format("itemCode=%s, description=%s, quantity=%s, unitCost=%s, " +
                        "totalCost=%s, sellingPrice=%s", itemCode, description, quantity,
                unitCost, totalCost, sellingPrice);
    }

    public String getCodeString() {
        return String.format("%s%04d", itemCodePrefix(), Integer.valueOf(getItemCode()));
    }

    // TODO
    private char itemCodePrefix() {
        return 'B';
    }
}
