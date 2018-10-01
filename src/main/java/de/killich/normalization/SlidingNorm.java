package de.killich.normalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

import java.util.ArrayList;

public class SlidingNorm extends AbstractProcessor{
    private int windowLength = 50;
    private ArrayList<Double> window;
    private String inputKey = "@value";
    private String outpuKey = "@norm";
    private double sum = 0;
    private double sqsum = 0;
    private static final Logger log = LoggerFactory.getLogger(SlidingNorm.class);

    @Override
    public void init(ProcessContext ctx) throws Exception{
        window = new ArrayList<>(windowLength);
        super.init(ctx);
    }

    @Override
    public Data process(Data data){
        if(!data.containsKey(inputKey)){
            log.error(String.format("Key %s not found.", inputKey));
            return null;
        }

        double val = (double)data.get(inputKey);
        if(window.size() == windowLength){
             double dropped = window.remove(0);
             sum -= dropped;
             sqsum -= dropped * dropped;
        }
        window.add(val);
        sum += val;
        sqsum += val*val;

        double mean = sum / window.size();
        double stdv = 1;
        if(window.size() > 1){
            stdv = (sqsum - window.size() * mean * mean) / (window.size() - 1);
        }
        stdv = stdv <= Double.MIN_VALUE ? 1 : stdv;
        stdv = Math.sqrt(stdv);
        data.put(outpuKey, (val-mean)/stdv);
        return data;
    }

    public void setWindowLength(int windowLength){
        this.windowLength = windowLength;
    }

    public void setInputKey(String inputKey){
        this.inputKey = inputKey;
    }

    public void setOutpuKey(String outpuKey){
        this.outpuKey = outpuKey;
    }
}
