package com.tigerfixonline.crud.upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;

public class LocalFileUtil implements FileUtil {

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	private static LocalFileUtil localFileUtil;

	private LocalFileUtil() {

	}

	public static LocalFileUtil getInstance() {
		if (localFileUtil == null)
			localFileUtil = new LocalFileUtil();
		return localFileUtil;
	}

	public File createFileAt(String fileName, String baseFolder) {
		File base = new File(baseFolder);
		if (!base.exists())
			base.mkdir();
		return new File(baseFolder, fileName);
	}

	@Override
	public boolean createFileAt(String fileName, String baseFolder, byte[] array, String located) {
		File file = null;
		try {
			file = createFileAt(fileName, baseFolder);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(array, 0, array.length);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't find a file for  " + fileName + " at " + baseFolder, crudException);
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "IOException for  " + fileName + " at " + baseFolder, crudException);
		}
		return file.exists();
	}

	@Override
	public boolean createFileAt(String fileName, String baseFolder, byte[] array) {
		return createFileAt(fileName, baseFolder, array, "");
	}

	@Override
	public boolean createFileAt(String fileName, String mimeType, String baseFolder, byte[] array) {
		return createFileAt(fileName, baseFolder, array, "");
	}

	@Override
	public boolean deleteFileAt(String fileName, String baseFolder) {
		File file = getFileAt(fileName, baseFolder);
		boolean deleted = false;
		try {
			deleted = Files.deleteIfExists(file.toPath());
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "IOException while deleting " + fileName + " at " + baseFolder, crudException);
		}

		return deleted;
	}

	@Override
	public boolean isFileExistsAt(String fileName, String baseFolder) {
		File file = getFileAt(fileName, baseFolder);
		return file.exists();
	}

	public File getFileAt(String fileName, String baseFolder) {
		File file = new File(baseFolder, fileName);
		return file;
	}

	@Override
	public long getFileSize(String fileName, String baseFolder) {
		return getFileAt(fileName, baseFolder).length();
	}

	@Override
	public ByteArrayInputStream getImageArrayAt(String fileName, String baseFolder) {
		File file = getFileAt(fileName, baseFolder);
		ByteArrayInputStream imageStream = null;
		try {
			InputStream inputStream = Files.newInputStream(file.toPath());
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int read = 0;
			byte[] temp = new byte[1024];
			while ((read = inputStream.read(temp, 0, temp.length)) != -1) {
				buffer.write(temp, 0, read);
			}
			buffer.flush();
			imageStream = new ByteArrayInputStream(buffer.toByteArray());
			buffer.close();
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "IOException at getImageArrayAt(...) for " + fileName + " at " + baseFolder,
					crudException);
		}
		return imageStream;
	}

	private BufferedImage getBufferedImageAt(String fileName, String baseFolder) {
		File file = getFileAt(fileName, baseFolder);
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(file);
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "IOException at getBufferedImageAt(...) for " + fileName + " at " + baseFolder,
					crudException);
		}
		return bufferedImage;
	}

	@Override
	public UploadedImage getImageAt(String fileName, String baseFolder) {
		return new LocalImage(fileName, baseFolder);
	}

	public boolean foundDuplicateAt(String fileName, String baseFolder) {
		File base = new File(baseFolder);
		if (!base.exists())
			return false;
		for (File file : base.listFiles()) {
			if (file.getName().equals(fileName))
				return true;
		}
		return false;
	}

	public class LocalImage extends UploadedImage {

		private String imageName;
		private String baseFolder;
		private File imageFile;
		private BufferedImage bufferedImage;

		public LocalImage(String name, String folder) {
			this.imageName = name;
			this.baseFolder = folder;
			imageFile = LocalFileUtil.this.getFileAt(imageName, baseFolder);

		}

		@Override
		public Resource getImageResource() {
			return new FileResource(imageFile);
		}

		@Override
		public BufferedImage getBufferedImage() {
			bufferedImage = LocalFileUtil.this.getBufferedImageAt(imageName, baseFolder);
			return bufferedImage;
		}

	}

	@Override
	public String serviceName() {
		return "Local file service";
	}

}
