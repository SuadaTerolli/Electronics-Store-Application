package com.electronicstore.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Bill {
    private int billId;
    private String billNumber;
    private Date billDate;
    private ArrayList<BillItem> billItems;
    private double totalAmount;
    private int sectorId;
    private int totalQuantity;

    public Bill() {
    }

    public Bill(int billId, String billNumber, Date billDate, ArrayList<BillItem> billItems, double totalAmount, int sectorId) {
        this.billId = billId;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.billItems = billItems;
        this.totalAmount = totalAmount;
        this.sectorId = sectorId;
    }

    public Bill(int billId, String billNumber, Date billDate, int totalQuantity, double totalAmount) {
        this.billId = billId;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }

    public double calculateTotalAmount() {
        double total = 0.0;
        for (BillItem billItem : billItems) {
            total += billItem.getItem().getSellingPrice() * billItem.getItemQuantity();
        }
        this.totalAmount = total;
        return total;
    }
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(this.billDate);
    }
    public void setBillItems(ArrayList<BillItem> billItems) {
        this.billItems = billItems;
    }

    public ArrayList<BillItem> getBillItems() {
        return billItems;
    }

    public int getSectorId() {
        return sectorId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getBillId() {
        return billId;
    }

    public Date getBillDate() {
        return billDate;
    }

    public int getTotalQuantity() { // Getter for totalQuantity
        return totalQuantity;
    }
}
