package org.newdawn.loaders.resourcepools;

import java.util.HashMap;

/**
 * Generic resource pool abstract class
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.1 $
 */
public abstract class ResourcePool
{

	/** the resources */
	private HashMap<String, Object> resources = new HashMap<String, Object>();

	/**
	 * Gets a resouce from the resouce pool
	 *
	 * @param key The key of the resouce to get
	 * @throws ResourceLoadFailedException Thrown if the resource is not loaded and fails to load.
	 * @return The resouce
	 */
	public Object getResource(String key) throws ResourceLoadFailedException
	{
		Object resource = resources.get(key);
		if (resource == null)
		{
			resource = loadResource(key);
			resources.put(key, resource);
		}
		return resource;
	}

	/**
	 * Abstract load method, all inheriting classes must replace this method
	 * with one that know how to load a resouce based on the key
	 *
	 * @param key They key of the resouce to load
	 * @throws ResourceLoadFailedException Thrown if the resouce fails to load
	 * @return The resouce
	 */
	public abstract Object loadResource(String key) throws ResourceLoadFailedException;
}
