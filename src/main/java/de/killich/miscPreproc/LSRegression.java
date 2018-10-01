package de.killich.miscPreproc;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.drift.DriftDetectionService;

import java.util.ArrayList;
import java.util.List;

public class LSRegression extends AbstractProcessor{
    private int learningTime = 50;
    private List<Double> xs;
    private DriftDetectionService driftDetectionService;
    private String inputKey = "@value";
    private String outputKey = "@error";
    private double a;
    private double prevX;
    private boolean isLearning = true;

    @Override
    public void init(ProcessContext ctx) throws Exception{
        super.init(ctx);
        xs = new ArrayList<>(learningTime);
    }

    @Override
    public Data process(Data data){
        if(!data.containsKey(inputKey)){
            return null;
        }
        if(driftDetectionService != null && driftDetectionService.getDrift()){
            xs.clear();
            isLearning = true;
            a = 0;
        }

        if(xs.size() < learningTime){
            xs.add((double)data.get(inputKey));
            data.put(outputKey, 0.0);
            return data;
        }
        if(isLearning){
            double sum = 0;
            double diff = 0;
            for(int i = 0; i< xs.size()-1;i++){
                diff = xs.get(i) - xs.get(i+1);
                sum += diff * diff;
            }
            a = sum / (xs.size()-1);
            prevX = xs.get(xs.size()-2);
            isLearning = false;
        }
        if(!isLearning){
            double error = prevX + a - (double)data.get(inputKey);
            error = error * error;
            data.put(outputKey, error);
            data.put(outputKey +"_a", a);
            prevX = (double)data.get(inputKey);
        }

        return data;
    }

    public void setInputKey(String inputKey){
        this.inputKey = inputKey;
    }

    public void setOutputKey(String outputKey){
        this.outputKey = outputKey;
    }

    public void setLearningTime(int learningTime){
        this.learningTime = learningTime;
    }

    public void setDriftDetectionService(DriftDetectionService driftDetectionService){
        this.driftDetectionService = driftDetectionService;
    }
}
