package com.tigerfixonline.crud.upload;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * NOT USED
 */
public class ImageCache extends LinkedHashMap<String, ImageResource> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3922009934873364710L;

	public ImageCache() {
		super(55 /* initial capacity */, 0.75f /* load factor */, true);

	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<String, ImageResource> eldest) {
		return size() > 50;
	}
}