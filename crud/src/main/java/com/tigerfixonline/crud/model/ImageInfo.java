package com.tigerfixonline.crud.model;

import java.io.Serializable;

public class ImageInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4732107173989295790L;
	private String name;
	private String mimeType;
	private Long length;

	public ImageInfo() {
	}

	public ImageInfo(String name, String mimeType, Long length) {
		super();
		this.name = name;
		this.mimeType = mimeType;
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return "ImageInfo [name=" + name + ", mimeType=" + mimeType + ", length=" + length + "]";
	}

}
