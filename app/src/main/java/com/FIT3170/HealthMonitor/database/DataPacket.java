package com.FIT3170.HealthMonitor.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * This class is used to communicate between the sensor service and the uploader.
 * This is the structure that is expected to add data to the uploader.  It should contain 5 seconds of data.
 */
public class DataPacket implements ECGAlgorithmInt {

    private List<DataPoint> dataArray;

    /**
     * This is the constructor that should be used when created by Sensor Service
     *
     * This could be changed to receiving ArrayList of arrays and this constructor could convert to DataPoints
     * @param dataPoints
     */
    public DataPacket(List<DataPoint> dataPoints){
        LinkedHashMap<Long, List<Integer>> noDupeMap = removeDuplicate(dataPoints);
        dataArray = toDataArray(noDupeMap);
    }

    private List<DataPoint> toDataArray(LinkedHashMap<Long, List<Integer>> hashMap){
        List<DataPoint> data = new ArrayList<DataPoint>();
        Set<Long> keys = hashMap.keySet();
        for (Long key: keys) {
            data.add(new DataPoint(calculateAverage(hashMap.get(key)), key));
        }
        return data;
    }

    private LinkedHashMap<Long, List<Integer>> removeDuplicate(List<DataPoint> dataPoints){
        // Process dataArray and remove duplicates with hashMap
        LinkedHashMap<Long, List<Integer>> hashMap = new LinkedHashMap<Long, List<Integer>>();
        for (int i = 0; i < dataPoints.size(); i ++){
            DataPoint currentDataPoint = dataPoints.get(i);
            // if hashMap does not have the current time, add it into the hashMap along with current value
            if (!hashMap.containsKey(currentDataPoint.getTime())) {
                List<Integer> valueList = new ArrayList<Integer>();
                valueList.add(currentDataPoint.getValue());
                hashMap.put(currentDataPoint.getTime(), valueList);
            }
            // Otherwise add the current value to the pre existing list
            else{
                hashMap.get(currentDataPoint.getTime()).add(currentDataPoint.getValue());
            }
        }
        return hashMap;
    }

    // Method to calculate averages
    private int calculateAverage(List<Integer> valueList){
        if (valueList.size() == 1){
            return valueList.get(0);
        }
        double sum = 0;
        for (Integer value : valueList){
            sum += value;
        }
        sum = sum/valueList.size();
        return (int) Math.round(sum);
    }



    public List<DataPoint> getData(){
        return dataArray;
    }
    public List<DataPoint> getDataArray(){
        return this.dataArray;
    }
}
