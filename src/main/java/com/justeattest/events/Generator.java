package com.justeattest.events;

import com.justeattest.buslayer.BsUtil;
import com.justeattest.common.GeneratorConfigs;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.json.Json;
import javax.json.JsonObject;

import static java.util.UUID.randomUUID;

public class Generator {
    public static void main(String[] args) {
        System.out.println("Starting");

        BsUtil bsUtil = new BsUtil();
        GeneratorConfigs configs = bsUtil.parseArgumentsIntoConfigs(args);

        StringBuilder sbIllegalOrMissingArguments = new StringBuilder();
        if(configs.getNumberOfOrders() == 0) sbIllegalOrMissingArguments.append(" --number-of-orders");
        if(configs.getBatchSize() == 0) sbIllegalOrMissingArguments.append(" --batch-size");
        if(configs.getInterval() == 0) sbIllegalOrMissingArguments.append(" --interval");
        if(configs.getOutput() == null || configs.getOutput() == "") sbIllegalOrMissingArguments.append(" --output-directory");

        if(sbIllegalOrMissingArguments.length() > 0){
            throw new IllegalArgumentException("please provide valid arguments:" + sbIllegalOrMissingArguments.toString());
        }

        Integer orderNum = 0;
        Integer currentBatchNumber = 0;
        StringBuilder sb = new StringBuilder();
        Boolean isOrder = true;
        String orderId = "";
        DateTime lastFileWrite = new DateTime(DateTimeZone.UTC).minusSeconds(configs.getInterval());
        for (int i = 0;i < configs.getNumberOfOrders();i++){
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

            if(currentBatchNumber >= configs.getBatchSize()){
                while(new DateTime(DateTimeZone.UTC).isBefore(lastFileWrite.plusSeconds(configs.getInterval()))){
                    //wait
                }

                lastFileWrite = new DateTime(DateTimeZone.UTC);
                bsUtil.writeStringToFile(sb.toString(), configs.getOutput(), lastFileWrite);

                currentBatchNumber = 0;
                sb = new StringBuilder();

            }
        }

        if(currentBatchNumber > 0){
            while(new DateTime(DateTimeZone.UTC).isBefore(lastFileWrite.plusSeconds(configs.getInterval()))){
                //wait
            }

            lastFileWrite = new DateTime(DateTimeZone.UTC);
            bsUtil.writeStringToFile(sb.toString(), configs.getOutput(), lastFileWrite);
        }

        System.out.println("Finished");
    }


}
