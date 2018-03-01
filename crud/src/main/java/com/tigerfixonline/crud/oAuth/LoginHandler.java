package com.tigerfixonline.crud.oAuth;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tigerfixonline.crud.components.UserUI;
import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.User;
import com.tigerfixonline.crud.model.User.GoogleUser;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

public class LoginHandler implements RequestHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -184618406381299422L;
	/**
	 * OAuth20Service Sample
	 */
	private OAuth20Service oAuth20Service;
	private OAuth2AccessToken accessToken;
	private VaadinSession vaadinSession;
	private User currentUser;
	private UserUI userUI;
	private AbstractOrderedLayout component;
	/*
	 * ViewName
	 */
	private String viewName;

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	public LoginHandler(AbstractOrderedLayout comp) {
		this.component = comp;
		/* vaadinSession */
		vaadinSession = VaadinSession.getCurrent();
		currentUser = vaadinSession.getAttribute(User.class);
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void logOut() {
		try {
			oAuth20Service = OAuthUtil.createGoogleService();
			oAuth20Service.revokeToken(vaadinSession.getAttribute(OAuth2AccessToken.class).getAccessToken());
			logger.info("token revoked... for:" + UserManager.getUserScreenName());
		} catch (InterruptedException | ExecutionException | IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't revoke token for " + UserManager.getUserScreenName(), crudException);
		}
	}

	public void unauthorizedUser() {
		Label notAuthorized = new Label("Opps you are not authorized to access the app! Try another account");
		logger.warning(currentUser.getUserType().getID() + " " + currentUser.getUserType().getName()
				+ " not authorized to access the app");
		component.addComponent(notAuthorized);
		component.setComponentAlignment(notAuthorized, Alignment.MIDDLE_CENTER);
		logOut();
		currentUser = null;
		generateLoginLink();
	}

	public void addUserUI() {
		userUI = new UserUI(currentUser);
		if (!hasUserUI()) {
			component.addComponent(userUI);
			component.setComponentAlignment(userUI, Alignment.TOP_RIGHT);
		}
	}

	private boolean hasUserUI() {
		Iterator<Component> iterator = component.iterator();
		while (iterator.hasNext()) {
			Component component = iterator.next();
			if (component instanceof UserUI)
				return true;
		}
		return false;
	}

	public void generateLoginLink() {
		oAuth20Service = OAuthUtil.createGoogleService();
		vaadinSession.addRequestHandler(this);
		String authorizationUrl = oAuth20Service.getAuthorizationUrl();
		Link signinLink = new Link("Sign with google", new ExternalResource(authorizationUrl));
		signinLink.setIcon(new FileResource(new File("resources/images/google-plus.png")));
		component.addComponent(signinLink);
		component.setComponentAlignment(signinLink, Alignment.MIDDLE_CENTER);
	}

	@Override
	public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response)
			throws IOException {

		String code = request.getParameter("code");

		if (code != null) {
			try {
				accessToken = oAuth20Service.getAccessToken(code);
				// System.out.println(accessToken.getRawResponse());
				final OAuthRequest oAuthrequest = new OAuthRequest(Verb.GET,
						"https://www.googleapis.com/plus/v1/people/me");
				oAuth20Service.signRequest(accessToken, oAuthrequest);
				final Response oAuthresponse = oAuth20Service.execute(oAuthrequest);
				vaadinSession.setAttribute(OAuth2AccessToken.class, accessToken);
				User user = vaadinSession.getAttribute(User.class);
				Gson gson = new GsonBuilder().create();
				// System.out.println(oAuthresponse.getCode());
				// System.out.println(oAuthresponse.getBody());
				GoogleUser googleUser = gson.fromJson(oAuthresponse.getBody(), User.GoogleUser.class);
				user.setUserType(googleUser);
				// System.out.println(user);
				oAuth20Service.close();
				// System.out.println("userID: " + user.getUserType().getID());
				boolean whiteList = UserManager.isWhiteList();
				// System.out.println(whiteList);
				if (whiteList) {
					logger.info(user.getUserType().getName() + " with id " + user.getUserType().getID()
							+ " is logged in...");
					user.setLogged(true);
					user.setAuthorized(true);
				} else {
					user.setLogged(true);
					user.setAuthorized(false);
				}

			} catch (InterruptedException e) {
				CRUDException crudException = new CRUDException("oAuth exception: " + e.getMessage(), e);
				crudException.printStackTrace();
			} catch (ExecutionException e) {
				CRUDException crudException = new CRUDException("oAuth exception: " + e.getMessage(), e);
				crudException.printStackTrace();
			}

			vaadinSession.removeRequestHandler(this);

			if (getViewName().equals("")) {
				((VaadinServletResponse) response).getHttpServletResponse().sendRedirect("/");
			} else {
				vaadinSession.removeRequestHandler(this);
				((VaadinServletResponse) response).getHttpServletResponse().sendRedirect("/#!" + getViewName());
			}

			return true;
		}

		return false;
	}

}
