import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayDeque;


public class DetectionAlgorithm {

    private static final String inputFile = "Actual_ECG_data.txt";
    private static int frequency;
    private static int bufferSize;

    public static void runDetection(int frequencyInput, int bufferSizeInput){
        frequency = frequencyInput;
        bufferSize = bufferSizeInput;

        ArrayList<Integer> dataPoints = new ArrayList<Integer>();
        try {
            File myObj = new File(inputFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                int data = new Integer(myReader.nextLine());
                dataPoints.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        ArrayDeque<Integer> movingQ = new ArrayDeque<>(frequency*bufferSize) ;
        int location = 0;
        int locationMax = dataPoints.size();
        int counter = 0;
        while (true){
            if (location % (frequency) == 0){
                Integer[] dataArray = new Integer[movingQ.size()];
                System.out.println(scanData(movingQ.toArray(dataArray)));
            }

            if (movingQ.size() ==300){
                movingQ.remove();
            }
            movingQ.add(dataPoints.get(location));
            location += 1;
            if (location == locationMax){
                location = 0;
                counter += 1;
                //System.out.println("loop");

            }
            if (counter == 100){
                break;
            }
        }
        /*
        ArrayList<Integer> heartRate = new ArrayList<>(dataPoints.size());
        scanData(dataPoints, heartRate);
        */
    }

    /**
     * Scans this array of ECG data points to determine the interval between peaks, then averages the calculated heart
     * rate across the data
     * @param rawData array of raw ECG data
     * @return the average heart rate across the data
     */
    public static int scanData(Integer[] rawData){
        ArrayList<Integer> heartRates = new ArrayList<>();
        int lastPeak = 0;  // index of the last peak value
        int peaks = 0;
        for (int i = 2; i < rawData.length; i ++){
            int dataPoint = rawData[i];
            int difference = dataPoint - rawData[i-2];  // The difference between this point and the last point

            final int arbitraryBound = -1000;

            if (difference < arbitraryBound && lastPeak < i - 1){
                if (lastPeak != 0) {
                    int interval = i - lastPeak;
                    peaks += 1;
                    int rate = (int) (60 * (frequency / (float) interval));
                    heartRates.add(rate);

                }
                lastPeak = i;
            }
        }
        int rate = (60/bufferSize)*peaks;
        return calculateAverage(heartRates);
    }

    /**
     * Calculate the average value across this arrayList
     * @param intArrayList
     * @return
     */
    private static int calculateAverage(ArrayList<Integer> intArrayList){
        return (int) intArrayList.stream().mapToDouble(d -> d).average().orElse(0.0);
    }


    private static void outputData(ArrayList<Integer> rawData, ArrayList<Integer> heartRate){
        try {
            File outputFile = new File("heartRate.csv");
            if (outputFile.createNewFile()) {
                System.out.println("File created: " + outputFile.getName());

            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter("heartRate.csv");

            for (int i = 0; i < rawData.size(); i ++){
                myWriter.write(rawData.get(i) + ", " + heartRate.get(i) + "\n");
            }

            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
