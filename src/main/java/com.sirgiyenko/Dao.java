package com.sirgiyenko;

import java.util.List;

public interface Dao {

	/**
	 * Stores given entity
	 * @param entity an entity to store
	 * @return true if entity was stored successfully, false if current entity is already in storage
	 * @throws NetworkException in case any issue occurred with storage connection
	 */
	boolean store(Entity entity) throws NetworkException;

	/**
	 * Retrieve all stored entities
	 * @return {@link java.util.List} of {@link Entity}
	 * @throws NetworkException in case any issue occurred with storage connection
	 */
	List<Entity> findAll() throws NetworkException;
}