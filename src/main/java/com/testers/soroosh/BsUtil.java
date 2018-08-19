package com.testers.soroosh;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BsUtil {

    public void writeStringToFile(String body, String output, DateTime dt) {
        try{
            String fileName = "orders-" + dt.toString("yyyy-MM-dd-HH-mm-ss-SSSSSS") + ".json";
            File file= new File(output, fileName);
            FileWriter fw;
            if (file.exists())
            {
                fw = new FileWriter(file,true);
            }
            else
            {
                fw = new FileWriter(file);
            }
            fw.write(body);
            fw.flush();
            fw.close();
        }catch(IOException ex){
            System.out.println("Error: " + ex.getMessage());
            System.out.println("Failed to write the following to file");
            System.out.println(body);
        }

    }

    public String getOrderAcceptedOrCancelled(Integer batchNumber){
        if(batchNumber % 5 == 0){
            return "OrderCancelled";
        }

        return "OrderAccepted";
    }

    public boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValidDirectory(String path){
        return new File(path).exists();
    }
}
