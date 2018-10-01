package de.killich.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;

public class ExponentialSmoothing extends AbstractProcessor{
    private static final Logger log = LoggerFactory.getLogger(ExponentialSmoothing.class);
    private double lambda = 0.5;
    private String inputKey = "@value";
    private String outputKey = "@norm";
    private double last = Double.NEGATIVE_INFINITY;

    public String getInputKey(){
        return inputKey;
    }

    public void setInputKey(String inputKey){
        this.inputKey = inputKey;
    }

    public String getOutputKey(){
        return outputKey;
    }

    public void setOutputKey(String outputKey){
        this.outputKey = outputKey;
    }


    public double getLambda(){
        return lambda;
    }

    public void setLambda(double lambda){
        this.lambda = lambda;
    }

    @Override
    public Data process(Data data){
        double value = (double)data.get(inputKey);
        if(last == Double.NEGATIVE_INFINITY){
            last = value;
        }else{
            last = value * lambda + last * (1 - lambda);
        }
        data.put(outputKey, last);
        return data;
    }
}
