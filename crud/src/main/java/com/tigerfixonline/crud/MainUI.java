package com.tigerfixonline.crud;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.tigerfixonline.crud.components.AppBackupRestoreView;
import com.tigerfixonline.crud.components.LoggerView;
import com.tigerfixonline.crud.components.MainView;
import com.tigerfixonline.crud.components.model.CustomerView;
import com.tigerfixonline.crud.model.User;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * Main UI class used to add views to the APP
 */
@Title("CRUD Webapp")
@Theme("mytheme")
@Push
public class MainUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7644860216673046120L;

	/**
	 * Navigator
	 */
	private Navigator navigator;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		
		
		// setContent(new TestHTML());
		navigator = new Navigator(this, this);
		navigator.addView("", new MainView(navigator));
		navigator.addView("customer", new CustomerView(navigator));
		navigator.addView("logger", new LoggerView());
		navigator.addView("appBackupRestore", new AppBackupRestoreView());

		/*
		 * Update specific entity with CustomerForm View
		 */
//		CustomerForm customerForm = new CustomerForm(null);
//		CustomerService customerService = CustomerService.getInstance();
//		customerForm.bindEntity(customerService.getEntityByID(1l), true);
//		customerForm.setSpacing(true);
//		customerForm.setMargin(true);
//		customerForm.setWidth(100, Unit.PERCENTAGE);
//		navigator.addView("form", customerForm);
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MainUI.class, productionMode = false)

	public static class MainUIServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4810602837332658777L;

		@Override
		protected void servletInitialized() throws ServletException {
			super.servletInitialized();
			getService().addSessionInitListener(this);
			getService().addSessionDestroyListener(this);
		}

		@Override
		public void sessionInit(SessionInitEvent event) throws ServiceException {
			VaadinSession.getCurrent().setAttribute(User.class, new User());
		}

		@Override
		public void sessionDestroy(SessionDestroyEvent event) {

		}

	}
}
