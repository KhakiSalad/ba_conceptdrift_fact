package de.killich.normalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;

import java.util.ArrayList;
import java.util.Random;

/**
 * Processor that normalizes the value for the given key. The window used for the estimation of the mean and variance, is
 * constructed by using reservoir sampling.
 * The number of values used ist determined by <code>windowLength</code>
 *
 */
public class ReservoirNorm extends AbstractProcessor{
    private int windowLength = 50;
    private ArrayList<Double> window;
    private String inputKey = "@value";
    private String outputKey = "@norm";
    private double sum = 0;
    private double sqsum = 0;
    private int n = 0;
    private static final Logger log = LoggerFactory.getLogger(ReservoirNorm.class);

    @Override
    public void init(ProcessContext ctx) throws Exception{
        window = new ArrayList<>(windowLength);
        super.init(ctx);
    }

    @Override
    public Data process(Data data){
        n++;
        if(!data.containsKey(inputKey)){
            log.error(String.format("Key %s not found.", inputKey));
            return null;
        }

        double val = (double)data.get(inputKey);
        if(window.size() < windowLength){
            window.add(val);
            sum += val;
            sqsum += val*val;
        }
        if(window.size() == windowLength){
            if(new Random().nextInt(n) < windowLength){
                //aufnehmen
                double dropped = window.remove(new Random().nextInt(window.size()));
                sum -= dropped;
                sqsum -= dropped * dropped;

                window.add(val);
                sum += val;
                sqsum += val*val;
            }
        }

        double mean = sum / window.size();
        double stdv = 1;
        if(window.size() > 1){
            stdv = (sqsum - window.size() * mean * mean) / (window.size() - 1);
        }
        stdv = stdv <= Double.MIN_VALUE ? 1 : stdv;
        stdv = Math.sqrt(stdv);
        data.put(outputKey, (val-mean)/stdv);
        return data;
    }

    @Parameter
    public void setWindowLength(int windowLength){
        this.windowLength = windowLength;
    }

    @Parameter
    public void setInputKey(String inputKey){
        this.inputKey = inputKey;
    }

    @Parameter
    public void setOutputKey(String outputKey){
        this.outputKey = outputKey;
    }
}
