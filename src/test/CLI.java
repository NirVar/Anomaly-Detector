package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();

		commands.add(c.new PrintMenuCommand());
		commands.add(c.new uploadTimeSeries());
		commands.add(c.new AlgorithmSetting());
		commands.add(c.new DetectAnomalies());
		commands.add(c.new DisplayResults());
		commands.add(c.new UploadAndAnalyze());
		commands.add(c.new Exit());
	}
	
	public void start() {
		commands.get(0).execute();
		String userInput = dio.readText();
		while (!userInput.equals("6")){
			if (userInput.equals("")) {
				userInput = dio.readText();
				continue;
			}
			commands.get(Integer.parseInt(userInput)).execute();
			commands.get(0).execute();
			userInput = dio.readText();
		}

		commands.get(6).execute();
	}
//	public void start() {
//		commands.get(0).execute();
//		String action = dio.readText();
//		while(!action.equals("6")) {
//
//			commands.get(Integer.parseInt(action)).execute();
//			commands.get(0).execute();
//			action = dio.readText();
//		}
//		commands.get(6).execute();
//	}
}
