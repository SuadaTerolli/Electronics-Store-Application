package com.electronicstore.util;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SupplierBinaryConverter {
    public static void main(String[] args) {
        String[][] suppliers = {
                {"1", "Sony"}, {"2", "Samsung"}, {"3", "Apple"}, {"4", "LG"},
                {"5", "Microsoft"}, {"6", "Intel"}, {"7", "Dell"}, {"8", "HP"},
                {"9", "Asus"}, {"10", "Lenovo"}, {"11", "Bosch"}, {"12", "OnePlus"}, {"13", "Nintendo"}
        };

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("suppliers.dat"))) {
            for (String[] supplier : suppliers) {
                dos.writeInt(Integer.parseInt(supplier[0]));
                dos.writeUTF(supplier[1]);
            }
            System.out.println("Binary file created: suppliers.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}