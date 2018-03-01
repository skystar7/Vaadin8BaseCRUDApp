package com.tigerfixonline.crud.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.tigerfixonline.crud.model.Customer;
import com.tigerfixonline.crud.model.Entity;

public class CustomerService implements BaseService {

	private static CustomerService customerService;
	private Map<Long, Customer> customers;
	private Long currentID;

	private CustomerService() {
		customers = new HashMap<>();
		// generateSampleData();
		/* read previous data if any */
		derializeCustomers();
	}

	@SuppressWarnings("unused")
	private void generateSampleData() {
		Set<Customer> generatedCustomersSet = CollectionSerializerTester.generateCustomersSet();
		SortedSet<Customer> sortedCustomers = new TreeSet<>(generatedCustomersSet);
		for (Customer customer : sortedCustomers) {
			customers.put(customer.getId(), customer);
		}
	}

	public static CustomerService getInstance() {
		if (customerService == null)
			customerService = new CustomerService();
		return customerService;
	}

	@Override
	public Long getNextID() {
		if (customers.size() == 0) {
			currentID = new Long(0l);
			currentID++;
			return currentID;
		} else {
			NavigableMap<Long, Customer> sortedMap = new TreeMap<>(customers);
			return sortedMap.lastKey() + 1;
		}
	}

	@Override
	public Entity getEntityByID(Long id) {
		return customers.get(id);
	}

	@Override
	public boolean addEntity(Entity entity) {
		Customer customer = (Customer) entity;
		Long id = getNextID();
		customer.setId(id);
		customers.put(id, customer);
		CollectionSerializer.serialize(new HashSet<>(customers.values()), Customer.class);
		return CollectionSerializer.isSame(new HashSet<>(customers.values()), Customer.class);
	}

	@Override
	public boolean deleteEntity(Entity entity) {
		Customer customer = (Customer) entity;
		customers.remove(customer.getId());
		CollectionSerializer.serialize(new HashSet<>(customers.values()), Customer.class);
		return CollectionSerializer.isSame(new HashSet<>(customers.values()), Customer.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean batchDelete(Set<?> batch) {
		Set<Customer> customersBatch = (Set<Customer>) batch;
		for (Customer tobeDelete : customersBatch)
			customers.remove(tobeDelete.getId());
		CollectionSerializer.serialize(new HashSet<>(customers.values()), Customer.class);
		return CollectionSerializer.isSame(new HashSet<>(customers.values()), Customer.class);
	}

	@Override
	public boolean updateEntity(Entity entity) {
		Customer customer = (Customer) entity;
		Customer checkCustomer = customers.get(customer.getId());
		/* no key found */
		if (checkCustomer == null) {
			return addEntity(customer);
		} else {
			/* update key with new value */
			customers.put(customer.getId(), customer);
			CollectionSerializer.serialize(new HashSet<>(customers.values()), Customer.class);
			return CollectionSerializer.isSame(new HashSet<>(customers.values()), Customer.class);
		}

	}

	/*
	 * Read previous data
	 */
	@SuppressWarnings("unchecked")
	public void derializeCustomers() {
		Set<Customer> deserializedCustomers = (Set<Customer>) CollectionSerializer.deserialize(Customer.class);
		if (!deserializedCustomers.isEmpty()) {
			for (Customer customer : deserializedCustomers) {
				customers.put(customer.getId(), customer);
			}
		}
		/**
		 * no else hence new map is instantiated at the constructor
		 */
	}

	@Override
	public boolean isPersisted(Entity entity) {
		Customer customer = (Customer) entity;
		return customers.get(customer.getId()) == null ? false : true;
	}

	@Override
	public List<Customer> allEntities() {
		return new ArrayList<>(customers.values());
	}

	@Override
	public List<Customer> filterEntities(String filter) {
		if ("".equals(filter))
			return allEntities();
		List<Customer> result = new ArrayList<>();
		for (Customer customer : customers.values()) {
			if (customer.fieldValues().toLowerCase().contains(filter.toLowerCase()))
				result.add(customer);
		}
		return result;
	}

}
