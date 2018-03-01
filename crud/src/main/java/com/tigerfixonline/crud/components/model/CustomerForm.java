package com.tigerfixonline.crud.components.model;

import java.util.BitSet;
import java.util.List;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.tigerfixonline.crud.components.EntityForm;
import com.tigerfixonline.crud.components.EntityView;
import com.tigerfixonline.crud.components.ImageViewer;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.Customer;
import com.tigerfixonline.crud.model.Entity;
import com.tigerfixonline.crud.model.Georgia;
import com.tigerfixonline.crud.model.ImageInfo;
import com.tigerfixonline.crud.persistence.CustomerService;
import com.tigerfixonline.crud.upload.BaseReceiver;
import com.tigerfixonline.crud.upload.FileUtil;
import com.tigerfixonline.crud.upload.RemoteFileUtil;
import com.tigerfixonline.crud.upload.RemoteReceiver;
import com.tigerfixonline.crud.upload.UploadBaseFolder;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;

public class CustomerForm extends EntityForm implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9170588150432010750L;

	/*
	 * Model
	 */
	private Customer currentCustomer;
	private CustomerService customerService;

	/*
	 * Binder
	 */
	private Binder<Customer> customerBinder;

	/*
	 * Controls
	 */
	private TextField name;
	private TextField email;
	private TextField phone;
	private TextField website;
	private TextField addressLine1;
	private TextField addressLine2;
	private NativeSelect<String> city;
	private TextField state;
	private TextField zip;
	private TextField contactPerson;
	private TextArea description;

	/*
	 * Image Upload state
	 */
	private BaseReceiver receiver;
	private ProgressBar progressBar;
	private ImageViewer imageViewer;
	private Upload imageUploader;
	private ImageInfo currentImageInfo;
	private FileUtil fileUtil;

	/*
	 * Constant
	 */
	/* over 1 gig */
	private final long UPLOAD_LIMIT = 1_000_000_000l;

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	public CustomerForm(EntityView entityV) {
		super(entityV);
	}

	private String formatePhone(String valueEntered) {
		String formatedPhone = null;

		switch (valueEntered.length()) {
		case 1: {
			formatedPhone = "(" + valueEntered;
			break;
		}

		case 4: {
			formatedPhone = valueEntered + ") ";
			break;
		}

		case 9: {
			formatedPhone = valueEntered + "-";
			break;

		}
		default: {
			return valueEntered;
		}

		}
		return formatedPhone;
	}

	@Override
	protected void initEntityForm() {
		name = new TextField("Business name");
		email = new TextField("Email");
		phone = new TextField("Phone");
		phone.setPlaceholder("Sample Format: (770) 123-0000");
		phone.addValueChangeListener(e -> {
			String valueEntered = e.getValue();
			String formatedPhone = formatePhone(valueEntered);
			phone.setValue(formatedPhone);
		});
		phone.setMaxLength(14);
		phone.setValueChangeMode(ValueChangeMode.EAGER);
		website = new TextField("Website");
		addressLine1 = new TextField("Address Line 1");
		addressLine2 = new TextField("Address Line 2");
		addressLine2.setPlaceholder("Apartment, suite, unit, building, floor, etc");
		city = new NativeSelect<>("City");
		state = new TextField("State");
		state.setEnabled(false);
		zip = new TextField("Zip");
		contactPerson = new TextField("Contact Person");
		description = new TextArea("Description");
		imageViewer = new ImageViewer(entityView);
		if (entityView == null)
			imageViewer.setEditMode(true);
		fileUtil = RemoteFileUtil.getStorageInstance();
		imageViewer.setFileUtil(fileUtil);
		progressBar = new ProgressBar();
		imageUploader = new Upload();
		/* choose between local or remote receiver */
		receiver = new RemoteReceiver();
		imageUploader.setReceiver(receiver);
		customerBinder = new Binder<>(Customer.class);
		city.setItems(Georgia.getCities());
		customerService = CustomerService.getInstance();
		description.setWordWrap(true);
		imageUploader.setButtonCaption("Upload image");

		/*
		 * Uploader logic
		 */
		imageUploader.addStartedListener(started -> {
			// System.out.println("Entered StartedListener");
			toolbar.addComponent(progressBar);
			/* disable uploader */
			imageUploader.setButtonCaption(null);

			/* add record number */
			receiver.setAssociatedEntity(currentCustomer);
			receiver.setRecordNumber(String.valueOf("record" + receiver.getAssociatedEntity().getId()));
		});
		imageUploader.addProgressListener((readBytes, contentLength) -> {
			// System.out.println("Entered ProgressListener");

			/* abort upload if file too large */;
			if (readBytes > UPLOAD_LIMIT) {
				Notification.show("File too big", Notification.Type.ERROR_MESSAGE);
				logger.info("File too big");
				imageUploader.interruptUpload();
			}

			if (contentLength == -1) {
				progressBar.setIndeterminate(true);
			} else {
				currentImageInfo = new ImageInfo();
				currentImageInfo.setLength(contentLength);
				// System.out.println("readBytes: " + readBytes);
				// System.out.println("contentLength: " + contentLength);
				float currentProgress = readBytes / contentLength;
				// System.out.println("currentProgress: " + currentProgress);
				progressBar.setValue(currentProgress);
			}

		});

		imageUploader.addSucceededListener(success -> {
			// System.out.println("Entered SucceededListener");
			toolbar.removeComponent(progressBar);

			UI ui = UI.getCurrent();
			/* start a thread for upload */
			Thread imageUploaderThread = new Thread(() -> {
				UI.setCurrent(ui);
				receiver.upload();
				String imageName = receiver.getFileName();
				String mimeType = receiver.getMimeType();
				currentImageInfo.setName(imageName);
				currentImageInfo.setMimeType(mimeType);
				receiver.getAssociatedEntity().addImageInfo(currentImageInfo);
				logger.info(imageName + " uploaded to memory...");

				UI.getCurrent().access(() -> {
					/* persist the changes */
					boolean updateEntity = customerService.updateEntity(receiver.getAssociatedEntity());
					if (updateEntity) {
						Notification.show("File uploaded successfully...",
								success.getFilename() + " to record: " + receiver.getAssociatedEntity().getId(),
								Type.TRAY_NOTIFICATION);

						/* view uploaded image if match intended customer */
						if (receiver.getAssociatedEntity().equals(currentCustomer)) {
							imageViewer.setEntity(receiver.getAssociatedEntity(), customerService, false);
						}

						/* enable uploader */
						imageUploader.setButtonCaption("Upload image");
					}

				});
			});
			imageUploaderThread.setName("imageUploaderThread");
			imageUploaderThread.start();

		});

		imageUploader.addFailedListener(failed -> {
			toolbar.removeComponent(progressBar);
			logger.warning(failed.getFilename() + " NOT uploaded successfully...");
			Notification.show("File NOT uploaded successfully...",
					failed.getFilename() + " to record: " + receiver.getAssociatedEntity().getId(), Type.ERROR_MESSAGE);
		});

		requiredRules();
		customerBinder.bindInstanceFields(this);
		groupVertical.addComponents(imageViewer);

		addComponents(name, email, phone, website, addressLine1, addressLine2, city, state, zip, contactPerson,
				description);
	}

	@Override
	protected void add_removeComponents(boolean enable) {
		super.add_removeComponents(enable);
		if (enable) {
			toolbar.addComponent(imageUploader);
		} else {
			toolbar.removeComponent(imageUploader);
		}
	}

	private void requiredRules() {
		customerBinder.forField(name).asRequired("this field is required").bind(Customer::getName, Customer::setName);
	}

	private void validationRules(String fieldName) {
		/* Validation */
		switch (fieldName) {
		case "phone":
			customerBinder.forField(phone)
					.withValidator(new RegexpValidator(
							"Invalid phone!\n Number should be formatted as for example (770) 123-0000",
							"^\\(\\d{3}\\) \\d{3}-\\d{4}$"))
					.bind(Customer::getPhone, Customer::setPhone);
			break;
		case "email":
			customerBinder.forField(email).withValidator(new EmailValidator("Invalid email")).bind(Customer::getEmail,
					Customer::setEmail);
			break;
		}

	}

	private boolean validateFields() {
		BinderValidationStatus<Customer> validate = customerBinder.validate();
		return validate.isOk();
	}

	private void prePopulateData(Customer customer) {
		if (!customerService.isPersisted(customer))
			customer.setState("Georgia");
	}

	/**
	 * Show form on binding
	 */
	@Override
	public void bindEntity(Entity selectedCustomer, boolean enable) {
		this.currentCustomer = (Customer) selectedCustomer;

		if (customerService.isPersisted(currentCustomer)) {
			delete.setEnabled(true);
			// imageViewer.setVisible(true);
			imageUploader.setEnabled(true);
		} else {
			prePopulateData(currentCustomer);
			delete.setEnabled(false);
			// imageViewer.setVisible(false);
			imageUploader.setEnabled(false);
		}

		customerBinder.setBean(currentCustomer);

		/*
		 * Bind image components
		 */

		imageViewer.setEntity(currentCustomer, customerService, true);

		setVisible(true);
		name.selectAll();
		add_removeComponents(enable);

		/* enable disable components */
		// enableComponents(enable);
	}

	protected void save() {
		boolean passValidation = false;

		requiredRules();

		/*
		 * List all validated fields
		 */
		if (phone.getValue() != null && phone.getValue() != "") {
			validationRules("phone");
		} else if (email.getValue() != null && email.getValue() != "") {
			validationRules("email");
		}

		passValidation = validateFields() && customerBinder.isValid();

		if (passValidation)
			ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Do you want to save the changes?", "Yes",
					"No, leave it unsaved", dialog -> {
						if (dialog.isConfirmed()) {
							boolean isPersisted = customerService.updateEntity(currentCustomer);
							if (isPersisted) {
								Notification.show("Saved...", "Your changes has been saved", Type.TRAY_NOTIFICATION);
								logger.info("Your changes has been saved for: " + currentCustomer);
								setVisible(false);

								/*
								 * standalone View
								 */
								if (entityView != null) {
									entityView.showEntities();
									entityView.clearFilter();
									entityView.newEntity();
								}

								// myUI.enableCustomerGrid(true);
							} else {
								logger.warning("Your changes has been NOT saved for: " + currentCustomer);
								Notification.show("Not saved...", "Your changes has NOT been saved",
										Type.ERROR_MESSAGE);
							}
						}
					});
	}

	protected void delete() {
		ConfirmDialog.show(UI.getCurrent(), "Please Confirm:",
				"Do you want to delete this record? All images related to this record will be deleted as well", "Yes",
				"No, don't delete it", dialog -> {
					if (dialog.isConfirmed()) {
						UI ui = UI.getCurrent();
						Thread entiyDeleteThread = new Thread(() -> {
							UI.setCurrent(ui);
							/*
							 * Deleting images
							 */
							List<ImageInfo> images = currentCustomer.getImages();
							int size = images.size();
							BitSet bitSet = new BitSet(images.size());
							for (int i = 0; i < images.size(); i++) {
								boolean deleteFileAt = fileUtil.deleteFileAt(images.get(i).getName(),
										UploadBaseFolder.vaadinuploadedimages.name());
								boolean deleteFileAtThumbnail = fileUtil.deleteFileAt(images.get(i).getName(),
										UploadBaseFolder.vaadinthumbnails.name());
								if (deleteFileAt && deleteFileAtThumbnail) {
									logger.info(images.get(i).getName() + " been deleted");
									bitSet.set(i);
								}

							}

							boolean isImagesDeleted = bitSet.cardinality() == size;
							if (isImagesDeleted) {
								logger.info("All images been deleted");
							}

							boolean isEntityDeleted = customerService.deleteEntity(currentCustomer);
							if (isEntityDeleted)
								logger.info(currentCustomer + " been deleted");
							/* Update UI */
							UI.getCurrent().access(() -> {
								if (isEntityDeleted && isImagesDeleted) {
									Notification.show("Deleteting...",
											"Your record " + currentCustomer.getName() + " has been deleted",
											Type.TRAY_NOTIFICATION);
									setVisible(false);

									/*
									 * standalone View
									 */
									if (entityView != null) {
										entityView.showEntities();
									}

									// myUI.enableCustomerGrid(true);
								} else {
									Notification.show("Deleteting...",
											"Your record  " + currentCustomer.getName() + " has NOT been deleted",
											Type.ERROR_MESSAGE);
								}
							});

						});
						entiyDeleteThread.setName("entiyDeleteThread");
						entiyDeleteThread.start();

					}

				});

	}

}
