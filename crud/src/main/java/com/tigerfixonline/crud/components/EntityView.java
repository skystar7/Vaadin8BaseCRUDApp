package com.tigerfixonline.crud.components;

import java.util.Set;
import java.util.logging.Logger;

import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.Entity;
import com.tigerfixonline.crud.model.User;
import com.tigerfixonline.crud.oAuth.LoginHandler;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Abstract EntityView hold main functionality of CRUD APP
 */
public abstract class EntityView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5311228427798224173L;

	/*
	 * Model
	 */
	protected Entity entity;

	/*
	 * Vaadin Controls
	 */
	protected EntityGrid<?> entityGrid;
	protected TextField filter;
	protected CheckBox edit;
	protected EntityForm entityForm;
	protected Button exportToCSV;
	protected Button batchDelete;
	private Button clearFilter;
	private Button addEntity;
	private Button clearGrid;
	private Button switchGridMode;
	private Button refreshData;

	/*
	 * Layout
	 */
	private CssLayout filterLayout;
	protected VerticalLayout mainContentVertLayout;
	protected HorizontalLayout toolBar;

	/*
	 * Login handler
	 */
	private VaadinSession vaadinSession;
	private LoginHandler loginHandler;
	private User currentUser;
	private boolean initialized;

	/*
	 * UI State
	 */
	private boolean editMode;

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	/**
	 * Navigator
	 */
	// private Navigator navigator;

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);

		/*
		 * get current session, and retrieve User
		 */
		vaadinSession = VaadinSession.getCurrent();
		currentUser = vaadinSession.getAttribute(User.class);

		/*
		 * loginHandler
		 */
		logger.info("forward login request");
		loginHandler = new LoginHandler(this);
		loginHandler.setViewName(getViewName());
		/* entity overridden */

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

	public EntityView(Navigator navObject) {
		// this.navigator = navObject;
	}

	public void setEntityGrid(EntityGrid<?> entityGrid) {
		this.entityGrid = entityGrid;
	}

	/*
	 * abstract methods
	 */
	protected abstract void initializeEntityComponents();

	protected abstract void initializeGrid();

	protected abstract EntityForm entityFormInit();

	public abstract void newEntity();

	public abstract void addEntityComponents();

	protected abstract void exportToCSV(Set<?> set);

	protected abstract void batchDelete(Set<?> set);

	public abstract void showEntities();

	private void initializeComponents() {
		mainContentVertLayout = new VerticalLayout();
		entityForm = entityFormInit();
		toolBar = new HorizontalLayout();
		edit = new CheckBox("Switch edit mode");
		exportToCSV = new Button("Export to CSV");
		batchDelete = new Button("Batch delete");
		clearFilter = new Button(VaadinIcons.CLOSE);
		filterLayout = new CssLayout();
		filter = new TextField();
		addEntity = new Button();
		clearGrid = new Button("Clear grid selection");
		switchGridMode = new Button("Switch grid mode");
		refreshData = new Button("Refresh data");

		/*
		 * initialize entity model & grid model
		 */
		initializeEntityComponents();
		initializeGrid();

		entityGrid.singleModeListener();
		entityForm.setVisible(false);
		mainContentVertLayout.addComponent(entityForm);
		mainContentVertLayout.setComponentAlignment(entityForm, Alignment.MIDDLE_CENTER);
		filter.setPlaceholder("filter...");
		filter.addValueChangeListener(e -> showEntities());
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		clearFilter.addClickListener(e -> clearFilter());
		filterLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		filterLayout.addComponents(filter, clearFilter);
		addEntity.setCaption("Add entity");
		addEntity.addClickListener(e -> addEntity());
		clearGrid.addClickListener(e -> ClearGridSelection());
		switchGridMode.addClickListener(e -> entityGrid.switchSelectionMode());
		refreshData.addClickListener(e -> showEntities());
		edit.addValueChangeListener(e -> {
			entityGrid.clearSelection();
			if (e.getValue()) {
				editMode = true;
				Notification.show("Edit mode", Notification.Type.WARNING_MESSAGE);
			} else {
				editMode = false;
				Notification.show("View mode", Notification.Type.HUMANIZED_MESSAGE);
			}
		});
		toolBar.addComponents(filterLayout, addEntity, clearGrid, switchGridMode, refreshData, edit);
		exportToCSV.addClickListener(listener -> {
			exportToCSV(entityGrid.getSelectedSet());
			toolBar.removeComponent(exportToCSV);
			showEntities();
		});

		batchDelete.addClickListener(batchDelListener -> {
			batchDelete(entityGrid.getSelectedSet());
		});

		showEntities();
		addEntityComponents();
		// CustomLayout customLayout = new CustomLayout("test");
		// Button button = new Button("Click Me");
		// customLayout.addComponent(button, "btnHTML");
		addComponents(toolBar, mainContentVertLayout);
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void clearFilter() {
		filter.setValue("");
	}

	/*
	 * Subclasses must call super implementation
	 */
	public void addEntity() {
		entityGrid.clearSelection();
	}

	public void ClearGridSelection() {
		entityGrid.clearSelection();
	}

	/**
	 * Subclasses should provide view name
	 * 
	 * @return null
	 */
	protected String getViewName() {
		return null;
	}

}
