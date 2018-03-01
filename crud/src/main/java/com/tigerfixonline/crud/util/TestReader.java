package com.tigerfixonline.crud.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/*
 * TEST CLASS
 */
public class TestReader {

	public static void main(String[] args) {
		Scanner scanner = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File("all_cities"));
			scanner = new Scanner(new File("cities.data"));
			scanner.useDelimiter("\\r\\n");
			while (scanner.hasNext()) {
				String next = scanner.next();
				String add = "\"" + next + "\"";
				System.out.println(add);
				writer.print(add);
				writer.print(",");
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
