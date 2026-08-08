package com.electronicstore.model;

import java.util.ArrayList;
import java.util.Date;

public class Cashier extends User{
    private Sector sector;
    private ArrayList<Bill> bills;
    public Cashier(int id, String name, String username, Date date_of_birth, int phoneNumber, String email, double salary, String access_level,String password,Sector sector)
    {
        super(id,name,username,date_of_birth,phoneNumber,email,salary,password,access_level);
        this.sector=sector;
    }

    public Sector getSector() {
        return sector;
    }
    public ArrayList<Bill> viewBillsByDate()
    {
        ArrayList<Bill> todayBills=new ArrayList<Bill>();
        Date today=new Date();
        for (Bill bill:bills)
        {
            if (isSameDay(bill.getBillDate(),today))
            {
                todayBills.add(bill);
            }
        }
        return todayBills;
    }
    public boolean isSameDay(Date date1,Date date2)
    {
        return (date1.getYear()==date2.getYear()
                &&date1.getMonth()==date2.getMonth()
                &&date1.getDay()==date2.getDay());
    }
    public ArrayList<Bill> viewAllBills()
    {
        return  new ArrayList<>(bills);
    }
}
