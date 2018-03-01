package com.tigerfixonline.crud.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.tigerfixonline.crud.model.Entity;

public class RemoteReceiver implements BaseReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3550376530985007323L;
	private ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
	private RemoteFileUtil remoteFileUtil = RemoteFileUtil.getStorageInstance();
	private String fileName;
	private String mimeType;
	private String recordNumber;
	private Entity entity;

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
		remoteFileUtil.createBlobAt(fileName, mimeType, UploadBaseFolder.vaadinuploadedimages.name(),
				arrayOutputStream.toByteArray());
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		this.fileName = remoteFileUtil.rename(filename, recordNumber);
		this.mimeType = mimeType;
		arrayOutputStream.reset();
		return arrayOutputStream;
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
