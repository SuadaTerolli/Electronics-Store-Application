package com.electronicstore.model;

public class BillItem {
    private Item item;
    private int itemQuantity;

    public BillItem(){

    }
    public BillItem(Item item, int itemQuantity){
        this.item=item;
        this.itemQuantity=itemQuantity;
    }
    public Item getItem(){
        return item;
    }
    public void setItem(Item item){
        this.item=item;
    }
    public int getItemQuantity(){
        return itemQuantity;
    }
    public void setItemQuantity(int itemQuantity){
        this.itemQuantity=itemQuantity;
    }
}
