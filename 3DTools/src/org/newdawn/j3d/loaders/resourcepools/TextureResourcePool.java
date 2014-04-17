package org.newdawn.j3d.loaders.resourcepools;

import java.net.MalformedURLException;
import java.net.URL;

import javax.media.j3d.Texture;

import org.newdawn.loaders.resourcepools.ResourceLoadFailedException;
import org.newdawn.loaders.resourcepools.ResourcePool;

import tools.image.J3dSimpleImageLoader;

public class TextureResourcePool extends ResourcePool
{

	private static TextureResourcePool instance = new TextureResourcePool();

	@Override
	public Object loadResource(String key) throws ResourceLoadFailedException
	{
		URL location;
		try
		{
			location = new URL(key);
		}
		catch (MalformedURLException e)
		{
			throw new ResourceLoadFailedException(key + " is an invalid location" + e.getMessage());
		}
		return loadResource(location);
	}

	public Object loadResource(URL location)
	{
		String fileName = location.getFile();
		if (fileName.startsWith("\\"))
			fileName = fileName.substring(1);
		return J3dSimpleImageLoader.getTexture(fileName);
	}

	public static Texture getTexture(URL location) throws ResourceLoadFailedException
	{
		return (Texture) instance.getResource(location.toString());
	}

}
