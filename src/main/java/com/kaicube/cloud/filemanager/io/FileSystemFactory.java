package com.kaicube.cloud.filemanager.io;

import java.lang.reflect.Constructor;

public class FileSystemFactory {

	public FileSystem createFileSystem(String className, String initString) throws FileSystemFactoryException {
		try {
			Class<FileSystem> aClass = (Class<FileSystem>) Class.forName(className);
			Constructor<?> constructor = aClass.getConstructor(String.class);
			return (FileSystem) constructor.newInstance(initString);
		} catch (Exception e) {
			throw new FileSystemFactoryException(e);
		}
	}

}
