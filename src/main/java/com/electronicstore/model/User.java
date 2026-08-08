package com.electronicstore.model;

import java.util.Date;

public class User {
    private int id;
    private String name;
    private String username;
    private Date date_of_birth;
    private long phoneNumber;
    private String email;
    private double salary;
    private String password;
    private static boolean isLoggedIn=false;
    private String access_level;
    private String sectorName;

    public User(){

    }
    public User(String name,String username,String password,String access_level)
    {
        this.name=name;
        this.username=username;
        this.password=password;
        this.access_level=access_level;
    }
    public User(int id,String name,String username,Date date_of_birth,long phoneNumber,String email,double salary,String password,String access_level)
    {
        this.id=id;
        this.name=name;
        this.username=username;
        this.date_of_birth=new Date();
        this.phoneNumber=phoneNumber;
        this.email=email;
        this.salary=salary;
        this.password=password;
        this.access_level=access_level;
    }

    public User(int id,String name,String username,Date date_of_birth,long phoneNumber,String email,double salary,String password,String access_level,String sectorName)
    {
        this.id=id;
        this.name=name;
        this.username=username;
        this.date_of_birth=new Date();
        this.phoneNumber=phoneNumber;
        this.email=email;
        this.salary=salary;
        this.password=password;
        this.access_level=access_level;
        this.sectorName=sectorName;
    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getSalary() {
        return  salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getAccess_level() {
        return access_level;
    }

    public String getPassword() {
        return password;
    }

    public void setAccess_level(String access_level) {
        this.access_level = access_level;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }
    public String getSectorName()
    {
        return sectorName;
    }

    public boolean login(String username, String password)
    {
        if (username==null && password==null)
        {
            System.out.println("Username and password cannot be empty!");
            return false;
        }
        if (username.equals("validUsername")&&password.equals("validPassword"))
        {
            isLoggedIn=true;
            System.out.println("Login successful");
            return true;
        }
        else
        {
            System.out.println("Invalid username or password");
            return false;
        }
    }
    public void logout()
    {
        if (isLoggedIn)
        {
            isLoggedIn=false;
            System.out.println("You are logged out!");
        }
    }
    public String toString()
    {
        return "id: "+getId()+" name: "+getName()+" username: "+getUsername()+" date of birth: "+getDate_of_birth()
                +" phone number: "+getPhoneNumber()+" email: "+getEmail()+" salary: "+getSalary()+" access level: "+getAccess_level();
    }

}
