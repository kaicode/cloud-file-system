package com.kaicube.cloud.filemanager.io.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.kaicube.cloud.filemanager.io.FileSystem;
import com.kaicube.cloud.filemanager.io.FileSystemException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class S3FileSystem implements FileSystem {

	private AmazonS3Client amazonS3Client;
	private String bucket;

	public S3FileSystem(String accessKey, String secretKey, String bucket) {
		amazonS3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
		this.bucket = bucket;
	}

	public S3FileSystem(String initString) {
		this(initString.split(" ")[0], initString.split(" ")[1], initString.split(" ")[2]);
	}

	@Override
	public List<String> directoryTree() throws FileSystemException {
		try {
			Set<String> dirs = new TreeSet<String>();
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
			listObjectsRequest.setBucketName(bucket);
			ObjectListing objectListing = amazonS3Client.listObjects(listObjectsRequest);
			List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
			for (S3ObjectSummary objectSummary : objectSummaries) {
				String key = objectSummary.getKey();
				dirs.add(key.substring(0, key.lastIndexOf("/")));
			}

			// All parent directories listed
			Set<String> allDirs = new TreeSet<String>();
			StringBuilder s = new StringBuilder();
			for (String dir : dirs) {
				String[] parts = dir.split("/");
				s.setLength(0);
				for (String part : parts) {
					s.append(part);
					allDirs.add(s.toString());
					s.append("/");
				}
			}
			ArrayList<String> dirsList = new ArrayList<String>();
			dirsList.addAll(allDirs);
			return dirsList;
		} catch (AmazonS3Exception e) {
			int statusCode = e.getStatusCode();
			throw new FileSystemException(statusCode, e);
		} catch (AmazonClientException e) {
			throw new FileSystemException(e);
		}
	}

	@Override
	public List<String> listFiles(String dir) throws FileSystemException {
		try {
			if (!dir.endsWith("/")) {
				dir = dir + "/";
			}
			ArrayList<String> files = new ArrayList<String>();
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
			listObjectsRequest.setBucketName(bucket);
			listObjectsRequest.setPrefix(dir);
			listObjectsRequest.setDelimiter("/");
			ObjectListing objectListing = amazonS3Client.listObjects(listObjectsRequest);
			List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
			for (S3ObjectSummary objectSummary : objectSummaries) {
				files.add(objectSummary.getKey().substring(dir.length()));
			}
			return files;
		} catch (AmazonS3Exception e) {
			int statusCode = e.getStatusCode();
			throw new FileSystemException(statusCode, e);
		} catch (AmazonClientException e) {
			throw new FileSystemException(e);
		}
	}

	@Override
	public InputStream loadFile(String path) throws FileSystemException {
		try {
			S3Object object = amazonS3Client.getObject(bucket, path);
			return object.getObjectContent();
		} catch (AmazonS3Exception e) {
			int statusCode = e.getStatusCode();
			throw new FileSystemException(statusCode, e);
		} catch (AmazonClientException e) {
			throw new FileSystemException(e);
		}
	}

	@Override
	public void saveFile(String path, InputStream fileData) throws FileSystemException {
		try {
			amazonS3Client.putObject(bucket, path, fileData, null);
		} catch (AmazonS3Exception e) {
			int statusCode = e.getStatusCode();
			throw new FileSystemException(statusCode, e);
		} catch (AmazonClientException e) {
			throw new FileSystemException(e);
		}
	}

}
