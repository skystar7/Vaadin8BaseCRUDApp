package com.tigerfixonline.crud.persistence;

import java.util.HashSet;
import java.util.Set;

import com.tigerfixonline.crud.model.Customer;

/**
 * Some test time
 * 
 * ~73_565_996 Just serializtion
 * 
 * ~344_222_933 Serializtion plus derializtion
 * 
 * ~283_064_442 Serializtion, derializtion, plus comparing
 * 
 * Total 0.283064442 second or 283 milisecond
 * 
 * @author Ahmad
 *
 */
public class CollectionSerializerTester {

	private final static int RECORDS = 1_000;

	public static void main(String[] args) {
		Set<Customer> generatedCustomersSet = generateCustomersSet();
		long startTime = System.nanoTime();
		CollectionSerializer.serialize(generatedCustomersSet, Customer.class);
		Set<?> deserializedSet = CollectionSerializer.deserialize(Customer.class);
		boolean equals = generatedCustomersSet.equals(deserializedSet);
		System.out.println(equals);
		long endTime = System.nanoTime() - startTime;
		System.out.println(endTime);
	}

	public static Set<Customer> generateCustomersSet() {
		Set<Customer> set = new HashSet<>();

		for (int i = 0; i < RECORDS; i++) {
			Customer customer = new Customer();
			customer.setId(new Long(i));
			customer.setName("record name " + i);
			String phoneEnding = String.valueOf(i);
			switch (phoneEnding.length()) {
			case 1:
				customer.setPhone("770-123-000" + i);
				break;
			case 2:
				customer.setPhone("770-123-00" + i);
				break;
			case 3:
				customer.setPhone("770-123-0" + i);
				break;
			}
			customer.setEmail("record_email@somedomain" + i + ".com");
			customer.setWebsite("record_website.domain.com");
			customer.setDescription(longParagraphGenerator());
			set.add(customer);
		}
		return set;
	}

	private static String longParagraphGenerator() {
		String line = "This is some sample test paragraph, ";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(line);
		for (int i = 0; i < 30; i++) {
			stringBuilder.append(line);
		}
		stringBuilder.append(".");
		return stringBuilder.toString();
	}

}
