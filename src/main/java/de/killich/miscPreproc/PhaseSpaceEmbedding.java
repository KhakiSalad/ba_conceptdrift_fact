package de.killich.miscPreproc;

import org.apache.commons.math3.linear.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;

import java.util.ArrayList;
/**
 * Processor that implements Phase Space Embedding for m=2. Estimators for mean vector and variance matrix are calculated once with the first values of the stream.
 */
public class PhaseSpaceEmbedding extends stream.AbstractProcessor{
    private double prev = Double.NEGATIVE_INFINITY;
    private static final Logger log = LoggerFactory.getLogger(PhaseSpaceEmbedding.class);
    private ArrayList<RealMatrix> window;
    private String inputKey ="value";
    private int windowLength = 100;
    private RealMatrix mean;
    private RealMatrix covInv;

    @Override
    public void init(ProcessContext ctx) throws Exception{
        super.init(ctx);
        window = new ArrayList<>(windowLength);
    }

    @Override
    public Data process(Data data){
        if(!data.containsKey(inputKey)){
            return data;
        }
        double val = (double)data.get(inputKey);
        if(prev == Double.NEGATIVE_INFINITY ){
            prev = val;
            return null;
        }

        RealMatrix entry = new Array2DRowRealMatrix(2,1);
        entry.setEntry(0,0,prev);
        entry.setEntry(1,0,val);
        prev = val;

        if(window.size() < windowLength){
            window.add(entry);
            if(window.size() == windowLength){
                RealMatrix sum = new Array2DRowRealMatrix(2,1);
                sum.setEntry(0,0,0);
                sum.setEntry(1,0,0);
                for(RealMatrix v : window){
                    sum = sum.add(v);
                }
                mean = sum.scalarMultiply(1.0/window.size());
                RealMatrix scatter = new Array2DRowRealMatrix(2,2);
                for(RealMatrix x : window){
                    scatter = scatter.add(x.subtract(mean).multiply(x.subtract(mean).transpose()));
                    RealMatrix sub1 = x.subtract(mean);
                    RealMatrix sub2 = sub1.transpose();
                    RealMatrix tmp = sub1.multiply(sub2);
                }
                covInv = scatter.scalarMultiply(1.0/window.size());
                covInv = MatrixUtils.inverse(covInv);

                log.info(""+mean.getEntry(0,0)+"  "+ covInv.getEntry(0,0) + " " + covInv.getEntry(0,1));
                log.info(""+mean.getEntry(1,0)+"  "+ covInv.getEntry(1,0) + " " + covInv.getEntry(1,1));
            }else{
                data.put("@error", 0.0d);
                return data;
            }
        }
        RealMatrix distM = entry.subtract(mean).transpose().multiply(covInv).multiply(entry.subtract(mean));
        double dist = Math.sqrt(distM.getEntry(0,0));
        data.put("@error", dist);
        return data;
    }

    public void setInputKey(String inputKey){
        this.inputKey = inputKey;
    }

    public int getWindowLength(){
        return windowLength;
    }

    /**
     * Sets Length of the window used for estimating the mean vector and the variance matrix
     * @param windowLength length of window used for estimators.
     */
    @Parameter
    public void setWindowLength(int windowLength){
        this.windowLength = windowLength;
    }
}
