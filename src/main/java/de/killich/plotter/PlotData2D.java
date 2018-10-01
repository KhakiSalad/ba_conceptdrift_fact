package de.killich.plotter;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import stream.Data;

import java.util.ArrayList;
import java.util.List;

public class PlotData2D{
    private final List<String> labels;
    private final List<Vector2D> data;
    private Limit xLimit;
    private Limit yLimit;
    private List<IndexMarker> markedIndices;

    public PlotData2D(List<Vector2D> data){
        this.data = data;
        this.labels = null;
    }

    public PlotData2D(List<Vector2D> data, List<String> labels){
        this.data = data;
        this.labels = labels;
    }

    public List<Vector2D> getData(){
        return data;
    }

    public String getLabelAt(int index){
        return labels.get(index);
    }

    public Limit getxLimit(){
        if(xLimit == null){
            xLimit = new Limit(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
            for(Vector2D v : data){
                if(v.getX() < xLimit.min){
                    xLimit.min = v.getX();
                }else if(v.getX() > xLimit.max){
                    xLimit.max = v.getX();
                }
            }
        }
        return new Limit(xLimit.min, xLimit.max);
    }

    public void setxLimit(Limit xLimit){
        this.xLimit = this.xLimit == null ? xLimit : this.xLimit;
    }

    public Limit getyLimit(){
        if(yLimit == null){
            yLimit = new Limit(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
            for(Vector2D v : data){
                if(v.getY() < yLimit.min){
                    yLimit.min = v.getY();
                }else if(v.getY() > yLimit.max){
                    yLimit.max = v.getY();
                }
            }
        }
        return new Limit(yLimit.min, yLimit.max);
    }

    public void setyLimit(Limit yLimit){
        this.yLimit = this.yLimit == null ? yLimit : this.yLimit;
    }

    public void setMarkedIndices(List<IndexMarker> markedIndices){
        this.markedIndices = this.markedIndices == null ? markedIndices : this.markedIndices;
    }

    public List<IndexMarker> getMarkedIndices(){
        return markedIndices;
    }

    public boolean hasMarkedIndices(){
        return markedIndices != null;
    }

    public boolean hasLabels(){
        return labels != null;
    }
    public static class Limit{
        public double min;
        public double max;

        public Limit(double min, double max){
            this.min = min;
            this.max = max;
        }

        public double range(){
            return Math.abs(this.max - this.min);
        }
    }
    public static class IndexMarker{
        public double index;
        public String label;
        public IndexMarker(double index, String label){
            this.index = index;
            this.label = label;
        }
    }

}
