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
		commands.add(c.new AlgorithmSetting());
		commands.add(c.new DetectAnomalies());
		commands.add(c.new DisplayResults());
		commands.add(c.new UploadAndAnalyze());
		commands.add(c.new Exit());
	}
	
	public void start() {
		commands.get(0).execute();
		String userInput = dio.readText();
		while (Integer.parseInt(userInput) != 6){
			commands.get(Integer.parseInt(userInput)).execute();
			commands.get(0).execute();
			userInput = dio.readText();
		}
		commands.get(6).execute();
	}
}
