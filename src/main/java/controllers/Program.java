package controllers;

import lib.ConsoleIO;

public class Program {
	public static void Run() {
		boolean stop = false;
		while(!stop) {
			try {
				String input = ConsoleIO.promptForInput("Enter Query (type stop to exit)", false);
				stop = input.equals("stop") || input.equals("Stop");
				if(!stop) {
					Query.Execute(input);									
				}
			} catch(IllegalArgumentException IAE) {
				System.out.println(IAE.getMessage());
			}
		}
	}
}
