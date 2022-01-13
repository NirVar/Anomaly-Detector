package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class TimeSeries {

	HashMap <String,ArrayList<Float>> table = new HashMap<>();
	int numberOfColumns;
	int totalTimeSteps;
	
	public TimeSeries(String csvFileName) {

		Scanner scanner;
		String[] headers = null;
		String[] values = null;
		String line = null;

		try {
			File file = new File(csvFileName);
			scanner = new Scanner(file);
			// read the first line- headers
			line = scanner.next();
			headers = line.split(",");
			this.numberOfColumns = headers.length;

			// inserts the headers (keys) of the map and empty Arraylist.
			for (int i =0; i< numberOfColumns; i++){
				ArrayList<Float> list = new ArrayList<>();
				table.put(headers[i], list);
			}

			while (scanner.hasNext()){
				line = scanner.next();
				values = line.split(",");
				for (int i =0; i < headers.length; i++){
					table.get(headers[i]).add(Float.parseFloat(values[i]));
				}
			}
			totalTimeSteps= table.get("A").size();
			scanner.close();

		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
