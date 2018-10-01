package de.killich.miscPreproc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;
import java.io.Serializable;

public class AbsProcessor extends AbstractProcessor{
    private static final Logger log = LoggerFactory.getLogger(AbsProcessor.class);
    private String[] keys;

    @Override
    public Data process(Data data){
        keys = keys == null ? (String[])data.keySet().toArray() : keys;
        for(String k : keys){
            Serializable element = data.get(k);
            if(element instanceof Double){
                data.put(k,Math.abs((Double)element));
            }
        }
        return data;
    }

    @Parameter
    public void setKeys(String[] keys){
        this.keys = keys;
    }
}
