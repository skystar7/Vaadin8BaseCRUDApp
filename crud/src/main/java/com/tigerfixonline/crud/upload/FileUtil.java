package com.tigerfixonline.crud.upload;

import java.io.ByteArrayInputStream;

public interface FileUtil {

	boolean createFileAt(String fileName, String baseFolder, byte[] array);

	boolean createFileAt(String fileName, String mimeType, String baseFolder, byte[] array);

	boolean createFileAt(String fileName, String baseFolder, byte[] array, String located);

	boolean deleteFileAt(String fileName, String baseFolder);

	long getFileSize(String fileName, String baseFolder);

	boolean isFileExistsAt(String fileName, String baseFolder);

	ByteArrayInputStream getImageArrayAt(String fileName, String baseFolder);

	UploadedImage getImageAt(String fileName, String baseFolder);

	/**
	 * Rename is by appending filename with {@code _} recordNumber
	 * 
	 * @param filename
	 * @param recordNumber
	 * @return
	 */
	public default String rename(String filename, String recordNumber) {
		String name = filename.substring(0, filename.lastIndexOf('.'));
		String extention = filename.substring(filename.lastIndexOf('.') + 1);
		String renamed = name + "_" + recordNumber + "." + extention;
		return renamed;
	}

	public String serviceName();

}