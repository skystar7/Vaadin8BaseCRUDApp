package com.tigerfixonline.crud.components.model;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

import com.tigerfixonline.crud.components.EntityForm;
import com.tigerfixonline.crud.components.EntityGrid;
import com.tigerfixonline.crud.components.EntityView;
import com.tigerfixonline.crud.components.GooglePlacesComponent;
import com.tigerfixonline.crud.csv.CSVWriter;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.Customer;
import com.tigerfixonline.crud.oAuth.UserManager;
import com.tigerfixonline.crud.persistence.CustomerService;
import com.tigerfixonline.crud.upload.RemoteFileUtil;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;

public class CustomerView extends EntityView {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 446524139602978756L;

	/*
	 * Domain model
	 */
	private Grid<Customer> customerGrid;
	private CustomerService customerService;
	private GooglePlacesComponent googlePlacesComponent;
	private EntityGrid<Customer> customEntityGrid;

	/*
	 * Additional controls
	 */
	private Button addGooglePlace;

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	public CustomerView(Navigator navObject) {
		super(navObject);

	}

	@Override
	protected String getViewName() {
		return "customer";
	}

	@Override
	protected void initializeEntityComponents() {
		/*
		 * Custom Grid
		 */
		customerGrid = new Grid<>(Customer.class);
		customerGrid.setSizeFull();

		/*
		 * Custom Entity
		 */
		entity = new Customer();
		customEntityGrid = new EntityGrid<Customer>(customerGrid, toolBar, edit, entityForm, exportToCSV, batchDelete);

		/*
		 * Entity Service
		 */
		customerService = CustomerService.getInstance();

		/*
		 * Entity Components
		 */

		googlePlacesComponent = new GooglePlacesComponent(this, customerService, RemoteFileUtil.getStorageInstance());
		googlePlacesComponent.setVisible(false);
		addGooglePlace = new Button("Add Google Place");
		addGooglePlace.addClickListener(clicked -> {
			ClearGridSelection();
			googlePlacesComponent.setVisible(true);
		});

		mainContentVertLayout.addComponent(customerGrid);
		mainContentVertLayout.setExpandRatio(customerGrid, 1.0f);
	}

	/**
	 * set EntityGrid Model
	 */
	@Override
	protected void initializeGrid() {
		entityGrid = customEntityGrid;
	}

	/**
	 * On creation bind the new entity to the form.
	 */
	@Override
	public void addEntity() {
		super.addEntity();
		if (entity == null)
			entity = new Customer();
		entityForm.bindEntity(entity, true);
	}

	@Override
	public void showEntities() {
		Collection<Customer> customers = customerService.filterEntities(filter.getValue());
		customerGrid.setItems(customers);
		customerGrid.setColumns("id", "name", "email", "phone", "website");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void exportToCSV(Set<?> set) {
		if (UserManager.isAdmin()) {
			Set<Customer> customers = (Set<Customer>) set;
			StringBuilder stringBuilder = new StringBuilder();
			for (Customer c : customers)
				stringBuilder.append(c.getId() + " ");
			CSVWriter.collectionWriter(customers);
			logger.info("Your selection:" + stringBuilder.toString() + "has been exported to CSV");
			Notification.show("Exporting to CSV...", "Your selection:" + stringBuilder.toString() + "has been exported",
					Notification.Type.TRAY_NOTIFICATION);
			Link csvLink = new Link("Exported CSV", new FileResource(new File("customers.csv")));
			mainContentVertLayout.addComponent(csvLink);
		} else {
			Notification.show("Privilaged operation", "You are NOT an Admin...", Notification.Type.ERROR_MESSAGE);
			logger.info(UserManager.getUserScreenName() + " trying to exportToCSV(...)");
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void batchDelete(Set<?> set) {
		if (UserManager.isAdmin()) {
			Set<Customer> customers = (Set<Customer>) set;
			StringBuilder stringBuilder = new StringBuilder();
			for (Customer c : customers)
				stringBuilder.append(c.getId() + " ");
			boolean isDeleted = customerService.batchDelete(customers);
			if (isDeleted) {
				Notification.show("Deleteting...", "Your records: " + stringBuilder.toString() + "has been deleted",
						Notification.Type.TRAY_NOTIFICATION);
				logger.info("Your records: " + stringBuilder.toString() + " has been deleted...");
				toolBar.removeComponent(batchDelete);
				showEntities();
			} else {
				Notification.show("Deleteting...", "Your records has NOT been deleted",
						Notification.Type.ERROR_MESSAGE);
			}
		} else {
			Notification.show("Privilaged operation", "You are NOT an Admin...", Notification.Type.ERROR_MESSAGE);
			logger.info(UserManager.getUserScreenName() + " trying to exportToCSV(...)");
		}

	}

	/**
	 * Link to custom entity form
	 */
	@Override
	protected EntityForm entityFormInit() {
		return new CustomerForm(this);
	}

	@Override
	public void newEntity() {
		entity = new Customer();
	}

	@Override
	public void addEntityComponents() {
		toolBar.addComponent(addGooglePlace);
		mainContentVertLayout.addComponent(googlePlacesComponent);
	}

}
