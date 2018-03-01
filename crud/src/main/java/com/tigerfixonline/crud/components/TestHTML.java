package com.tigerfixonline.crud.components;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot
public class TestHTML extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7949677448111238630L;
	private TextField emailField;
	private PasswordField passwordField;
	private Button signInButton;

	public TestHTML() {
		Design.read(this);
		signInButton.addClickListener(clicked -> {
			Notification.show(emailField.getValue() + " " + passwordField.getValue() + " entered...");
		});
	}

}
