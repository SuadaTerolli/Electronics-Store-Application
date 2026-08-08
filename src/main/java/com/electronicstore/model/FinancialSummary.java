package com.electronicstore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class FinancialSummary implements Serializable {
    private double totalIncome;
    private double totalCosts;
    private double salaries;
    private double itemsPurchasedPrice;
    private Date startDate;
    private Date endDate;
    private ArrayList<User> users;
    private ArrayList<Item> items;
    private ArrayList<Bill> bills;

    public FinancialSummary() {
    }

    public FinancialSummary(Date startDate, Date endDate, double totalIncome, double totalCosts, double salaries, double itemsPurchasedPrice) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalCosts = totalCosts;
        this.salaries = salaries;
        this.itemsPurchasedPrice = itemsPurchasedPrice;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalCosts() {
        return totalCosts;
    }

    public void setTotalCosts(double totalCosts) {
        this.totalCosts = totalCosts;
    }

    public double getSalaries() {
        return salaries;
    }

    public void setSalaries(double salaries) {
        this.salaries = salaries;
    }

    public double getItemsPurchasedPrice() {
        return itemsPurchasedPrice;
    }

    public void setItemsPurchasedPrice(double itemsPurchasedPrice) {
        this.itemsPurchasedPrice = itemsPurchasedPrice;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Bill> getBills() {
        return bills;
    }

    public void setBills(ArrayList<Bill> bills) {
        this.bills = bills;
    }

    public double viewTotalCosts() {
        totalCosts = 0;
        for (User singleUser : users) {
            totalCosts += singleUser.getSalary();
        }
        for (Item item : items) {
            totalCosts += (item.getPurchasePrice() * item.getQuantity());
        }
        return totalCosts;
    }

    public double viewTotalIncome() {
        totalIncome = 0;
        for (Bill bill : bills) {
            totalIncome += bill.calculateTotalAmount();
        }
        return totalIncome;
    }

    public double viewSalaries() {
        salaries = 0;
        for (User singleUser : users) {
            salaries += singleUser.getSalary();
        }
        return salaries;
    }

    public double viewItemsPurchasedPrice() {
        itemsPurchasedPrice = 0;
        for (Item item : items) {
            itemsPurchasedPrice += item.getPurchasePrice();
        }
        return itemsPurchasedPrice;
    }
}
