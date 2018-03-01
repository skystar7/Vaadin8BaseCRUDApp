package com.tigerfixonline.crud.oAuth;

import java.util.HashSet;
import java.util.Set;

import com.tigerfixonline.crud.model.User;
import com.vaadin.server.VaadinSession;

public class UserManager {

	/*
	 * Users
	 */
	private static Set<String> admin = new HashSet<>();
	private static Set<String> whiteListIDs = new HashSet<>();
	private static User currentLoggedUser;

	static {
		/* Users IDs */
		admin.add("***");
		whiteListIDs.add("***");
	}

	public static boolean isAdmin() {
		VaadinSession vaadinSession = VaadinSession.getCurrent();
		currentLoggedUser = vaadinSession.getAttribute(User.class);
		if (admin.contains(currentLoggedUser.getUserType().getID()))
			return true;
		else
			return false;
	}

	public static boolean isWhiteList() {
		VaadinSession vaadinSession = VaadinSession.getCurrent();
		currentLoggedUser = vaadinSession.getAttribute(User.class);

		if (whiteListIDs.contains(currentLoggedUser.getUserType().getID()))
			return true;
		else
			return false;
	}

	public static String getUserScreenName() {
		return currentLoggedUser.getUserType().getName();
	}

}
