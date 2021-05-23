import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class DetectionAlgorithm {

    private static final String inputFile = "Actual_ECG_data.txt";
    private static final int frequency = 50;

    public static void runDetection(){
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

        ArrayList<Integer> heartRate = new ArrayList<>(dataPoints.size());
        scanData(dataPoints, heartRate);
    }


    public static void scanData(ArrayList<Integer> rawData, ArrayList<Integer> heartRate){
        int lastPeak = 0;  // index of the last peak value
        heartRate.add(0);
        heartRate.add(0);
        for (int i = 2; i < rawData.size(); i ++){
            int dataPoint = rawData.get(i);
            int difference = dataPoint - rawData.get(i - 2);  // The difference between this point and the last point

            final int arbitraryBound = -1000;
            heartRate.add(heartRate.get(i - 1));
            if (difference < arbitraryBound && lastPeak < i - 1){
                if (lastPeak != 0) {
                    int interval = i - lastPeak;
                    int rate = (int) (60 * (frequency / (float) interval));
                    heartRate.set(i, rate);
                }
                lastPeak = i;
            }
        }

        outputData(rawData, heartRate);
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
