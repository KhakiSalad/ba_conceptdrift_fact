package de.killich.plotter;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;
import stream.drift.DriftDetectionService;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Processor to visualize drift detection applications. When processing data items a new <code>DataPlotter2D</code> is started. This is used
 * to display the data specified by <code>dataKey</code> in a scatter plot. If <code>indexKey</code> is set to "__automatic" indices will be
 * assigned to the values, and not extracted from the data item.
 *
 * @author nilskillich
 * @see DataPlotter2D
 * @see PlotData2D
 */
public class Visualizer extends AbstractProcessor implements PlotDataSupplier{
    private static final Logger log = LoggerFactory.getLogger(Visualizer.class);
    private String dataKey = "@value";
    private String indexKey = "__automatic";
    private String title;
    private DriftDetectionService driftDetectionService;
    private int limit = Integer.MAX_VALUE;
    private int n = -1;
    private LinkedList<Vector2D> dataPoints = new LinkedList<>();
    private LinkedList<PlotData2D.IndexMarker> markedIndices = new LinkedList<>();
    private LinkedList<String> labels = new LinkedList<>();

    @Override
    @SuppressWarnings("unchecked")
    public PlotData2D getPlotData2D(){
        LinkedList<Vector2D> dataCopy = (LinkedList<Vector2D>)dataPoints.clone();
        PlotData2D ret;
        if(labels.size() > 0){
            LinkedList<String> labelCopy = (LinkedList<String>)labels.clone();
            ret = new PlotData2D(dataCopy, labelCopy);
        }else{
            ret = new PlotData2D(dataCopy);
        }
        ret.setMarkedIndices((LinkedList<PlotData2D.IndexMarker>)markedIndices.clone());
        return ret;
    }

    @Override
    public void init(ProcessContext ctx) throws Exception{
        super.init(ctx);
        title = title == null ? dataKey : title;
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                new DataPlotter2D(Visualizer.this, title);
            }
        });
    }

    @Override
    public Data process(Data data){
        n++;
        if(!data.containsKey(dataKey)){
            log.error("Key "+dataKey +" not found!");
            return data;
        }

        double value = (Double)data.get(dataKey);
        double index = n;

        if(!indexKey.equals("__automatic") && !data.containsKey(indexKey)){
            log.error("indexKey " + indexKey+ " not found!");
            return data;
        }
        if(!indexKey.equals("__automatic")){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date dateIndex = format.parse((String)data.get(indexKey));
                index = dateIndex.getTime();
            }catch(ParseException e){
                log.error(e.getMessage());
            }
        }

        if(driftDetectionService != null && driftDetectionService.getDrift()){
            markedIndices.add(new PlotData2D.IndexMarker(index, driftDetectionService.getId()));
        }

        //reservoir sampling
        if(n > limit){
            int random = ThreadLocalRandom.current().nextInt(n);
            if(random > limit)
                return data;
        }

        dataPoints.add(new Vector2D(index,value));
        if(data.containsKey("@label")){
            labels.add(data.get("@label").toString());
        }
        if(dataPoints.size() > limit){
            int randomIndex = ThreadLocalRandom.current().nextInt(dataPoints.size());
            dataPoints.remove(randomIndex);
            labels.remove(randomIndex);
        }

        return data;
    }

    @Parameter
    public void setData(String dataKey){
        this.dataKey = dataKey;
    }
    @Parameter
    public void setIndex(String indexKey){
        this.indexKey = indexKey;
    }
    @Parameter
    public void setLimit(int limit){
        if(limit > 0){
            this.limit = limit;
        }
    }
    @Parameter
    public void setTitle(String title){
        this.title = title;
    }
    public void setDriftDetectionService(DriftDetectionService driftDetectionService){
        this.driftDetectionService = driftDetectionService;
    }
}
