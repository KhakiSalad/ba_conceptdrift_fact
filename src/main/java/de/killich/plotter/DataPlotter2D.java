package de.killich.plotter;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataPlotter2D extends JFrame implements MouseMotionListener{
    private final PlotDataSupplier supplier;
    private static final Logger log = LoggerFactory.getLogger(DataPlotter2D.class);
    private GraphPanel graphPanel;
    private PositionLabel positionLabel;
    private JCheckBox labelCheckBox;
    private boolean useLabel = false;

     DataPlotter2D(PlotDataSupplier supplier){
        this.supplier = supplier;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> this.refreshPlot());
        JButton plusYButton = new JButton("Y +");
        plusYButton.addActionListener(e-> this.zoomYAxis(0.8));
        JButton minusYButton = new JButton("Y -");
        minusYButton.addActionListener(e -> this.zoomYAxis(1.2));
        JButton plusXButton = new JButton("X +");
        plusXButton.addActionListener(e-> this.zoomXAxis(0.1));
        JButton minusXButton = new JButton("X -");
        minusXButton.addActionListener(e -> this.zoomXAxis(-0.1));
        JButton upButton = new JButton("↑");
        upButton.addActionListener(e->this.moveUp(0.2));
        JButton downButton = new JButton("↓");
        downButton.addActionListener(e->this.moveUp(-0.2));
        JButton rightButton = new JButton("→");
        rightButton.addActionListener(e->this.moveRight(0.2));
        JButton leftButton = new JButton("←");
        leftButton.addActionListener(e->this.moveRight(-0.2));

        labelCheckBox = new JCheckBox("show labels");
        labelCheckBox.addItemListener(this::toggleLabel);
        labelCheckBox.setEnabled(false);

        graphPanel = new GraphPanel();
        positionLabel = new PositionLabel();

        graphPanel.setPreferredSize(new Dimension(700,500));
        graphPanel.setBackground(Color.WHITE);
        this.setSize(800,800);
        graphPanel.addMouseMotionListener(this);

        this.setLayout(new FlowLayout());
        this.add(graphPanel);
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(3,4,5,5));
        controlsPanel.add(upButton);
        controlsPanel.add(plusYButton);
        controlsPanel.add(leftButton);
        controlsPanel.add(rightButton);
        controlsPanel.add(downButton);
        controlsPanel.add(minusYButton);
        controlsPanel.add(plusXButton);
        controlsPanel.add(minusXButton);
        controlsPanel.add(refreshButton);
        controlsPanel.add(new JLabel());
        controlsPanel.add(labelCheckBox);
        controlsPanel.add(new JLabel());
        this.add(controlsPanel);
        this.add(positionLabel);
        this.pack();
        this.setVisible(true);
    }

    private void toggleLabel(ItemEvent e){
         this.useLabel = e.getStateChange() == ItemEvent.SELECTED;
         graphPanel.repaint();
    }

    private void moveUp(double v){
        double range = graphPanel.yDrawLimit.range();
        graphPanel.yDrawLimit.min += v*range;
        graphPanel.yDrawLimit.max += v*range;
        graphPanel.repaint();
        graphPanel.revalidate();
    }

    private void moveRight(double v){
        double range = graphPanel.xDrawLimit.range();
        graphPanel.xDrawLimit.min += v * range;
        graphPanel.xDrawLimit.max += v * range;
        graphPanel.repaint();
    }
    public DataPlotter2D(PlotDataSupplier supplier, String title){
        this(supplier);
        this.setTitle(title);
    }

    private void zoomYAxis(double v){
        graphPanel.yDrawLimit.min *=  v;
        graphPanel.yDrawLimit.max *=  v;
        graphPanel.repaint();
        graphPanel.revalidate();
    }

    private void zoomXAxis(double v){
         double range = graphPanel.xDrawLimit.range();
         graphPanel.xDrawLimit.min += range * v;
         graphPanel.xDrawLimit.max -= range * v;
         graphPanel.repaint();
         graphPanel.revalidate();
    }

    private void refreshPlot(){
        graphPanel.yDrawLimit = null;
        graphPanel.xDrawLimit = null;
        graphPanel.repaint();
        graphPanel.revalidate();
    }

    private class GraphPanel extends JPanel{
        public PlotData2D.Limit xDrawLimit;
        public PlotData2D.Limit yDrawLimit;

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            PlotData2D plotData2D = supplier.getPlotData2D();
            if(xDrawLimit == null)
                xDrawLimit = plotData2D.getxLimit();
            if(yDrawLimit == null)
                yDrawLimit = plotData2D.getyLimit();

            int i = 0;
            for(Vector2D v : plotData2D.getData()){
                double index = (v.getX() - xDrawLimit.min) * ((this.getWidth()-10) /(xDrawLimit.max - xDrawLimit.min));
                double value = (v.getY() - yDrawLimit.max) * ((this.getHeight()-10)/(yDrawLimit.min - yDrawLimit.max));
                int indexInt = (int)Math.round(index);
                int valueInt = (int)Math.round(value);
                if(useLabel){
                    String label = plotData2D.getLabelAt(i);
                    g.drawString(label, indexInt, valueInt);
                }
                g.drawOval(indexInt,valueInt, 2,2);
                i++;
            }
            if(plotData2D.hasMarkedIndices()){
                for(PlotData2D.IndexMarker im : plotData2D.getMarkedIndices()){
                    double index = (im.index - xDrawLimit.min) * ((this.getWidth()-10) /(xDrawLimit.max - xDrawLimit.min));
                    int indexInt = (int)Math.round(index);
                    g.setColor(Color.RED);
                    g.drawLine(indexInt,0,indexInt,this.getHeight());
                    g.setColor(Color.BLACK);
                }
            }
            if(plotData2D.hasLabels()){
                labelCheckBox.setEnabled(true);
            }
        }
    }
    private class PositionLabel extends JPanel{
        private JLabel indexLabel = new JLabel("index: ");
        private JLabel indexText = new JLabel();
        private JLabel valueLabel = new JLabel("value: ");
        private JLabel valueText = new JLabel();

        public PositionLabel(){
            super();
            this.setLayout(new GridLayout(2,2));
            indexText.setPreferredSize(new Dimension(90,10));
            valueText.setPreferredSize(new Dimension(90,10));
            this.add(indexLabel);
            this.add(indexText);
            this.add(valueLabel);
            this.add(valueText);
        }
        public void setIndex(double index){
            this.indexText.setText(String.format("%f",index));
            this.repaint();
            this.revalidate();
        }
        public void setValue(double value){
            this.valueText.setText(String.format("%f", value));
            this.repaint();
            this.revalidate();
        }
        public void setIndex(String index){
            this.indexText.setText(index);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e){

    }

    @Override
    public void mouseMoved(MouseEvent e){
        double x = e.getX();
        double y = e.getY();

//        double maxYAbs = Math.max(Math.abs(graphPanel.yDrawLimit.min), Math.abs(graphPanel.yDrawLimit.max));

        x = x * (graphPanel.xDrawLimit.max - graphPanel.xDrawLimit.min) /(graphPanel.getWidth() -10) + graphPanel.xDrawLimit.min;
        //for x > 10^9 interpret as date
        if(x > (10^12)){
            Date xDate = new Date((long) x);

            positionLabel.setIndex(new SimpleDateFormat("yyyy-MM-dd").format(xDate));
        }else{
            positionLabel.setIndex(x);
        }
        y = y * (graphPanel.yDrawLimit.min - graphPanel.yDrawLimit.max)/(this.getHeight()-10) + graphPanel.yDrawLimit.max;
        positionLabel.setValue(y);
    }
}
