package com.electronicstore.model;


public class CashierPerformance {
    private final String cashierId;
    private int totalBills;
    private int itemsSold;
    private double totalRevenue;

    public CashierPerformance(String cashierId) {
        this.cashierId = cashierId;
        this.totalBills = 0;
        this.itemsSold = 0;
        this.totalRevenue = 0.0;
    }

    public String getCashierId() {
        return cashierId;
    }

    public int getTotalBills() {
        return totalBills;
    }

    public void incrementBills() {
        this.totalBills++;
    }

    public int getItemsSold() {
        return itemsSold;
    }

    public void addItemsSold(int items) {
        this.itemsSold += items;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void addRevenue(double revenue) {
        this.totalRevenue += revenue;
    }
}