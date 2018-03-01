package com.tigerfixonline.crud.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {

	private static Handler fileHandler;

	public static Logger getLogger(Class<?> clazz) {
		Logger myLogger = Logger.getLogger(clazz.getName());
		myLogger.setLevel(Level.FINE);
		myLogger.setUseParentHandlers(false);

		if (fileHandler == null) {
			try {
				fileHandler = new FileHandler("crudApp.log", 0, 1, false);
				fileHandler.setLevel(Level.FINE);
				fileHandler.setFormatter(new SimpleFormatter());
			} catch (SecurityException | IOException e) {
				e.printStackTrace();
			}
		}

		// handler.setFormatter(new XMLFormatter());
		if (myLogger.getHandlers().length == 0)
			myLogger.addHandler(fileHandler);
		return myLogger;
	}

	public static String logsReader() {
		File logs = new File("crudApp.log");
		if (!logs.exists())
			return "";

		Scanner scanner = null;
		StringBuilder logsBuilder = new StringBuilder();

		try {
			scanner = new Scanner(logs);
			scanner.useDelimiter("\n");
			while (scanner.hasNext()) {
				String next = scanner.next();
				logsBuilder.append(next);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return logsBuilder.toString();

	}

}
