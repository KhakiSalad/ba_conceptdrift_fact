package de.killich.miscPreproc;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;

import static java.lang.Math.abs;
/**
 * Processor that tests, whether values for valueKey are between the given Percentiles p and (1-p) of the Chi-Squared distribution with two degrees of freedom.
 */
public class InChi2DistributionPercentile extends AbstractProcessor{
    private String inputKey = "value";
    private String outputKey= "@error";
    private double percentile= 0.9d;
    private double upper;
    private double lower;

    @Parameter
    public String getInputKey(){
        return inputKey;
    }

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

    public double getPercentile(){
        return percentile;
    }

    /**
     * Sets percentile p used for bounds.
     * @param percentile used for bounds
     */
    @Parameter
    public void setPercentile(double percentile){
        this.percentile = percentile;
    }

    @Override
    public void init(ProcessContext ctx) throws Exception{
        ChiSquaredDistribution distribution = new ChiSquaredDistribution(2.0);
        upper = distribution.inverseCumulativeProbability(percentile);
        lower = distribution.inverseCumulativeProbability(1-percentile);
        super.init(ctx);
    }

    @Override
    public Data process(Data data){
        double value = (double)data.get(inputKey);
        if(value < upper && lower < value){
            data.put(outputKey, 0.0);
        }else{
            data.put(outputKey, 1.0);
        }

        return data;
    }
}
