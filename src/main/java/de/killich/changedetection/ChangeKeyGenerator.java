package de.killich.changedetection;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;
import stream.drift.DriftDetectionService;

/**
 *  Processor that adds driftDetection information to a data item. It adds 1.0 to the specified keys, iff the given
 *  driftDetectionService detects a warning or a change.
 */

public class ChangeKeyGenerator extends AbstractProcessor{
    private DriftDetectionService driftDetectionService;
    private String driftKey = "drift";
    private String warningKey = "warning";
    private boolean useWarningPrefix = false;
    private boolean useDriftPrefix = false;


    @Override
    public void init(ProcessContext ctx) throws Exception{
        super.init(ctx);
        if(useDriftPrefix){
            driftKey = driftDetectionService.getId() + driftKey;
        }
        if(useWarningPrefix){
            warningKey = driftDetectionService.getId() + warningKey;
        }
    }

    @Override
    public Data process(Data data){
        data.put(driftKey,driftDetectionService.getDrift());
        data.put(warningKey,driftDetectionService.getWarning());
        return data;

    }

    /**
     * Sets observed driftDetectionService
     * @param driftDetectionService observed DriftDetectionService
     */
    @Parameter(required = true)
    public void setDriftDetectionService(DriftDetectionService driftDetectionService){
        this.driftDetectionService = driftDetectionService;
    }

    /**
     * Sets key for drift value.
     * @param driftKey Key to use for driftDetection alarm value. The String can be prefixed with "__id__" to use service id as prefix.
     */
    public void setDriftKey(String driftKey){
        if(driftKey.startsWith("__id__")){
            useDriftPrefix = true;
            this.driftKey = driftKey.substring(6);
        }else{
            this.driftKey = driftKey;
        }
    }

    public void setWarningKey(String warningKey){
        if(warningKey.startsWith("__id__")){
            useWarningPrefix = true;
            this.warningKey = warningKey.substring(6);
        }else{
            this.warningKey = warningKey;
        }
    }
}
