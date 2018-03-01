package com.tigerfixonline.crud.upload;

import com.tigerfixonline.crud.model.Entity;
import com.vaadin.ui.Upload.Receiver;

public interface BaseReceiver extends Receiver {

	Entity getAssociatedEntity();

	void setAssociatedEntity(Entity associatedEntity);

	String getFileName();

	String getMimeType();

	void setRecordNumber(String recordNumber);

	void upload();

}