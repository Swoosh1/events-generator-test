package com.testers.soroosh;

import com.testers.soroosh.BsUtil;
import org.apache.commons.cli.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.json.Json;
import javax.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

public class Generator {
    public static void main(String[] args) throws ParseException {
        System.out.println("Starting");

        Options options = new Options();

        options.addOption("number_of_events", true, "number of events to create");
        options.addOption("batch_size", true, "number of events per file");
        options.addOption("interval", true, "seconds between files");
        options.addOption("output_directory", true, "directory to write files to");

        CommandLineParser parser = new DefaultParser();

        //parse the options passed as command line arguments
        CommandLine cmd = parser.parse( options, args);


        BsUtil bsUtil = new BsUtil();
        List<String> missingArguments = new ArrayList<String>();

        int numberOfEvents = 0;
        if(!cmd.hasOption("number_of_events")) {
            missingArguments.add("number_of_events");
        }else{
            String strNumberOfEvents = cmd.getOptionValue("number_of_events");
            if (!bsUtil.tryParseInt(strNumberOfEvents)) throw new IllegalArgumentException("number_of_events is not a valid integer");
            numberOfEvents = Integer.parseInt(strNumberOfEvents);
        }

        int batchSize = 0;
        if(!cmd.hasOption("batch_size")) {
            missingArguments.add("batch_size");
        }else{
            String strBatchSize = cmd.getOptionValue("batch_size");
            if (!bsUtil.tryParseInt(strBatchSize)) throw new IllegalArgumentException("batch_size is not a valid integer");
            batchSize = Integer.parseInt(strBatchSize);
        }

        int interval=0;
        if(!cmd.hasOption("interval")) {
            missingArguments.add("interval");
        }else{
            String strInterval = cmd.getOptionValue("interval");
            if (!bsUtil.tryParseInt(strInterval)) throw new IllegalArgumentException("interval is not a valid integer");
            interval = Integer.parseInt(strInterval);
        }

        String outputDir = "";
        if(!cmd.hasOption("output_directory")) {
            missingArguments.add("output_directory");
        }else{
            outputDir = cmd.getOptionValue("output_directory");
            if (!bsUtil.isValidDirectory(outputDir)) throw new IllegalArgumentException("output_directory is not a valid path");
        }

        if(missingArguments.size() > 0){

            String strMissingArgs = "";
            for(int i =0;i< missingArguments.size();i++){
                strMissingArgs+=" "+missingArguments.get(i);
            }
            throw new IllegalArgumentException("missing arguments:" + strMissingArgs);
        }

        Integer orderNum = 0;
        Integer currentBatchNumber = 0;
        StringBuilder sb = new StringBuilder();
        Boolean isOrder = true;
        String orderId = "";
        DateTime lastFileWrite = new DateTime(DateTimeZone.UTC).minusSeconds(interval);
        for (int i = 0;i < numberOfEvents;i++){
            String type = "OrderPlaced";

            if(!isOrder){
                type = bsUtil.getOrderAcceptedOrCancelled(orderNum);
            }else{
                orderNum++;
                orderId = randomUUID().toString();
            }

            isOrder = !isOrder;

            JsonObject evnt = Json.createObjectBuilder()
                    .add("Type", type)
                    .add("Data", Json.createObjectBuilder()
                            .add("OrderId", orderId)
                            .add("TimestampUtc", new DateTime().toString("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                            .build())
                    .build();

            if(sb.length() > 0){
                sb.append("\n");
            }
            sb.append(evnt);
            currentBatchNumber++;

            if(currentBatchNumber >= batchSize){
                while(new DateTime(DateTimeZone.UTC).isBefore(lastFileWrite.plusSeconds(interval))){
                    //wait
                }

                lastFileWrite = new DateTime(DateTimeZone.UTC);
                bsUtil.writeStringToFile(sb.toString(), outputDir, lastFileWrite);

                currentBatchNumber = 0;
                sb = new StringBuilder();

            }
        }

        if(currentBatchNumber > 0){
            while(new DateTime(DateTimeZone.UTC).isBefore(lastFileWrite.plusSeconds(interval))){
                //wait
            }

            lastFileWrite = new DateTime(DateTimeZone.UTC);
            bsUtil.writeStringToFile(sb.toString(), outputDir, lastFileWrite);
        }

        System.out.println("Finished");

    }


}