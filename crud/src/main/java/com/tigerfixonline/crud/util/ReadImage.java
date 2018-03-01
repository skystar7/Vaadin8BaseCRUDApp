package com.tigerfixonline.crud.util;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.tigerfixonline.crud.upload.RemoteFileUtil;

/*
 * TEST CLASS
 */
public class ReadImage {

	public static void main(String[] args) {
		viewImage("landscape_104-wallpaper-1920x1080_record3.jpg", "vaadinuploadedimages");

	}

	public static void viewImage(String blobName, String bucketName) {
		RemoteFileUtil remoteFileUtil = RemoteFileUtil.getStorageInstance();
		Image image = null;
		try {
			image = ImageIO.read(remoteFileUtil.getImageArrayAt(blobName, bucketName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame();
		frame.setSize(300, 300);
		JLabel label = new JLabel(new ImageIcon(image));
		frame.add(label);
		frame.setVisible(true);
	}
}
