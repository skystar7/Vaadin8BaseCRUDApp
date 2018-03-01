package com.tigerfixonline.crud.persistence;

import java.util.List;
import java.util.Set;

import com.tigerfixonline.crud.model.Entity;

public interface BaseService {

	/**
	 * Key policy is a continuation from last assigned key
	 * 
	 * @return new key
	 */
	
	Entity getEntityByID(Long id);
	
	Long getNextID();

	boolean addEntity(Entity entity);

	boolean deleteEntity(Entity entity);

	boolean batchDelete(Set<?> batch);

	boolean updateEntity(Entity entity);

	boolean isPersisted(Entity entity);

	List<?> allEntities();

	List<?> filterEntities(String filter);

}