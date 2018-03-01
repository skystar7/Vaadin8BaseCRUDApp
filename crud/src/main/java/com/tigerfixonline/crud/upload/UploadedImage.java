package com.tigerfixonline.crud.upload;

import java.awt.image.BufferedImage;

import com.vaadin.server.Resource;

public abstract class UploadedImage {

	public abstract BufferedImage getBufferedImage();

	public abstract Resource getImageResource();

}
