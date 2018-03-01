package com.tigerfixonline.crud.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigerfixonline.crud.components.AppBackupRestoreView;
import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;

public class CollectionSerializer {

	/*
	 * Logger
	 */
	private static Logger logger = LoggerUtil.getLogger(CollectionSerializer.class);

	public static void serialize(Set<?> writeSet, Class<?> type) {
		ObjectOutputStream objectOutputStream = null;
		try {
			try {
				File data = new File("data");
				if (!data.exists())
					data.mkdir();
				String typeName = type.getSimpleName() + ".data";
				File serializedFile = new File(data, typeName);
				FileOutputStream fileOutputStream = new FileOutputStream(serializedFile);
				objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(writeSet);
				objectOutputStream.flush();
			} finally {
				objectOutputStream.close();

				/*
				 * Backup DB to cloud
				 */
				Thread dbBackup = new Thread(() -> {
					AppBackupRestoreView.backupDB();
				});
				dbBackup.setName("dbBackup thread");
				dbBackup.start();
			}

		} catch (FileNotFoundException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't find a file while serializing ", crudException);
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "IOException while serializing ", crudException);
		}

	}

	/**
	 * 
	 * @param type
	 * 
	 * @return {@code new HashSet<>()} if no set is serialized
	 */
	public static Set<?> deserialize(Class<?> type) {
		/*
		 * Restore DB from cloud
		 */
//		AppBackupRestoreView.restoreDB();

		Set<?> readSet = null;
		String typeName = type.getSimpleName() + ".data";
		Set<?> deserializedSet = null;

		try {
			File data = new File("data");
			File serializedFile = new File(data, typeName);
			if (serializedFile.exists()) {
				FileInputStream fileInputStream = new FileInputStream(serializedFile);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				readSet = (Set<?>) objectInputStream.readObject();
				objectInputStream.close();
				if (readSet != null)
					deserializedSet = new HashSet<>(readSet);
			} else
				deserializedSet = new HashSet<>();

		} catch (FileNotFoundException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't find a file while deserializing ", crudException);
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "IOException while deserializing ", crudException);
		} catch (ClassNotFoundException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "ClassNotFoundException while deserializing ", crudException);
		}

		return deserializedSet;
	}

	/**
	 * Is the passed set the same as the deserialized set
	 * 
	 * @param set
	 * @param type
	 * 
	 * @return
	 */
	public static boolean isSame(Set<?> set, Class<?> type) {
		return set.equals(deserialize(type));
	}

}
