package com.tigerfixonline.crud.components;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.Entity;
import com.tigerfixonline.crud.model.ImageInfo;
import com.tigerfixonline.crud.persistence.BaseService;
import com.tigerfixonline.crud.upload.FileUtil;
import com.tigerfixonline.crud.upload.ImageResource;
import com.tigerfixonline.crud.upload.UploadBaseFolder;
import com.tigerfixonline.crud.upload.UploadedImage;
import com.tigerfixonline.crud.util.ImageUtil;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ImageViewer extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7201163024118525316L;

	/*
	 * Controls
	 */
	private Image currentImage;
	private Link currentLink;
	private Image previous, next, delete;
	private Label imageSize;
	private Label numberOfImages;
	private ProgressBar progressBar;

	/*
	 * Layout
	 */
	private VerticalLayout content;
	private HorizontalLayout controlLayout;
	private HorizontalLayout functionLayout;

	/*
	 * Component logic
	 */
	private FileResource noImage;

	/*
	 * UI State
	 */
	private int currentIndex;
	private int listSize;
	private List<ImageInfo> images;
	private ImageInfo currentImageInfo;
	private ConcurrentLinkedHashMap<String, ImageResource> cache;
	private Lock cacheLock;
	private boolean editMode;

	/*
	 * Model
	 */
	private Entity currentEntity;
	private BaseService baseService;

	/*
	 * Util
	 */
	private FileUtil fileUtil;

	/*
	 * Main View
	 */
	private EntityView entityView;

	/*
	 * Servlet Context
	 */
	private ServletContext servletContext;

	/*
	 * Constant
	 */
	private final int image_scalling_size = 300;

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	@SuppressWarnings("unchecked")
	public ImageViewer(EntityView mView) {
		entityView = mView;

		servletContext = VaadinServlet.getCurrent().getServletContext();
		/* instantiate cache */
		cache = (ConcurrentLinkedHashMap<String, ImageResource>) servletContext.getAttribute("cache");
		cacheLock = (Lock) servletContext.getAttribute("cacheLock");

		next = new Image();
		next.setSource(new FileResource(new File("resources/images/next.png")));
		next.addClickListener(next -> {
			currentIndex = (currentIndex + 1) % listSize;
			viewImages();
		});

		previous = new Image();
		previous.setSource(new FileResource(new File("resources/images/previous.png")));
		previous.addClickListener(previous -> {
			currentIndex = (currentIndex - 1) % listSize;
			viewImages();
		});

		delete = new Image();
		delete.setCaption("Delete Image");
		delete.setSource(new FileResource(new File("resources/images/delete.png")));
		delete.addClickListener(previous -> {
			currentEntity.removeImage(currentImageInfo);

			boolean updateCustomer = baseService.updateEntity(currentEntity);
			if (updateCustomer)
				logger.info(currentImageInfo.getName() + " removed from entity " + currentEntity);
			boolean isImageFilesDeleted = deleteImageFiles();
			if (isImageFilesDeleted)
				logger.info(currentImageInfo.getName() + " original and thumbnail are deleted from "
						+ fileUtil.serviceName());
			if (isImageFilesDeleted && updateCustomer) {
				Notification.show("Deleting...", "Your image " + currentImageInfo.getName() + " has been deleted",
						Type.TRAY_NOTIFICATION);
			}

			else {
				logger.warning(currentImageInfo.getName() + " original and thumbnail are NOT deleted from "
						+ fileUtil.serviceName());
				Notification.show("Deleting...", "Your image " + currentImageInfo.getName() + "  has NOT been deleted",
						Type.ERROR_MESSAGE);
			}

			checkHasImages(true);
		});

		imageSize = new Label();
		numberOfImages = new Label();
		content = new VerticalLayout();
		controlLayout = new HorizontalLayout();
		functionLayout = new HorizontalLayout();
		currentImage = new Image();
		progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		noImage = new FileResource(new File("resources/images/noImage.png"));
		currentImage.setSource(noImage);
		currentLink = new Link();
		functionLayout.addComponent(currentLink);

		content.addComponents(currentImage, functionLayout, controlLayout);
		content.setComponentAlignment(controlLayout, Alignment.MIDDLE_CENTER);
		addComponents(progressBar, content);

	}

	private boolean deleteImageFiles() {
		boolean deleteUploadedImages = false;
		boolean deleteThumbnails = false;
		deleteUploadedImages = fileUtil.deleteFileAt(currentImageInfo.getName(),
				UploadBaseFolder.vaadinuploadedimages.name());
		deleteThumbnails = fileUtil.deleteFileAt(currentImageInfo.getName(), UploadBaseFolder.vaadinthumbnails.name());
		return deleteUploadedImages && deleteThumbnails;
	}

	public void setEntity(Entity selctedEntity, BaseService service, boolean first) {
		this.currentEntity = selctedEntity;
		this.baseService = service;
		checkHasImages(first);
	}

	public void setFileUtil(FileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	private void checkHasImages(boolean first) {
		/* entity has no images */
		if (currentEntity.getImages().isEmpty()) {
			currentImage.setCaption(null);
			currentImage.setSource(noImage);
			currentLink.setCaption(null);
			currentLink.setResource(null);
			controlLayout.removeComponent(next);
			controlLayout.removeComponent(previous);
			functionLayout.removeComponent(delete);
			functionLayout.removeComponent(imageSize);
			functionLayout.removeComponent(numberOfImages);
		} else {
			images = currentEntity.getImages();
			listSize = images.size();
			if (first)
				currentIndex = 0;
			else
				currentIndex = listSize - 1;
			currentLink.setCaption("Original size");
			functionLayout.addComponent(delete);
			functionLayout.addComponent(imageSize);
			functionLayout.addComponent(numberOfImages);
			viewImages();
		}
	}

	public void setEditMode(boolean edit) {
		editMode = true;
	}

	public boolean isEditMode() {
		return editMode;
	}

	private void viewImages() {
		currentImageInfo = images.get(currentIndex);
		numberOfImages.setCaption("Image: " + String.valueOf(currentIndex + 1) + " of " + listSize);

		/*
		 * Update UI
		 */
		if (showPrevious(currentIndex))
			controlLayout.addComponent(previous);
		else
			controlLayout.removeComponent(previous);

		if (showNext(currentIndex))
			controlLayout.addComponent(next);
		else
			controlLayout.removeComponent(next);

		/*
		 * standalone View
		 */
		if (entityView != null) {
			if (entityView.isEditMode()) {
				delete.setVisible(true);
			} else {
				delete.setVisible(false);
			}
		} else {
			if (editMode) {
				delete.setVisible(true);
			} else {
				delete.setVisible(false);
			}
		}

		if (!isImageCached(currentImageInfo)) {
			// System.out.println("Cache not found for " + currentImageInfo.getName());
			cacheImage(true, currentImageInfo);

			for (int i = currentIndex + 1; i < images.size(); i++)
				cacheImage(false, images.get(i));

		} else {
			// System.out.println("Cache found for " + currentImageInfo.getName());
			updateImageInfo(currentImageInfo);
		}

	}

	private void cacheImage(boolean updateUI, ImageInfo imageInfo) {

		if (updateUI) {
			/* cache the image */
			progressBar.setVisible(true);
			content.setVisible(false);
		}

		UI ui = UI.getCurrent();
		Thread photoDownloaderThread = new Thread(() -> {
			UI.setCurrent(ui);
			ByteArrayInputStream imageStream;

			final UploadedImage image = fileUtil.getImageAt(imageInfo.getName(),
					UploadBaseFolder.vaadinuploadedimages.name());
			boolean isThumbnailExists = fileUtil.isFileExistsAt(imageInfo.getName(),
					UploadBaseFolder.vaadinthumbnails.name());
			byte[] byteArrayThumbnailImage = null;
			ImageResource imageResource = new ImageResource();

			if (!isThumbnailExists) {
				/* start timing */
				long startTime = System.nanoTime();

				/* scaling image */
				BufferedImage bufferedImage = image.getBufferedImage();
				byte[] scaledImage = ImageUtil.scaleImage(bufferedImage, /* set scaling size */image_scalling_size,
						imageInfo.getName());
				byteArrayThumbnailImage = scaledImage.clone();
				imageStream = new ByteArrayInputStream(scaledImage);

				/* create thumbnail from cloned byteArray */
				boolean created = fileUtil.createFileAt(imageInfo.getName(), UploadBaseFolder.vaadinthumbnails.name(),
						byteArrayThumbnailImage, UploadBaseFolder.vaadinuploadedimages.name());
				if (created) {
					long endTime = System.nanoTime() - startTime;
					logger.info("Scaling of " + imageInfo.getName() + " took: " + ImageUtil.getScalingTime(endTime));
				}
			} else {
				/* thumbnail exists */
				imageStream = fileUtil.getImageArrayAt(imageInfo.getName(), UploadBaseFolder.vaadinthumbnails.name());
			}

			byte[] imageArray = ImageUtil.getFileArray(imageStream);
			imageResource.setResource(image.getImageResource());
			imageResource.setImageArray(imageArray);

			try {
				cacheLock.lock();
				/* cache the image */
				cache.put(imageInfo.getName(), imageResource);
			} finally {
				cacheLock.unlock();
			}

			if (updateUI) {
				/* update UI */
				UI.getCurrent().access(() -> {
					progressBar.setVisible(false);
					content.setVisible(true);
					updateImageInfo(imageInfo);
				});
			}

		});
		photoDownloaderThread.setName("photoDownloaderThread");
		photoDownloaderThread.start();
	}

	private boolean isImageCached(ImageInfo imageInfo) {
		boolean isCached = false;
		ImageResource imageResource = null;
		try {
			cacheLock.lock();
			imageResource = cache.get(imageInfo.getName());
		} finally {
			cacheLock.unlock();
		}

		if (imageResource != null)
			isCached = true;
		return isCached;
	}

	private void updateImageInfo(ImageInfo imageInfo) {
		ImageResource imageResource = null;
		StreamResource streamResource = null;
		try {
			cacheLock.lock();
			imageResource = cache.get(imageInfo.getName());
			byte[] clonedImageArray = imageResource.getImageArray().clone();
			streamResource = new StreamResource(() -> new ByteArrayInputStream(clonedImageArray), imageInfo.getName());
		} finally {
			cacheLock.unlock();
		}

		imageSize.setCaption("Original image size: " + ImageUtil.getImageSize(imageInfo.getLength()));
		currentImage.setCaption(imageInfo.getName());
		currentImage.setSource(streamResource);
		currentLink.setCaption(imageInfo.getName());
		currentLink.setResource(imageResource.getResource());
		currentLink.setTargetName("_blank");
		currentLink.setTargetBorder(BorderStyle.NONE);
		currentLink.setTargetHeight(300);
		currentLink.setTargetWidth(400);
	}

	private boolean showNext(int index) {
		return index < listSize - 1;
	}

	private boolean showPrevious(int index) {
		return index > 0;
	}

}
