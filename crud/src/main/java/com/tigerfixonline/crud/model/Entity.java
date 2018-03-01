package com.tigerfixonline.crud.model;

import java.io.Serializable;
import java.util.List;

public abstract class Entity implements Serializable, Cloneable, EntityImages {

	/**
	 * 
	 */
	private static final long serialVersionUID = 986840562049836858L;

	@Override
	public Entity clone() {
		Entity cloned = null;
		try {
			cloned = (Entity) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return cloned;
	}

	public abstract Long getId();

	public abstract void setId(Long id);

	@Override
	public Long identifier() {
		return null;
	}

	@Override
	public void addImageInfo(ImageInfo image) {

	}

	@Override
	public void removeImage(ImageInfo image) {

	}

	@Override
	public List<ImageInfo> getImages() {
		return null;
	}
}
