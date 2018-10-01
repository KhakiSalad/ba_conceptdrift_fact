package de.killich.miscPreproc;

import stream.AbstractProcessor;
import stream.Data;

public class Invert extends AbstractProcessor{
    private String inputKey = "@error";
    private String outputKey = "@error";

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

    @Override
    public Data process(Data data){
        double val = (double)data.get(inputKey);
        data.put(outputKey, -1*val);
        return data;
    }
}
