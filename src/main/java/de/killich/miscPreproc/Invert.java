package de.killich.miscPreproc;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;

/**
 * Processor that changes the value of inputKey to -1*value.
 */
public class Invert extends AbstractProcessor{
    private String inputKey = "@error";
    private String outputKey = "@error";

    public String getInputKey(){
        return inputKey;
    }

    @Parameter
    public void setInputKey(String inputKey){
        this.inputKey = inputKey;
    }

    public String getOutputKey(){
        return outputKey;
    }

    @Parameter
    public void setOutputKey(String outputKey){
        this.outputKey = outputKey;
    }

    @Override
    public Data process(Data data){
        double val = (double)data.get(inputKey);
        data.put(outputKey, -1*val);
        return data;
    }
}
