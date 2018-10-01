package de.killich.miscPreproc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Processor that reduces Values on a time basis. All data items in the given period are consumed and replaced by one item
 * containing the mean of the values in the consumed items. Currently only one value is supported. The value used is
 * specified by valueKey. The dates used for aggregation are given by dateKey.
 */
public class TimebasedReduction extends AbstractProcessor{
    private static Logger log = LoggerFactory.getLogger(TimebasedReduction.class);
    private Period aggregationPeriod = Period.ofMonths(1);
    private LocalDateTime windowStart;
    private double currentMean = 0;
    private double n = 0;
    private String valueKey = "@value";
    private String dateKey = "Date";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * This is the main method for processing items. This method is called
     * numerous times - once for each incoming data item.
     *
     * @param item@return
     */
    @Override
    public Data process(Data item){
        if(!item.containsKey(valueKey)){
            log.error("Key "+valueKey+" not found - returning item without change!");
            return item;
        }
        if(!item.containsKey(dateKey)){
            log.error("Key "+dateKey+" not found - returning item without change!");
            return item;
        }

        double value = (double)item.get(valueKey);
        LocalDateTime date = LocalDateTime.parse((String)item.get(dateKey), dateFormatter);
        if(windowStart == null){
            windowStart = date;
            currentMean += value;
            n = 1;
        }
        if(date.isAfter(windowStart.plus(aggregationPeriod))){
           item.put(valueKey,currentMean);
           item.put(dateKey, windowStart.format(dateFormatter));
           item.put(dateKey+":end", windowStart.plus(aggregationPeriod));
           currentMean = 0;
           n = 0;

           windowStart = date;
           currentMean += value;
           n = 1;
           return item;
        }
        currentMean = (currentMean * n + value) / (n+1);
        n++;
        return null;
    }

    @Override
    public void finish() throws Exception{
        super.finish();
    }

    public void setValueKey(String valueKey){
        this.valueKey = valueKey;
    }

    public void setDateFormat(String dateFormatString){
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormatString);
    }

    public void setDateKey(String dateKey){
        this.dateKey = dateKey;
    }
    public void setAggregationPeriod(String periodString){
        this.aggregationPeriod = Period.parse(periodString);
    }
}
