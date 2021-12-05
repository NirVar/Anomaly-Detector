package test;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;



public class TimeSeries {

	HashMap <String,ArrayList<Float>> table = new HashMap<>();
	
	public TimeSeries(String csvFileName) {
		Scanner scanner;
		try {
			File file = new File(csvFileName);
			scanner = new Scanner(file);
			while (scanner.hasNextLine()){
				String data = scanner.nextLine();

			}

		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
