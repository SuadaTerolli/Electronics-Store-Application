package com.electronicstore.model;

public class Supplier {
    private int supplierId;
    private String name;
    private int sectorId;

    public Supplier()
    {

    }
    public Supplier(int supplierId, String name) {
        this.supplierId = supplierId;
        this.name = name;
        this.sectorId = 0;
    }

    public Supplier(int supplierId, String name, int sectorId) {
        this.supplierId = supplierId;
        this.name = name;
        this.sectorId = sectorId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public int getSupplierId(){
        return supplierId;
    }
    public int getSectorId(){
        return sectorId;
    }


}
