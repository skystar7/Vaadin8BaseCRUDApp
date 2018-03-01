package com.tigerfixonline.crud.components;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tigerfixonline.crud.model.Entity;
import com.tigerfixonline.crud.model.ImageInfo;
import com.tigerfixonline.crud.upload.FileUtil;
import com.tigerfixonline.crud.upload.UploadBaseFolder;
import com.tigerfixonline.crud.upload.UploadedImage;
import com.tigerfixonline.crud.util.ImageUtil;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

public class ImageList extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5158495064365679718L;

	/*
	 * Components
	 */
	private GridLayout gridLayout;
	private UI ui;
	private FileUtil fileUtil;
	private ImageCache cache;

	/*
	 * Constants
	 */
	private final int GRID_Size = 10;

	public ImageList(FileUtil fileUtil, UI ui) {
		this.fileUtil = fileUtil;
		this.ui = ui;
		setWidth(600, Unit.PIXELS);
		setHeight(300, Unit.PIXELS);
		cache = new ImageCache();
		setCaption("Entity name's images");

		gridLayout = new GridLayout(GRID_Size, GRID_Size);
		gridLayout.setSpacing(true);
		gridLayout.setMargin(true);

		setContent(gridLayout);
	}

	public void viewList(Entity entity) {
		List<ImageInfo> images = entity.getImages();

		for (int i = 0; i < images.size(); i++) {
			Image imageComp = new Image();
			downloadImage(images.get(i), imageComp);
			gridLayout.addComponent(imageComp, i, 0);
		}

	}

	private void downloadImage(ImageInfo imageInfo, Image imageComponent) {

		Thread photoDownloaderThread = new Thread(() -> {
			UI.setCurrent(ui);
			ByteArrayInputStream imageStream;

			final UploadedImage image = fileUtil.getImageAt(imageInfo.getName(),
					UploadBaseFolder.vaadinuploadedimages.name());
			boolean isThumbnailExists = fileUtil.isFileExistsAt(imageInfo.getName(),
					UploadBaseFolder.vaadinthumbnails.name());
			byte[] byteArrayThumbnailImage = null;

			if (!isThumbnailExists) {
				/* start timing */
				long startTime = System.nanoTime();

				/* scaling image */
				BufferedImage bufferedImage = image.getBufferedImage();
				byte[] scaledImage = ImageUtil.scaleImage(bufferedImage, /* set scaling size */300,
						imageInfo.getName());
				byteArrayThumbnailImage = scaledImage.clone();
				imageStream = new ByteArrayInputStream(scaledImage);

				/* create thumbnail from cloned byteArray */
				boolean created = fileUtil.createFileAt(imageInfo.getName(), UploadBaseFolder.vaadinthumbnails.name(),
						byteArrayThumbnailImage, UploadBaseFolder.vaadinuploadedimages.name());
				if (created) {
					long endTime = System.nanoTime() - startTime;
					UI.getCurrent().access(() -> {
						Notification.show("Scaling...",
								"Scaling of " + imageInfo.getName() + " took: " + ImageUtil.getScalingTime(endTime),
								Type.TRAY_NOTIFICATION);
					});
				}
			} else {
				/* thumbnail exists */
				imageStream = fileUtil.getImageArrayAt(imageInfo.getName(), UploadBaseFolder.vaadinthumbnails.name());
			}

			StreamResource streamResource = new StreamResource(() -> imageStream, imageInfo.getName());

			/* cache the image */
			cache.put(imageInfo.getName(), streamResource);

			UI.getCurrent().access(() -> {
				StreamResource cachedResource = cache.get(imageInfo.getName());
				imageComponent.setSource(cachedResource);

			});

		});
		photoDownloaderThread.setName("photoDownloaderThread");
		photoDownloaderThread.start();

	}

	public StreamResource getCachedImage(String imageName) {
		return cache.get(imageName);
	}

	public class ImageCache extends LinkedHashMap<String, StreamResource> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3922009934873364710L;

		public ImageCache() {
			super(35 /* initial capacity */, 0.75f /* load factor */, true);

		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, StreamResource> eldest) {
			return size() > 30;
		}
	}

}
