package com.tigerfixonline.crud.upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;

public class RemoteFileUtil implements FileUtil {

	private static RemoteFileUtil googleCloudStorage;
	private static Storage storage;
	/*
	 * Logger
	 */
	private Logger logger = LoggerUtil.getLogger(getClass());

	private RemoteFileUtil() {
		try {
			storage = StorageOptions.newBuilder()
					.setCredentials(ServiceAccountCredentials
							.fromStream(new FileInputStream("VaadinSignInDemo-344a9236d1aa.json")))
					.setProjectId("vaadinsignindemo").build().getService();

		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't load josn file while buiding Storage object", crudException);
		}

	}

	public static RemoteFileUtil getStorageInstance() {
		if (googleCloudStorage == null)
			googleCloudStorage = new RemoteFileUtil();
		return googleCloudStorage;
	}

	public boolean createBucket(String bucketName) {
		Bucket bucket = storage.create(BucketInfo.of(bucketName));
		return bucket.exists();
	}

	public boolean isBucketExists(String bucketName) {
		Page<Bucket> buckets = storage.list();
		Iterator<Bucket> iterator = buckets.iterateAll().iterator();
		while (iterator.hasNext()) {
			Bucket bucket = iterator.next();
			if (bucketName.equals(bucket.getName()))
				return true;
		}

		return false;
	}

	public boolean createBlobAt(String blobName, String mimeType, String bucketName, byte[] byteArray) {
		boolean bucketExists = isBucketExists(bucketName);
		boolean bucketCreated = false;
		if (!bucketExists)
			bucketCreated = createBucket(bucketName);

		BlobId blobId = BlobId.of(bucketName, blobName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
		Blob blob = storage.create(blobInfo, byteArray);
		return bucketExists ? blob.exists() : bucketCreated && blob.exists();
	}

	public boolean isBlobExistsAt(String blobName, String bucketName) {
		BlobId blobId = BlobId.of(bucketName, blobName);
		Blob blob = storage.get(blobId);
		if (blob == null)
			return false;
		else
			return blob.exists();
	}

	public Long getBlobSize(String blobName, String bucketName) {
		BlobId blobId = BlobId.of(bucketName, blobName);
		Blob blob = storage.get(blobId);
		return blob.getSize();
	}

	public boolean deleteBlobAt(String blobName, String bucketName) {
		BlobId blobId = BlobId.of(bucketName, blobName);
		return storage.delete(blobId);
	}

	public ByteArrayInputStream getBlobAt(String blobName, String bucketName) {
		BlobId blobId = BlobId.of(bucketName, blobName);
		return new ByteArrayInputStream(storage.readAllBytes(blobId));
	}

	private String getMIMEType(String blobName, String bucketName) {
		BlobId blobId = BlobId.of(bucketName, blobName);
		Blob blob = storage.get(blobId);
		return blob.getContentType();
	}

	public URL getURL(String blobName, String bucketName) {
		URL url = null;
		try {
			url = new URL("https://storage.cloud.google.com/" + bucketName + "/" + blobName);
		} catch (MalformedURLException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Bad URL for " + blobName + " at " + bucketName, crudException);
		}
		return url;
	}

	private BufferedImage getBufferedImageAt(String fileName, String baseFolder) {
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(getImageArrayAt(fileName, baseFolder));
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't read bufferedImage for " + fileName + " at " + baseFolder,
					crudException);
		}
		return bufferedImage;
	}

	@Override
	public boolean createFileAt(String fileName, String baseFolder, byte[] array, String located) {
		return createBlobAt(fileName, getMIMEType(fileName, located), baseFolder, array);
	}

	@Override
	public boolean createFileAt(String fileName, String baseFolder, byte[] array) {
		return createBlobAt(fileName, getMIMEType(fileName, baseFolder), baseFolder, array);
	}

	@Override
	public boolean deleteFileAt(String fileName, String baseFolder) {
		return deleteBlobAt(fileName, baseFolder);
	}

	@Override
	public boolean isFileExistsAt(String fileName, String baseFolder) {
		return isBlobExistsAt(fileName, baseFolder);
	}

	@Override
	public ByteArrayInputStream getImageArrayAt(String fileName, String baseFolder) {
		return getBlobAt(fileName, baseFolder);
	}

	@Override
	public UploadedImage getImageAt(String fileName, String baseFolder) {
		return new RemoteImage(fileName, baseFolder);
	}

	@Override
	public boolean createFileAt(String fileName, String mimeType, String baseFolder, byte[] array) {
		return createBlobAt(fileName, mimeType, baseFolder, array);
	}

	@Override
	public long getFileSize(String fileName, String baseFolder) {
		return getBlobSize(fileName, baseFolder);
	}

	public class RemoteImage extends UploadedImage {

		private String imageName;
		private String baseFolder;
		private BufferedImage bufferedImage;

		public RemoteImage(String name, String folder) {
			this.imageName = name;
			this.baseFolder = folder;

		}

		@Override
		public Resource getImageResource() {
			return new ExternalResource(RemoteFileUtil.this.getURL(imageName, baseFolder));
		}

		@Override
		public BufferedImage getBufferedImage() {
			bufferedImage = RemoteFileUtil.this.getBufferedImageAt(imageName, baseFolder);
			return bufferedImage;
		}

	}

	@Override
	public String serviceName() {
		return "Remote file service using Google cloud storage";
	}

}
