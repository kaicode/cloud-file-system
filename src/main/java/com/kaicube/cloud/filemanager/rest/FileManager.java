package com.kaicube.cloud.filemanager.rest;

import com.kaicube.cloud.filemanager.io.FileSystem;
import com.kaicube.cloud.filemanager.io.FileSystemException;
import com.kaicube.cloud.filemanager.io.FileSystemFactory;
import com.kaicube.cloud.filemanager.io.FileSystemFactoryException;
import org.apache.wink.common.model.multipart.BufferedInMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;

@Path("/")
public class FileManager {

	public static final String CLASS = "class";
	public static final String INIT_STRING = "initString";
	private final Logger logger = LoggerFactory.getLogger(FileManager.class);;

	private FileSystem fileSystem;

	public FileManager() throws IOException, FileSystemFactoryException {
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/FileSystem.properties"));
		FileSystemFactory fileSystemFactory = new FileSystemFactory();
		this.fileSystem = fileSystemFactory.createFileSystem(properties.getProperty(CLASS), properties.getProperty(INIT_STRING));
	}

	@GET
	@Path("/directory-tree")
	@Produces(APPLICATION_JSON)
	public JSONArray directoryTree() throws WebApplicationException, JSONException {
		try {
			List<String> directories = fileSystem.directoryTree();
			return listToJsonObjectsWithName(directories);
		} catch (FileSystemException e) {
			throw new WebApplicationException(e, e.getStatusCode());
		}
	}

	@GET
	@Path("/file-list/{dirPath: .*}")
	@Produces(APPLICATION_JSON)
	public JSONArray listFiles(@PathParam("dirPath") String dirPath) throws FileSystemException, JSONException {
		try {
			logger.info("list files '{}'", dirPath);
			List<String> files = fileSystem.listFiles(dirPath);
			return listToJsonObjectsWithName(files);
		} catch (FileSystemException e) {
			throw new WebApplicationException(e, e.getStatusCode());
		}
	}

	@GET
	@Path("/file/{filePath: .*}")
	@Produces(WILDCARD)
	public InputStream loadFile(@PathParam("filePath") String filePath) throws FileSystemException, JSONException {
		try {
			logger.info("load file '{}'", filePath);
			return fileSystem.loadFile(filePath);
		} catch (FileSystemException e) {
			throw new WebApplicationException(e, e.getStatusCode());
		}
	}

	@POST
	@Path("/file/{filePath: .*}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	public JSONObject saveFile(BufferedInMultiPart bufferedInMultiPart, @PathParam("filePath") String filePath) throws FileSystemException {
		try {
			InPart inPart = bufferedInMultiPart.getParts().get(0);
			InputStream fileData = inPart.getInputStream();
			fileSystem.saveFile(filePath, fileData);
			return new JSONObject();
		} catch (FileSystemException e) {
			throw new WebApplicationException(e, e.getStatusCode());
		}
	}

	private JSONArray listToJsonObjectsWithName(List<String> directories) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (String directory : directories) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", directory);
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

}
