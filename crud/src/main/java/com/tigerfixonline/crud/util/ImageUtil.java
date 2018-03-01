package com.tigerfixonline.crud.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;

import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;

public class ImageUtil {

	/*
	 * Logger
	 */
	private static Logger logger = LoggerUtil.getLogger(ImageUtil.class);

	public static byte[] getFileArray(InputStream is) {
		ByteArrayOutputStream bufferedImage = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				bufferedImage.write(data, 0, nRead);
			}
			bufferedImage.flush();
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't read array ", crudException);
		}

		return bufferedImage.toByteArray();
	}

	public static byte[] scaleImage(BufferedImage bufferedImage, int targetSize, String imageName) {
		byte[] byteArray = null;

		try {
			/* scaling image */
			BufferedImage scaledBufferedImage = Scalr.resize(bufferedImage, /* scale down to */targetSize);
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			String extention = imageName.substring(imageName.lastIndexOf('.') + 1);
			ImageIO.write(scaledBufferedImage, extention, arrayOutputStream);
			arrayOutputStream.flush();
			byteArray = arrayOutputStream.toByteArray();
			arrayOutputStream.close();
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "IOException while scalling ", crudException);
		}
		return byteArray;

	}

	public static String getImageSize(double imageSize) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		if (imageSize < 1024)
			return imageSize + " bytes";
		else if (imageSize < (1_048_576))
			return numberFormat.format(imageSize / ((1_024))) + " KB";
		else if (imageSize > (1_048_576))
			return numberFormat.format(imageSize / ((1_048_576))) + " MB";
		return "image too large";
	}

	public static String getScalingTime(double time) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		if (time < 1_000_000)
			return numberFormat.format(time) + " nanoseconds";
		else if (time > 1_000_000 && time < 1_000_000_000)
			return numberFormat.format(time / 1_000_000) + " miliseconds";
		else if (time > 1_000_000_000)
			return numberFormat.format(time / 1_000_000_000) + " seconds";
		return "";
	}

}
