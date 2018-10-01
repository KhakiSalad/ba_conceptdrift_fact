package de.killich.normalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;
import stream.drift.DriftDetectionService;

import java.util.ArrayList;
/**
 * Processor that normalizes the value for the given key. The first values of the stream are used for the estimation of the mean and variance.
 * The number of values used ist determined by <code>windowLength</code>. This process can be repeated after a concept drift was detected.
 *
 */
public class FixNorm extends stream.AbstractProcessor{
    private ArrayList<Double> window;
    private ArrayList<Double> warningWindow;
    private int windowLength = 50;
    private double mean, stdv;
    private static final Logger log = LoggerFactory.getLogger(FixNorm.class);
    private String inputKey = "@value";
    private String outputKey= "@norm";
    private DriftDetectionService driftDetectionService;
    private boolean logged = false;


    @Override
    public void init(ProcessContext ctx) throws Exception{
        super.init(ctx);
        window = new ArrayList<>(windowLength);
        warningWindow = new ArrayList<>(windowLength);
    }

    @Override
    public Data process(Data data){
        if(!data.containsKey(inputKey)){
            if(!logged){
                log.error(String.format("Key %s not found! Returning item without change.", inputKey));
                logged= true;
            }
            return data;
        }
        double val = (double)data.get(inputKey);

        if(driftDetectionService != null && driftDetectionService.getWarning()){
            warningWindow.add(val);
        }
        if(driftDetectionService != null && driftDetectionService.getDrift() && window.size() == windowLength){
            window.clear();
            window.addAll(warningWindow);
            warningWindow.clear();
        }

        if(window.size() < windowLength){
            window.add(val);
            if(window.size() == windowLength){
                double sum = 0.0d;
                double sqsum = 0.0d;
                for(double x : window){
                    sum += x;
                    sqsum += x*x;
                }
                mean = sum / window.size();
                stdv = Math.sqrt((sqsum - window.size() * mean * mean)/(window.size() - 1));
                stdv = stdv == 0 ? 1 : stdv;
            }
        }
        if(window.size() == windowLength){
            val = (val - mean)/stdv;
            data.put(outputKey, val);
            return data;
        }
        return null;
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
    public void setDriftDetectionService(DriftDetectionService driftDetectionService){
        this.driftDetectionService = driftDetectionService;
    }

    @Parameter
    public void setOutputKey(String outputKey){
        this.outputKey = outputKey;
    }
}
