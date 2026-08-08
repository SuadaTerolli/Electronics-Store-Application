package com.electronicstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Supplier;

public class Administrator extends User {
    private ArrayList<User> employees;

    public Administrator(int id, String name, String username, Date date_of_birth, int phoneNumber, String email, double salary, String password,String access_level, ArrayList<User> employees)
    {
        super(id,name,username,date_of_birth,phoneNumber,email,salary,password,access_level);
        this.employees=employees;
    }
    public ArrayList<User> getEmployees()
    {
        return employees;
    }
    public void registerEmployee(User user)
    {
        if (user!=null)
        {
            for (User existingUser:employees)
            {
                if (existingUser.getId()== user.getId())
                {
                    System.out.println("User with ID " + user.getId() + " already exists!");
                    return;
                }
                employees.add(user);
                System.out.println("Employee " + user.getName() + " is registered successfuly!");
            }//THESE PRINTINGS ARE OPTIONAL
        }
        else
        {
            System.out.println("Invalid user entered!");
        }
    }
    public User editEmployee(int userId,String name,int phoneNumber,String email,int salary,String newRole)
    {
        for (User employee:employees)
        {
            if (employee.getId()==userId)
            {
                if (name!=null&& !name.isEmpty())
                {
                    employee.setName(name);
                }
                if (phoneNumber!=0)
                {
                    employee.setPhoneNumber(phoneNumber);
                }
                if (email!=null && !email.isEmpty())
                {
                    employee.setEmail(email);
                }
                if (salary!=0)
                {
                    employee.setSalary(salary);
                }
                if (newRole!=null && !newRole.isEmpty())
                {
                    User updatedUser=changeRoles(employee,newRole);
                    if (updatedUser!=null)
                    {
                        employees.set(employees.indexOf(employee),updatedUser);
                        return updatedUser;
                    }
                    else
                    {
                        System.out.println("Invalid role input!");//OPTIONAL OUTPUT
                    }
                }
                return employee;
            }
        }
        System.out.println("Employee not found!");
        return null;
    }

    public void deleteEmployee(int id)
    {
        boolean removed = employees.removeIf(employee -> employee.getId() == id);
        if (removed) {
            System.out.println("Employee with ID " + id + " removed.");
        } else {
            System.out.println("Employee with ID " + id + " not found.");
        }
    }


    public User changeRoles(User user,String newRole)
    {
        switch (newRole.toLowerCase())
        {
            /*
            case "administrator":
                return new Administrator(
                        user.getId(),user.getName(),user.getUsername(),
                        user.getDate_of_birth(),user.getPhoneNumber(),user.getEmail(),
                        user.getSalary(),new ArrayList<User>()
                );
            case "manager":
                return new Manager(
                        user.getId(),user.getName(),user.getUsername(),
                        user.getDate_of_birth(),user.getPhoneNumber(),user.getEmail(),
                        user.getSalary(),new ArrayList<Sector>(),new ArrayList<Supplier>()
                );
            case "cashier":
                return new Cashier(
                        user.getId(),user.getName(),user.getUsername(),
                        user.getDate_of_birth(),user.getPhoneNumber(),user.getEmail(),
                        user.getSalary(),user.getSector()
                );*/
            default:
                return null;
        }
    }
    public FinancialSummary generateStatistics(Date startDate, Date endDate, ArrayList<User> users, ArrayList<Item> items, ArrayList<Bill> bills) {
        double totalIncome = 0.0;
        double totalCosts = 0.0;
        double salaries = 0.0;
        double itemsPurchasedCosts = 0.0;

        for (Bill bill : bills) {
            if (bill.getBillDate().after(startDate) && bill.getBillDate().before(endDate)) {
                totalIncome += bill.calculateTotalAmount();
            }
        }

        for (User user : users) {
            salaries += user.getSalary();
        }

        for (Item item : items) {
            itemsPurchasedCosts += item.getPurchasePrice() * item.getQuantity();
        }

        totalCosts = salaries + itemsPurchasedCosts;

        FinancialSummary summary = new FinancialSummary();
        summary.setTotalIncome(totalIncome);
        summary.setTotalCosts(totalCosts);
        summary.setSalaries(salaries);
        summary.setItemsPurchasedPrice(itemsPurchasedCosts);
        summary.setStartDate(startDate);
        summary.setEndDate(endDate);

        return summary;
    }
}
