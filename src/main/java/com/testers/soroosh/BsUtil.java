package com.testers.soroosh;

import com.testers.soroosh.GeneratorConfigs;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BsUtil {

    public GeneratorConfigs parseArgumentsIntoConfigs(String[] args){

        GeneratorConfigs res = new GeneratorConfigs();

        for(int i = 0;i < args.length;i++){

            try{
                String[] arg = args[i].split("=");
                if(arg.length > 1){
                    if(arg[0].substring(2, arg[0].length()).equals("number-of-orders")){
                        res.setNumberOfOrders(Integer.parseInt(arg[1]));
                    }
                    else if(arg[0].substring(2, arg[0].length()).equals("batch-size")){
                        res.setBatchSize(Integer.parseInt(arg[1]));
                    }
                    else if(arg[0].substring(2, arg[0].length()).equals("interval")){
                        res.setInterval(Integer.parseInt(arg[1]));
                    }
                    else if(arg[0].substring(2, arg[0].length()).equals("output-directory")){
                        res.setOutput(arg[1]);
                    }
                }

            }catch (Exception ex){

            }
        }

        return res;
    }

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
}
