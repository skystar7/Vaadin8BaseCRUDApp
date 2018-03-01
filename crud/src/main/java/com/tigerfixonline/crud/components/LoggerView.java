package com.tigerfixonline.crud.components;

import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.User;
import com.tigerfixonline.crud.oAuth.LoginHandler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class LoggerView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1208378773937147002L;
	private Label label;
	private static TextArea textArea;
	private HorizontalLayout horizontalLayout = new HorizontalLayout();

	/*
	 * Constants
	 */

	private static final float field_length_em = 20.0f;

	/**
	 * Navigator
	 */
	// private Navigator navigator;

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
		loginHandler = new LoginHandler(this);
		loginHandler.setViewName("logger");
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

	private void initializeComponents() {
		label = new Label("App logger");
		horizontalLayout.addComponents(label);
		textArea = new TextArea();
		textArea.setWidth(field_length_em * 3, Unit.EM);
		textArea.setHeight(field_length_em * 2, Unit.EM);
		textArea.setValue(LoggerUtil.logsReader());
		setVisible(true);
		addComponents(horizontalLayout, textArea);
	}

	public LoggerView() {

	}

}
