package de.killich.miscPreproc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;

public class Difference extends AbstractProcessor{
    private static final Logger log = LoggerFactory.getLogger(Difference.class);
    private String inputKey = "@value";
    private String outputKey = null;
    private double lastValue = Double.NEGATIVE_INFINITY;

    @Override
    public Data process(Data data){
        if(!data.containsKey(inputKey)){
            log.error("Key "+ inputKey + " not found.");
            return data;
        }

        double value = (double)data.get(inputKey);
        if(lastValue == Double.NEGATIVE_INFINITY){
            lastValue = value;
            return data;
        }

        double dif = value - lastValue;
        if(outputKey != null)
            data.put(outputKey, dif);
        else
            data.put(inputKey, dif);
        lastValue = value;
        return data;

    }

    public void setInput(String inputKey){
        this.inputKey = inputKey;
    }

    public void setOutput(String outputKey){
        this.outputKey = outputKey;
    }
}
