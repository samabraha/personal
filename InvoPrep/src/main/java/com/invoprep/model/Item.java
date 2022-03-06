package com.invoprep.model;

public class Item {
    private double itemCode;
    private String description;
    private double quantity;
    private double unitCost;
    private double totalCost;
    private double sellingPrice;

    public Item(double itemCode, String description, double quantity,
                double unitCost, double totalCost, double sellingPrice) {

        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.sellingPrice = sellingPrice;
    }

    @Override
    public String toString() {
        return String.format("itemCode=%s, description=%s, quantity=%s, unitCost=%s, " +
                        "totalCost=%s, sellingPrice=%s", itemCode, description, quantity,
                unitCost, totalCost, sellingPrice);
    }
}
