package com.tigerfixonline.crud.components;

import java.util.logging.Logger;

import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.User;
import com.tigerfixonline.crud.oAuth.LoginHandler;
import com.tigerfixonline.crud.oAuth.UserManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/*
 * MainView used to provide links to all views in the APP
 */
public class MainView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1304364443166215430L;
	private Button customerBtn = new Button("Business Explorer");
	private Button loggerBtn = new Button("Logger");
	private Button dbBackupBtn = new Button("App Backup / Restore");
	// private Button editEntity = new Button("Edit Business");

	/**
	 * Navigator
	 */
	private Navigator navigator;

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

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
		loginHandler.setViewName("");
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

	public MainView(Navigator navObj) {
		navigator = navObj;
	}

	private void initializeComponents() {
		customerBtn.setStyleName(ValoTheme.BUTTON_HUGE);
		customerBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);

		customerBtn.addClickListener(listener -> {
			navigator.navigateTo("customer");
		});
		loggerBtn.setStyleName(ValoTheme.BUTTON_HUGE);
		loggerBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);

		loggerBtn.addClickListener(listener -> {
			if (UserManager.isAdmin())
				navigator.navigateTo("logger");
			else
				Notification.show("Privilaged operation", "You are NOT an Admin...", Notification.Type.ERROR_MESSAGE);
		});

		dbBackupBtn.setStyleName(ValoTheme.BUTTON_HUGE);
		dbBackupBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);

		dbBackupBtn.addClickListener(listener -> {
			if (UserManager.isAdmin())
				navigator.navigateTo("appBackupRestore");
			else
				Notification.show("Privilaged operation", "You are NOT an Admin...", Notification.Type.ERROR_MESSAGE);
		});

//		editEntity.setStyleName(ValoTheme.BUTTON_HUGE);
//		editEntity.setStyleName(ValoTheme.BUTTON_PRIMARY);
//		
//		editEntity.addClickListener(listener -> {
//			navigator.navigateTo("form");
//		});

		addComponents(customerBtn, loggerBtn, dbBackupBtn /* ,editEntity*/);
		setComponentAlignment(customerBtn, Alignment.MIDDLE_CENTER);
		setComponentAlignment(loggerBtn, Alignment.MIDDLE_CENTER);
		setComponentAlignment(dbBackupBtn, Alignment.MIDDLE_CENTER);
//		setComponentAlignment(editEntity, Alignment.MIDDLE_CENTER);
	}

}
