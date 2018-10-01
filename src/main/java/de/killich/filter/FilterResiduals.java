package de.killich.filter;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;

/**
 * Calculates Filterresiduals. Adds difference between values for filteredKey and signalKey to data item.
 */
public class FilterResiduals extends AbstractProcessor{
    private String signalKey = "norm";
    private String filteredKey = "filtered";
    private String outputKey = "@error";

    public String getSignalKey(){
        return signalKey;
    }

    /**
     * Sets key for original values (before filtering).
     * @param signalKey key of original values
     */
    @Parameter
    public void setSignalKey(String signalKey){
        this.signalKey = signalKey;
    }

    public String getFilteredKey(){
        return filteredKey;
    }

    /**
     * Sets key for filtered values (after filtering).
     * @param filteredKey key of filtered values
     */
    @Parameter
    public void setFilteredKey(String filteredKey){
        this.filteredKey = filteredKey;
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
        double signal = (double)data.get(signalKey);
        double filtered = (double)data.get(filteredKey);
        data.put(outputKey, filtered - signal);
        return data;
    }
}
