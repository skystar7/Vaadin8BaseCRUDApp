package com.tigerfixonline.crud.components;

import java.util.Set;

import com.tigerfixonline.crud.model.Entity;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModel;

/**
 * Generic Grid Wrapper
 * All operations on GRID should be added here
 */
public class EntityGrid<T> {

	private Grid<T> grid;
	private HorizontalLayout toolBar;
	private CheckBox edit;
	private EntityForm entityForm;
	private Button exportToCSV;
	private Button batchDelete;
	private Set<T> selectedSet;

	public Set<T> getSelectedSet() {
		return selectedSet;
	}

	public EntityGrid(Grid<T> entityGrid, HorizontalLayout toolBar, CheckBox edit, EntityForm entityForm,
			Button exportToCSV, Button batchDelete) {
		super();
		this.grid = entityGrid;
		this.toolBar = toolBar;
		this.edit = edit;
		this.entityForm = entityForm;
		this.exportToCSV = exportToCSV;
		this.batchDelete = batchDelete;
	}

	public void clearSelection() {
		GridSelectionModel<T> selectionModel = grid.getSelectionModel();
		if (selectionModel instanceof SingleSelectionModel) {
			grid.asSingleSelect().clear();
		} else {
			grid.asMultiSelect().clear();
		}
	}

	public void switchSelectionMode() {
		GridSelectionModel<T> selectionModel = grid.getSelectionModel();

		if (selectionModel instanceof SingleSelectionModel) {
			grid.setSelectionMode(SelectionMode.MULTI);
			multiModeListener();
			toolBar.removeComponent(edit);
		} else {
			grid.setSelectionMode(SelectionMode.SINGLE);
			singleModeListener();
			toolBar.addComponent(edit);
		}

	}

	public void singleModeListener() {
		grid.asSingleSelect().addValueChangeListener(single -> {
			if (single.getValue() == null) {
				entityForm.setVisible(false);
			} else {
				UI.getCurrent().scrollIntoView(entityForm);
				Entity value = (Entity) single.getValue();
				entityForm.bindEntity(value.clone(), edit.getValue());
				// customerGrid.setEnabled(false);
			}
		});
	}

	public void multiModeListener() {
		grid.asMultiSelect().addValueChangeListener(multi -> {
			selectedSet = multi.getValue();

			if (multi.getValue().size() == 0) {
				toolBar.removeComponent(exportToCSV);
				toolBar.removeComponent(batchDelete);

			} else {
				/* no effects if component is already added */
				toolBar.addComponent(exportToCSV);
				toolBar.addComponent(batchDelete);
			}

		});
	}

	public void enableGrid(boolean enable) {
		grid.setEnabled(enable);
	}

}
