package de.killich.miscPreproc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;

/**
 * Processor that implements a simple SAX version. Data has to be normalised and processed with PAA according to SAX paper.
 */
public class SAXProcessor extends AbstractProcessor{
    private static final double[][] breakpoints = {
            {-0.43, 0.43},
            {-0.67, 0, 0.67},
            {-0.84, -0.25, 0.25, 0.84},
            {-0.97, -0.43, 0, 0.43, 0.97}
    };
    private static final double[][] altBreakpoints = {
            {-0.86, 0.86},
            {-1.34, 0, 1.34},
            {-1.68, -0.5, 0, 0.5, 1.68},
            {-1.94, -0.86, 0, 0.86, 1.94}
    };
    private static final Character[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};
    private static final Logger log = LoggerFactory.getLogger(SAXProcessor.class);
    private int alphabetSize = 3;
    private String[] keys;
    private boolean useAltBreakPoints = false;

    public int getAlphabetSize(){
        return alphabetSize;
    }

    public void setAlphabetSize(int alphabetSize){
        this.alphabetSize = alphabetSize;
    }

    public String[] getKeys(){
        return keys;
    }

    public void setKeys(String[] keys){
        this.keys = keys;
    }

    @Parameter
    public void setUseAltBreakpoints(boolean useAltBreakPoints){
        this.useAltBreakPoints = useAltBreakPoints;
        log.info("Using alternative breakpoints, assuming sigma=2");
    }
    @Override
    public Data process(Data data){
        //TODO:more than one key
        String key = keys[0];
        double value = (double) data.get(key);

        if(!useAltBreakPoints){
            for(int i = 0; i < alphabetSize - 1; i++){
                if(value < breakpoints[alphabetSize - 3][i]){
                    data.put("SAX_" + key, alphabet[i]);
                    break;
                }
                data.put("SAX_" + key, alphabet[alphabetSize - 1]);
            }
            return data;
        }else{
            for(int i = 0; i < alphabetSize - 1; i++){
                if(value < altBreakpoints[alphabetSize - 3][i]){
                    data.put("SAX_" + key, alphabet[i]);
                    break;
                }
                data.put("SAX_" + key, alphabet[alphabetSize - 1]);
            }
            return data;
        }
    }
}
