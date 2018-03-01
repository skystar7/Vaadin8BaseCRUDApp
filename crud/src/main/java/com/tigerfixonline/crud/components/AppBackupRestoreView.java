package com.tigerfixonline.crud.components;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.Customer;
import com.tigerfixonline.crud.model.User;
import com.tigerfixonline.crud.oAuth.LoginHandler;
import com.tigerfixonline.crud.persistence.CustomerService;
import com.tigerfixonline.crud.upload.RemoteFileUtil;
import com.tigerfixonline.crud.util.ImageUtil;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class AppBackupRestoreView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4508961352699200631L;
	private static final String dbRemoteBaseFolder = "app-db-backup";
	private final String logsRemoteBaseFolder = "app-logs-folder";
	private Button backupDB = new Button("Backup DB");
	private Button restoreDB = new Button("Restore DB");
	private Button backupLogs = new Button("Backup Logs");
	private Button restoreLogs = new Button("Restore Logs");
	private static final String serializedMimeType = "application/x-java-serialized-object";
	private final String logsMimeType = "text/plain";
	private static final String customerDBName = Customer.class.getSimpleName() + ".data";
	private final String logsFileName = "crudApp.log";

	private static RemoteFileUtil remoteFileUtil = RemoteFileUtil.getStorageInstance();

	/*
	 * Logger
	 */
	private static Logger logger = LoggerUtil.getLogger(AppBackupRestoreView.class);

	/*
	 * Login handler
	 */
	private VaadinSession vaadinSession;
	private LoginHandler loginHandler;
	private User currentUser;
	private boolean initialized;

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		/* get current session, and retrieve User */
		vaadinSession = VaadinSession.getCurrent();
		currentUser = vaadinSession.getAttribute(User.class);

		/* loginHandler */
		logger.info("forward login request");
		loginHandler = new LoginHandler(this);
		loginHandler.setViewName("appBackupRestore");
		/* view name */

		/*
		 * Check whether or not User is logged in, otherwise offer a link for a log-in
		 */

		if (!currentUser.isLogged()) {
			loginHandler.generateLoginLink();
		} else if (currentUser.isLogged() && (!currentUser.isAuthorized())) {
			loginHandler.unauthorizedUser();
		} else {
			loginHandler.addUserUI();

			if (!initialized) {
				/* initialize components */
				initializeComponents();
				initialized = true;
			}
		}
	}

	public AppBackupRestoreView() {

	}

	private void initializeComponents() {
		backupDB.addClickListener(clicked -> backup(customerDBName, serializedMimeType, "data", dbRemoteBaseFolder, true));

		if (remoteFileUtil.isBlobExistsAt(customerDBName, dbRemoteBaseFolder)) {
			addComponents(restoreDB);
			restoreDB.addClickListener(clicked -> restore(customerDBName, "data", dbRemoteBaseFolder, true));
		}

		backupLogs.addClickListener(clicked -> backup(logsFileName, logsMimeType, null, logsRemoteBaseFolder, true));

		if (remoteFileUtil.isBlobExistsAt(logsFileName, logsRemoteBaseFolder)) {
			addComponents(restoreLogs);
			restoreLogs.addClickListener(clicked -> restore(logsFileName, null, logsRemoteBaseFolder, true));
		}

		addComponents(backupDB, backupLogs);
	}

	public static void backupDB() {
		backup(customerDBName, serializedMimeType, "data", dbRemoteBaseFolder, false);
	}

	public static void restoreDB() {
		if (remoteFileUtil.isBlobExistsAt(customerDBName, dbRemoteBaseFolder))
			restore(customerDBName, "data", dbRemoteBaseFolder, false);
	}

	private static void backup(String fileName, String mimeType, String localBaseFolder, String remoteBaseFolder,
			boolean ui) {
		File localFile = null;
		if (localBaseFolder != null) {
			File localParentFolder = new File(localBaseFolder);
			localFile = new File(localParentFolder, fileName);
		} else {
			localFile = new File(fileName);
		}

		try {
			FileInputStream backupFile = new FileInputStream(localFile);
			byte[] backupFileArray = ImageUtil.getFileArray(backupFile);
			boolean isBackUpCreated = remoteFileUtil.createBlobAt(fileName, mimeType, remoteBaseFolder,
					backupFileArray);
			if (isBackUpCreated) {
				logger.info("DB Backup created... at " + new Date());
				if (ui)
					Notification.show("Backup created...", "Your file has been backedup...",
							Notification.Type.TRAY_NOTIFICATION);
			}

		} catch (FileNotFoundException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't find backupfile locally...", crudException);
		}
	}

	private static void restore(String fileName, String localBaseFolder, String remoteBaseFolder, boolean ui) {
		File localFile = null;
		if (localBaseFolder != null) {
			File localParentFolder = new File(localBaseFolder);
			if (!localParentFolder.exists())
				localParentFolder.mkdir();
			localFile = new File(localParentFolder, fileName);
		} else {
			localFile = new File(fileName);
		}

		ByteArrayInputStream backupFileInputStream = remoteFileUtil.getBlobAt(fileName, remoteBaseFolder);
		OutputStream out = null;
		try {
			try {
				out = new FileOutputStream(localFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = backupFileInputStream.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} finally {
				backupFileInputStream.close();
				out.close();
				Notification.show("Backup restored...", "Your file has been restored...",
						Notification.Type.TRAY_NOTIFICATION);
			}
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't restore backupfile...", crudException);
		}
		CustomerService customerService = CustomerService.getInstance();
		customerService.derializeCustomers();
	}

}
