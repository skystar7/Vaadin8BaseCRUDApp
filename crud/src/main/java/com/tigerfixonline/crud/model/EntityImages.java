package com.tigerfixonline.crud.model;

import java.util.List;

public interface EntityImages {

	Long identifier();
	void addImageInfo(ImageInfo image);
	void removeImage(ImageInfo image);
	List<ImageInfo> getImages();

}
