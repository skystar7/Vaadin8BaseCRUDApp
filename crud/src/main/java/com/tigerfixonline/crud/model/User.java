package com.tigerfixonline.crud.model;

public class User {

	private boolean isLogged;
	private boolean isAuthorized;
	private UserType userType;

	public boolean isLogged() {
		return isLogged;
	}

	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}
	
	public boolean isAuthorized() {
		return isAuthorized;
	}

	public void setAuthorized(boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getService() {

		if (userType instanceof GoogleUser) {
			return "GooglePlus ID";
		}

		return "";
	}

	public interface UserType {
		
		String getID();

		String getName();

		String getImagePath();
	}

	public class GoogleUser implements UserType {
		private String id;
		private String displayName;
		private UserImage image;

		private class UserImage {
			private String url;

			@Override
			public String toString() {
				return "UserImage [url=" + url + "]";
			}
		}

		@Override
		public String toString() {
			return "GoogleUser [id=" + id + ", displayName=" + displayName + ", image=" + image + "]";
		}

		@Override
		public String getName() {
			return displayName;
		}

		@Override
		public String getImagePath() {
			return image.url;
		}

		@Override
		public String getID() {
			return id;
		}
	}

	@Override
	public String toString() {
		return "User [isLogged=" + isLogged + ", userType=" + userType + "]";
	}

}
