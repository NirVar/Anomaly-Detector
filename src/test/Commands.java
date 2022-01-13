package test;

import test.SimpleAnomalyDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.net.CookieManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Commands {

	// Default IO interface
	public interface DefaultIO {
		public String readText();

		public void write(String text);

		public float readVal();

		public void write(float val);

		default public boolean CreateFile(String FileName) {
			File csvFile = new File(FileName);
			try {
				if (csvFile.createNewFile())
					return true;
				if (csvFile.exists())
					return true;
			} catch (IOException e) {
				System.out.println("Can't Open the file");
				e.printStackTrace();
			}
			return false;
		}

		default public int WriteCsv(String fileName) {
			int totalLines = 0;
			FileIO csvWriter = new FileIO(fileName, fileName);
			String line = this.readText();
			while (!line.equals("done")) {
				csvWriter.write(line);
				csvWriter.write("\n");
				line = this.readText();
				totalLines++;
				csvWriter.out.flush();
			}

			return totalLines;
		}
	}

	// the default IO to be used in all commands
	DefaultIO dio;
	SimpleAnomalyDetector anomalyDetector = new SimpleAnomalyDetector();

	public Commands(DefaultIO dio) {
		this.dio = dio;
	}

	// you may add other helper classes here


	// the shared state of all commands
	private class SharedState {
		// implement here whatever you need

	}

	private SharedState sharedState = new SharedState();


	// Command abstract class
	public abstract class Command {
		protected String description;

		public Command(String description) {
			this.description = description;
		}

		public abstract void execute();
	}

	//Command when starting the code and menu for the user.
	public class PrintMenuCommand extends Command {

		public PrintMenuCommand() {
			super("Welcome to the Anomaly Detection Server.\n" +
					"Please choose an option:\n" +
					"1. upload a time series csv file\n" +
					"2. algorithm settings\n" +
					"3. detect anomalies\n" +
					"4. display results\n" +
					"5. upload anomalies and analyze results\n" +
					"6. exit\n");
		}

		@Override
		public void execute() {
			dio.write(description);
		}
	}

	// command #1
	public class uploadTimeSeries extends Command {
		public uploadTimeSeries() {
			super("Uploading the test and train anomalies lists.\n");
		}

		@Override
		public void execute() {
			dio.write("Please upload your local train CSV file");
			String trainFile = "anomalyTrain.csv";
			String testFile = "anomalyTest.csv";
			if (dio.CreateFile(trainFile) && dio.CreateFile(testFile)) {
				dio.WriteCsv(trainFile);
				dio.write("Upload complete.\nPlease upload your local test CSV file.\n");
				dio.WriteCsv(testFile);
				dio.write("Upload complete.\n");
			} else {
				dio.write("There was an issue with your CSV file.\n");
			}
		}
	}

	// command #2
	public class AlgorithmSetting extends Command {
		public AlgorithmSetting() {
			super("Choosing the algorithm settings.\n");
		}

		@Override
		public void execute() {
			dio.write("The current correlation threshold is ");
			dio.write(anomalyDetector.threshold + "\n" + "Type a new threshold");
			Float newThreshold = dio.readVal();
			if (newThreshold > 1 || newThreshold < 0) {
				dio.write("please choose a value between 0 and 1.\n");
			} else {
				anomalyDetector.threshold = newThreshold;
			}
		}
	}

	//command #3
	public class DetectAnomalies extends Command {

		public DetectAnomalies() {
			super("Detecting Anomalies.");
		}

		@Override
		public void execute() {
			TimeSeries trainTable = new TimeSeries("anomalyTrain.csv");
			TimeSeries testTable = new TimeSeries("anomalyTest.csv");
			anomalyDetector.learnNormal(trainTable);
			anomalyDetector.detect(testTable);
			dio.write("anomaly detection complete.\n");
		}
	}

	//command #4
	public class DisplayResults extends Command {
		public DisplayResults() {
			super("Displays the results of the anomaly detection");
		}

		@Override
		public void execute() {
			for (int i = 0; i < anomalyDetector.anomalyReports.size(); i++) {
				dio.write(anomalyDetector.anomalyReports.get(i).timeStep + "\t");
				dio.write(anomalyDetector.anomalyReports.get(i).description + "\n");
			}
			dio.write("Done.\n");
		}
	}

	//command #5
	public class UploadAndAnalyze extends Command {

		public UploadAndAnalyze() {
			super("Upload anomalies and analyze results");
		}

		@Override
		public void execute() {
			dio.write("Please upload your local anomalies file.\n");
			ArrayList<Long> inputValue = inputToValue();
			// P = total number of anomaly in file
			float P = inputValue.size()/2;
			// N = total number of time without anomaly
			float N = anomalyDetector.totalTimeSteps - TotalErrorTime(inputValue);//total time without reports
			float truePositive = TruePositive(inputValue);
			float falsePositive = StraightReports() - truePositive;
			dio.write("Upload complete.\n");
			DecimalFormat df = new DecimalFormat("#0.0");
			df.setMaximumFractionDigits(3);
			df.setRoundingMode(RoundingMode.DOWN);
			dio.write("True Positive Rate: " + df.format(truePositive/P) + "\n");
			dio.write("False Positive Rate: " + df.format(falsePositive/N) + "\n");
		}

		public ArrayList<Long> inputToValue(){
			String[] strings = dio.readText().split(",");
			ArrayList<Long> timeSteps = new ArrayList<>();
			while(!strings[0].equals("done")){
				timeSteps.add(Long.parseLong(strings[0]));
				timeSteps.add(Long.parseLong(strings[1]));
				strings = dio.readText().split(",");
			}
			return timeSteps;
		}//even place means start and odd place means ends.

		public int TruePositive(ArrayList<Long> input){
			int numOfFPs = 0;
			long current;
			for(int i = 0; i < input.size(); i+=2){
				for(int j = 0; j < anomalyDetector.anomalyReports.size(); j++){
					current = anomalyDetector.anomalyReports.get(j).timeStep;
					if(current >= input.get(i) && current <= input.get(i+1)) {
						numOfFPs++;
						break;
					}
				}
			}
			return numOfFPs;
		}

		public int TotalErrorTime(ArrayList<Long> input){
			int total = 0;
			for(int i = 0; i < input.size(); i+=2){
				total+= (input.get(i+1) - input.get(i));
			}
			return total;
		}

		public int StraightReports(){
			if(anomalyDetector.anomalyReports.size() == 0)
				return 0;
			int straightReports = 1;
			String current = anomalyDetector.anomalyReports.get(0).description;
			for(int i = 1; i < anomalyDetector.anomalyReports.size(); i++){
				if(!anomalyDetector.anomalyReports.get(i).description.equals(current)
						|| anomalyDetector.anomalyReports.get(i).timeStep != (anomalyDetector.anomalyReports.get(i-1).timeStep + 1)) {
					straightReports++;
					current = anomalyDetector.anomalyReports.get(i).description;
				}
			}
			return straightReports;
		}//checks the number of straight reports -> straight reports means reports that
		//has the same headline and keeps the time steps.
	}

	//command #6
	public class Exit extends Command{

		public Exit(){
			super("Exit the program");
		}
		@Override
		public void execute(){
			dio.write("bye");
		}
	}

}



