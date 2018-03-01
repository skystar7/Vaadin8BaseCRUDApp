package com.tigerfixonline.crud.upload;

import com.vaadin.server.Resource;

public class ImageResource {
	private Resource resource;
	private byte[] imageArray;

	public ImageResource() {
		super();
	}

	public byte[] getImageArray() {
		return imageArray;
	}

	public void setImageArray(byte[] imageArray) {
		this.imageArray = imageArray;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}