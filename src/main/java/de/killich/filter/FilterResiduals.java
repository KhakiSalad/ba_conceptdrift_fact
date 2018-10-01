package de.killich.filter;

import stream.AbstractProcessor;
import stream.Data;

public class FilterResiduals extends AbstractProcessor{
    private String signalKey = "@norm";
    private String filteredKey = "@filtered";
    private String outputKey = "@error";

    public String getSignalKey(){
        return signalKey;
    }

    public void setSignalKey(String signalKey){
        this.signalKey = signalKey;
    }

    public String getFilteredKey(){
        return filteredKey;
    }

    public void setFilteredKey(String filteredKey){
        this.filteredKey = filteredKey;
    }

    public String getOutputKey(){
        return outputKey;
    }

    public void setOutputKey(String outputKey){
        this.outputKey = outputKey;
    }

    @Override
    public Data process(Data data){
        double signal = (double)data.get(signalKey);
        double filtered = (double)data.get(filteredKey);
        data.put(outputKey, filtered - signal);
        return data;
    }
}
