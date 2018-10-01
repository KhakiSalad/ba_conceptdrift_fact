package de.killich.changedetection;

import stream.AbstractProcessor;
import stream.Data;
import stream.drift.DriftDetectionService;

public class ChangeKeyGenerator extends AbstractProcessor{
    private DriftDetectionService driftDetectionService;

    @Override
    public Data process(Data data){
        data.put("drift",driftDetectionService.getDrift());
        data.put(driftDetectionService.getId()+"_warning",driftDetectionService.getWarning());
        return data;

    }

    public void setDriftDetectionService(DriftDetectionService driftDetectionService){
        this.driftDetectionService = driftDetectionService;
    }
}
