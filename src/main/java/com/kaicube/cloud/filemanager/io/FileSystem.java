package com.kaicube.cloud.filemanager.io;

import java.io.InputStream;
import java.util.List;

public interface FileSystem {

	List<String> directoryTree() throws FileSystemException;
	List<String> listFiles(String dir) throws FileSystemException;
	InputStream loadFile(String path) throws FileSystemException;
	void saveFile(String path, InputStream fileData) throws FileSystemException;
}
