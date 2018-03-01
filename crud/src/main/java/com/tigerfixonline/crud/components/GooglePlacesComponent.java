package com.tigerfixonline.crud.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.Customer;
import com.tigerfixonline.crud.model.ImageInfo;
import com.tigerfixonline.crud.persistence.BaseService;
import com.tigerfixonline.crud.upload.FileUtil;
import com.tigerfixonline.crud.upload.UploadBaseFolder;
import com.tigerfixonline.crud.util.ImageUtil;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import se.walkercrou.places.AddressComponent;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Photo;
import se.walkercrou.places.Place;

public class GooglePlacesComponent extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2468018699252723738L;

	/*
	 * Google places
	 */
	private GooglePlaces client;
	private TextField placeId;
	private Label descripion;
	private Link link;
	private Button findPlace;
	private Button hide;
	private Button addPlace;

	/*
	 * Persistence
	 */
	private BaseService service;
	private FileUtil fileUtil;

	private EntityView entityView;

	/*
	 * Layout
	 */
	private HorizontalLayout toolBar = new HorizontalLayout();

	/*
	 * Constants
	 */

	private final float field_length_em = 20.0f;
	private final String GOOGLE_PLACES_API_KEY = "AIzaSyCuXlycJDiLm3RvCnll3UKDNv0OhJ7ubFw";

	private String currentPlaceID;

	private Place currentPlace;

	/*
	 * UI State
	 */

	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	public GooglePlacesComponent(EntityView view, BaseService baseService, FileUtil fileUtility) {
		service = baseService;
		fileUtil = fileUtility;
		entityView = view;
		placeId = new TextField("Google place ID");
		placeId.setWidth(field_length_em * 2, Unit.EM);
		placeId.setPlaceholder("Google place ID look something like gheirtyvmqs9YgRWpBHCsMppEg");
		descripion = new Label();
		link = new Link("Click here to look up a Google Place ID", new ExternalResource(
				"https://developers.google.com/maps/documentation/javascript/examples/places-placeid-finder"));
		link.setTargetName("_blank");
		link.setTargetBorder(BorderStyle.NONE);
		findPlace = new Button("Find a Google Place");
		addPlace = new Button("Yes...Add the place to DB");
		addPlace.setVisible(false);
		hide = new Button("Hide component");
		hide.addClickListener(clicked -> {
			setVisible(false);
		});

		findPlace.addClickListener(clicked -> {
			currentPlaceID = placeId.getValue();
			UI ui = UI.getCurrent();

			Thread checkIDThread = new Thread(() -> {
				UI.setCurrent(ui);
				StringBuffer content = null;

				try {
					URL url = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid="
							+ currentPlaceID + "&key=AIzaSyCuXlycJDiLm3RvCnll3UKDNv0OhJ7ubFw");
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					in.close();
					con.disconnect();
				} catch (ProtocolException e) {
					CRUDException crudException = new CRUDException(e.getMessage(), e);
					logger.log(Level.SEVERE, "Couldn't form a URL to find a place " + currentPlaceID, crudException);
				} catch (MalformedURLException e) {
					CRUDException crudException = new CRUDException(e.getMessage(), e);
					logger.log(Level.SEVERE, "Couldn't form a URL to find a place " + currentPlaceID, crudException);
				} catch (IOException e) {
					CRUDException crudException = new CRUDException(e.getMessage(), e);
					logger.log(Level.SEVERE, "Couldn't form a URL to find a place " + currentPlaceID, crudException);
				}
				Gson gson = new GsonBuilder().create();
				PlaceIDStatus placeIDStatus = gson.fromJson(content.toString(), PlaceIDStatus.class);
				if (placeIDStatus.status.equals("INVALID_REQUEST")) {
					UI.getCurrent().access(() -> {
						descripion.setValue("Opps! ID not found, Try again...");
						logger.info("Google place ID not found " + currentPlaceID);
						Notification.show("Wrong Place ID", "Place try a diffrent Google Place ID", Type.ERROR_MESSAGE);
					});

				} else {
					/* Instantiated with API Key */
					client = new GooglePlaces(GOOGLE_PLACES_API_KEY);
					currentPlace = client.getPlaceById(currentPlaceID);
					UI.getCurrent().access(() -> {
						descripion.setValue("Is your place named " + currentPlace.getName() + "?");
						addPlace.setVisible(true);
						findPlace.setVisible(false);
						placeId.setEnabled(false);
					});

				}
			});

			checkIDThread.setName("checkIDThread");
			checkIDThread.start();
		});

		addPlace.addClickListener(clicked -> {
			addPlace.setEnabled(false);
			descripion.setValue("Addition in process...");
			UI ui = UI.getCurrent();

			Thread placeDownloader = new Thread(() -> {
				UI.setCurrent(ui);

				UI.getCurrent().access(() -> {
					Notification.show("Addition in process...", currentPlace.getName() + " is being added to the DB",
							Type.TRAY_NOTIFICATION);
				});

				Customer customer = new Customer();
				List<AddressComponent> addressComponents = currentPlace.getAddressComponents();
				StringBuilder addressLine1 = new StringBuilder();
				for (AddressComponent addressComponent : addressComponents) {
					List<String> types = addressComponent.getTypes();
					for (String type : types) {
						if (type.equals("street_number")) {
							addressLine1.append(addressComponent.getLongName() + " ");
						}
						if (type.equals("route")) {
							addressLine1.append(addressComponent.getLongName());
						}

						if (type.equals("locality")) {
							customer.setCity(addressComponent.getLongName());
						}
						if (type.equals("postal_code")) {
							customer.setZip(addressComponent.getLongName());
						}
						if (type.equals("administrative_area_level_1")) {
							customer.setState(addressComponent.getLongName());
						}

					}
				}
				customer.setName(currentPlace.getName());
				customer.setAddressLine1(addressLine1.toString());
				customer.setPhone(currentPlace.getPhoneNumber());
				customer.setWebsite(currentPlace.getWebsite());
				boolean addedEntity = service.addEntity(customer);
				logger.info("Entity added by google places " + customer.toString());

				if (addedEntity) {
					/*
					 * Downloading photos
					 */

					List<Photo> photos = currentPlace.getPhotos();

					for (int i = 0; i < photos.size(); i++) {
						Photo downloaded = photos.get(i).download();
						InputStream inputStream = downloaded.getInputStream();
						byte[] imageArray = ImageUtil.getFileArray(inputStream);
						String mimeType = getMIMEType(photos.get(i).getReference());
						String extention = mimeType.substring(mimeType.lastIndexOf('/') + 1);
						String currentPhotoName = renamePlaceName(currentPlace.getName(),
								String.valueOf("record" + customer.getId()), i + 1, extention);

						boolean createBlobAt = fileUtil.createFileAt(currentPhotoName, mimeType,
								UploadBaseFolder.vaadinuploadedimages.name(), imageArray);
						long fileSize = fileUtil.getFileSize(currentPhotoName,
								UploadBaseFolder.vaadinuploadedimages.name());

						UI.getCurrent().access(() -> {
							Notification.show(currentPhotoName + " is uploaded ", Type.TRAY_NOTIFICATION);
						});

						logger.info(currentPhotoName + " uploaded...");

						if (createBlobAt) {
							customer.addImageInfo(new ImageInfo(currentPhotoName, mimeType, fileSize));
						}
					}

					/* persist entity */
					boolean updateEntity = service.updateEntity(customer);
					if (updateEntity) {
						UI.getCurrent().access(() -> {
							logger.info("Entity updated by google places " + customer.toString());
							Notification.show(currentPlace.getName(), "Persisted to DB", Type.TRAY_NOTIFICATION);
							/* refresh UI */
							entityView.showEntities();
							setVisible(false);
							placeId.setEnabled(true);
							addPlace.setVisible(false);
							addPlace.setEnabled(true);
							findPlace.setVisible(true);
							placeId.setValue("");
							placeId.setPlaceholder("Google place ID look something like gheirtyvmqs9YgRWpBHCsMppEg");
							descripion.setValue("");
						});
					}
				}

			});
			placeDownloader.setName("placeDownloader");
			placeDownloader.start();

		});
		toolBar.addComponents(hide, findPlace, addPlace);
		addComponents(placeId, link, descripion, toolBar);
	}

	private String renamePlaceName(String placeName, String recordNo, int photoNumber, String ext) {
		return placeName.replaceAll("[,.!? ]", "_").concat("_" + recordNo).concat("_" + photoNumber).concat("." + ext);
	}

	private String getMIMEType(final String photoreference) {
		String fileType = "Undetermined";
		try {
			final URL url = new URL("https://maps.googleapis.com/maps/api/place/photo?photoreference=" + photoreference
					+ "&key=AIzaSyCuXlycJDiLm3RvCnll3UKDNv0OhJ7ubFw");
			final URLConnection connection = url.openConnection();
			fileType = connection.getContentType();
		} catch (MalformedURLException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "ERROR: Bad URL for getMIMEType(...) ", crudException);
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Cannot access URLConnection - for getMIMEType(...) ", crudException);
		}
		return fileType;
	}

	public class PlaceIDStatus {
		private String status;
	}

}
