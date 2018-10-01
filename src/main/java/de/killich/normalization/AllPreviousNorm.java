package de.killich.normalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;
import stream.drift.DriftDetectionService;

public class AllPreviousNorm extends AbstractProcessor{
    private double sum = 0;
    private double sumSq = 0;
    private double itemCount = 0;
    private double sumWarning = 0;
    private double sumSqWarning = 0;
    private double itemCountWarning = 0;
    private double prevError = 0;
    private String key = "@value";
    private String outputKey ="@error";
    private DriftDetectionService driftDetectionService;
    private static final Logger log = LoggerFactory.getLogger(AllPreviousNorm.class);

    /**
     * This is the main method for processing items. This method is called
     * numerous times - once for each incoming data item.
     *
     * @param item@return
     */
    @Override
    public Data process(Data item){
        if(driftDetectionService != null && driftDetectionService.getDrift()){
            sum = sumWarning;
            sumSq = sumSqWarning;
            itemCount = itemCountWarning;
            sumWarning = 0;
            sumSqWarning = 0;
            itemCountWarning = 0;
        }

        if(item.containsKey(key)){
            double value = (Double)item.get(key);
            sum += value;
            sumSq += value*value;
            itemCount++;
            if(driftDetectionService != null && driftDetectionService.getWarning()){
                sumWarning += value;
                sumSqWarning += value * value;
                itemCountWarning++;
            }

            if(itemCount > 1){
                double mean = sum / itemCount;
                double stdv = Math.sqrt(1.0d / (itemCount - 1.0d) * sumSq - itemCount / (itemCount - 1.0d) * (mean * mean));
                stdv = stdv == 0 ? 1 : stdv;
                prevError = (value - mean)/stdv;
            }
        }
        item.put(outputKey, prevError);
        return item;
    }

    public void setDriftDetectionService(DriftDetectionService driftDetectionService){
        this.driftDetectionService = driftDetectionService;
    }

    public void setKey(String key){
        this.key = key;
    }

    public void setOutput(String outputKey){
        this.outputKey = outputKey;
    }
}
