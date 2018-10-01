package de.killich.changedetection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * Only shows Data, for which change value is true. Allows to quickly find detected changes in streams.
 */
public class ChangeFilter extends AbstractProcessor{
    private static final Logger log = LoggerFactory.getLogger(ChangeFilter.class);
    private String[] keys = {"change"};

    @Parameter
    public void setKeys(String keys){
        List<String> keyList = new LinkedList<>();
        StringTokenizer tk = new StringTokenizer(keys, ",");
        while(tk.hasMoreTokens()){
            keyList.add(tk.nextToken().replace(" ", ""));
        }
        this.keys = new String[keyList.size()];
        this.keys = keyList.toArray(this.keys);
    }


    @Override
    public Data process(Data data){
        boolean show = false;
        for(String key : keys){
            if(!data.containsKey(key)){
                log.info("key "+ key+" not found. Skipping key!");
            }
        }

        for(String key : keys){
            if(data.containsKey(key) && data.get(key).toString().contains("true")){
                show = true;
                break;
            }
        }
        return show? data : null;
    }
}
