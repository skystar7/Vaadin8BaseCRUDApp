package com.tigerfixonline.crud.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.Entity;

public class LocalReceiver implements BaseReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3550376530985007323L;
	private String fileName;
	private String recordNumber;
	private String mimeType;
	private LocalFileUtil localFileUtil = LocalFileUtil.getInstance();
	private Entity entity;

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public void setRecordNumber(String recordNumber) {
		this.recordNumber = recordNumber;
	}

	@Override
	public void upload() {

	}

	@Override
	public OutputStream receiveUpload(String receivedFilename, String mimeType) {
		FileOutputStream fileOutputStream = null;
		this.fileName = localFileUtil.rename(receivedFilename, recordNumber);
		this.mimeType = mimeType;
		File uplloadedImageFile = localFileUtil.createFileAt(fileName, UploadBaseFolder.vaadinuploadedimages.name());
		try {
			fileOutputStream = new FileOutputStream(uplloadedImageFile);
		} catch (FileNotFoundException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't find file at receiveUpload(...) for " + receivedFilename, crudException);
		}
		return fileOutputStream;
	}

	@Override
	public Entity getAssociatedEntity() {
		return entity;
	}

	@Override
	public void setAssociatedEntity(Entity associatedEntity) {
		entity = associatedEntity;
	}

}
