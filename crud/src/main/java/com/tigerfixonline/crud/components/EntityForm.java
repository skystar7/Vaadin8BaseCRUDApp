package com.tigerfixonline.crud.components;

import java.util.Iterator;

import com.tigerfixonline.crud.model.Entity;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Provide basic form functionality, sub-classes should provide additional
 * functionality and override the basic functionality.
 * 
 * @author Ahmad
 *
 */
public abstract class EntityForm extends FormLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3394809809194036815L;

	private Button save = new Button("Save");
	protected Button delete = new Button("Delete");
	private Button hide = new Button("Hide");

	/*
	 * Constants
	 */

	private final float field_length_em = 20.0f;

	/*
	 * UI
	 */
	protected EntityView entityView;

	/*
	 * Layout
	 */
	protected VerticalLayout groupVertical = new VerticalLayout();
	protected HorizontalLayout toolbar = new HorizontalLayout();

	public EntityForm(EntityView entityV) {

		/*
		 * Form functionality
		 */
		this.entityView = entityV;
		/* needed for centering the form */
		setSizeUndefined();
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		/* save on Enter */
		save.setClickShortcut(KeyCode.ENTER);
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		hide.addClickListener(e -> {
			setVisible(false);
			// myUI.enableCustomerGrid(true);
		});

		/*
		 * Entity
		 */
		initEntityForm();

		toolbar.addComponents(hide);
		groupVertical.addComponents(toolbar);
		addComponents(groupVertical);
		setComponentsSize();
	}

	private void setComponentsSize() {
		Iterator<Component> iterator = iterator();
		while (iterator.hasNext()) {
			Component next = iterator.next();
			if (next instanceof TextArea)
				next.setWidth(field_length_em * 2, Unit.EM);
			else
				next.setWidth(field_length_em, Unit.EM);
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void enableComponents(boolean enable) {
		Iterator<Component> iterator = iterator();
		while (iterator.hasNext()) {
			Component next = iterator.next();
			next.setEnabled(enable);
		}
	}

	protected void add_removeComponents(boolean enable) {
		if (enable) {
			toolbar.addComponent(save);
			toolbar.addComponent(delete);
		} else {
			toolbar.removeComponent(save);
			toolbar.removeComponent(delete);
		}
	}

	protected abstract void initEntityForm();

	public abstract void bindEntity(Entity selectedCustomer, boolean enable);

	protected abstract void save();

	protected abstract void delete();
}
