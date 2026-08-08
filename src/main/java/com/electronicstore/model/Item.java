package com.electronicstore.model;

import java.util.Date;
import java.util.ArrayList;
public class Item {
    private int itemId;
    private String name;
    private Category category;
    private Supplier supplier;
    private Date purchaseDate;
    private double purchasePrice;
    private double sellingPrice;
    private int quantity;
    private int sectorId;

    public Item(){

    }
    public Item(int itemId, String name, Category category, Supplier supplier, Date purchaseDate, double purchasePrice, double sellingPrice, int quantity){
        this.itemId=itemId;
        this.name=name;
        this.category = category;
        this.supplier = supplier;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        
    }
    public Item(int itemId, String name, Category category, Supplier supplier, Date purchaseDate, double purchasePrice, double sellingPrice, int quantity,int sectorId){
        this.itemId=itemId;
        this.name=name;
        this.category = category;
        this.supplier = supplier;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.sectorId=sectorId;

    }
    public void reduceQuantity(int quantity){
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
        } else {
            System.out.println("Reduction failed. There are only  " + this.quantity + " items available in stock.");
        }

    }
    public ArrayList<Item> getStockLevelsBelowFive(){
        ArrayList<Item> items = new ArrayList<>();
        if (this.quantity < 5) {
            items.add(this);
        }
        return items;

    }
    
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public int getItemId(){
        return itemId;
    }
    public Category getCategory(){
        return category;
    }
    public void setCategory(Category category){
        this.category=category;
    }
    public Supplier getSupplier(){
        return supplier;
    }
    public void setSupplier(Supplier supplier){
        this.supplier=supplier;
    }
    public Date getPurchaseDate(){
        return purchaseDate;
    }
    public void setPurchaseDate(Date purchaseDate){
        this.purchaseDate=purchaseDate;
    }
    public double getPurchasePrice(){
        return purchasePrice;
    }
    public void setPurchasePrice(double purchasePrice){
        this.purchasePrice=purchasePrice;
    }
    public double getSellingPrice(){
        return sellingPrice;
    }
    public void setSellingPrice(double sellingPrice){
        this.sellingPrice=sellingPrice;
    }
    public int getQuantity(){
        return quantity;
    }
    public void setQuantity(int quantity){
        this.quantity=quantity;
    }

    public void setSectorId(int sectorId) {
        this.sectorId = sectorId;
    }
}
