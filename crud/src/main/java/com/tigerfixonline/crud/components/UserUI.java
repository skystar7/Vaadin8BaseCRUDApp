package com.tigerfixonline.crud.components;

import java.util.logging.Logger;

import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.User;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

public class UserUI extends HorizontalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8734976013467719891L;

	/**
	 * Controls
	 */
	private Label displayName = new Label();
	private Image userImage = new Image();
	private Button logout = new Button();

	/*
	 * Model
	 */
	private User user;

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	public UserUI(User userObj) {
		super();
		user = userObj;
		displayName.setCaption(
				"You are logged in as: " + user.getUserType().getName() + " using your " + user.getService());
		logout.setCaption("Log out...");
		logout.setStyleName(ValoTheme.BUTTON_TINY);
		userImage.setSource(new ExternalResource(user.getUserType().getImagePath()));
		logout.addClickListener(listener -> {
			VaadinSession.getCurrent().close();
			UI.getCurrent().getPage().setLocation("/");
			logger.info(user.getUserType().getName() + " logged out...");
		});
		addComponents(userImage, displayName, logout);
	}

}
