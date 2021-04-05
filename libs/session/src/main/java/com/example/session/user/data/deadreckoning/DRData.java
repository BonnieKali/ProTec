package com.example.session.user.data.deadreckoning;

import java.util.List;

public class DRData {

    private String timestamp;
    private List<Double> xList;
    private List<Double> yList;


    public DRData(String timestamp, List<Double> xList, List<Double> yList){
        this.timestamp = timestamp;
        this.xList = xList;
        this.yList = yList;
    }

    //add a point to the series
    public void addPoint(double x, double y) {
        xList.add(x);
        yList.add(y);
    }

    public float getLastXPoint() {
        double x = xList.get(xList.size() - 1);
        return (float)x;
    }

    public float getLastYPoint() {
        double y = yList.get(yList.size() - 1);
        return (float)y;
    }

    public void clearSet() {
        xList.clear();
        yList.clear();
    }

    public List<Double> getXList(){
        return xList;
    }
    public List<Double> getYList(){
        return yList;
    }

    @Override
    public String toString() {
        return "DRData{" +
                "timestamp='" + timestamp + '\'' +
                ", xList=" + xList +
                ", yList=" + yList +
                '}';
    }
}
