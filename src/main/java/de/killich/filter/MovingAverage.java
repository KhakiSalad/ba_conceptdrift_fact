package de.killich.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

import java.util.LinkedList;
/**
 * Processor that implements moving average for an attribute of the data item.
 */
public class MovingAverage extends AbstractProcessor{
    private static final Logger log = LoggerFactory.getLogger(MovingAverage.class);
    private String inputKey = "@value";
    private String outputKey = "@norm";
    private int windowSize = 5;
    private LinkedList<Data> window;

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

    public int getWindowSize(){
        return windowSize;
    }

    public void setWindowSize(int windowSize){
        this.windowSize = windowSize;
    }

    @Override
    public void init(ProcessContext ctx) throws Exception{
        super.init(ctx);
        window = new LinkedList<>();
    }

    @Override
    public Data process(Data data){
        window.add(data);
        if(window.size() > windowSize){
            window.remove();
        }
        double sum = 0d;
        for(Data d: window){
            sum += (double)d.get(inputKey);
        }
        Data ret;
        if(window.size() != 1){
            ret = window.get(window.size() / 2);
        }else{
            ret = window.get(0);
        }
        ret.put(outputKey, sum/window.size());
        return ret;
    }
}
