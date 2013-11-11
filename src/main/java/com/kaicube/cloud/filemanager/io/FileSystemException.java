package com.kaicube.cloud.filemanager.io;

public class FileSystemException extends Exception {

	private int statusCode;

	public FileSystemException(Throwable cause) {
		super(cause);
	}

	public FileSystemException(int statusCode, Throwable cause) {
		super(cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
