package com.tigerfixonline.crud.util;

import java.util.Random;

public class RandomUniqueID {

	private static String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String digits = "0123456789";
	private final static Random random = new Random();

	public static void main(String[] args) {
		String generateID = generateID(25);
		System.out.println(generateID);
	}

	public static String generateID(int length) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i % 2 == 0 || i == 0) {
				int charcter = random.nextInt(characters.length());
				stringBuilder.append(characters.charAt(charcter));
			} else {
				int digit = random.nextInt(digits.length());
				stringBuilder.append(digits.charAt(digit));
			}
		}
		return stringBuilder.toString();
	}

}