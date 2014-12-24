/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright 2006-2014 by respective authors (see below). All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.red5.examples.s3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.Red5;
import org.red5.server.api.stream.IStreamFilenameGenerator;
import org.slf4j.Logger;

/**
 * Provides custom playback and recording directories for use with S3.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class S3FilenameGenerator implements IStreamFilenameGenerator {

	private static Logger logger = Red5LoggerFactory.getLogger(S3FilenameGenerator.class, "examples");
	
    // Path that will store recorded videos
    public static String recordPath;
    // Path that contains VOD streams
    public static String playbackPath;
    // Create a random generator
	public static Random rnd = new Random();
	
	// S3 bucket name
	private static String bucketName;
	// S3 access key
	private static String accessKey;
	// S3 secret access key
	private static String secretKey;
	
	private static AWSCredentials awsCredentials;
	
	public void init() {
		S3FilenameGenerator.awsCredentials = new AWSCredentials(accessKey, secretKey);
		logger.debug("Credentials: {}", awsCredentials.getFriendlyName());
		// check for the bucket name, if not found create it
		List<String> buckets = S3FilenameGenerator.getBucketList();
		if (!buckets.contains(bucketName)) {
			S3FilenameGenerator.createBucket();
		}
	}

	public String generateFilename(IScope scope, String name, GenerationType type) {
		return generateFilename(scope, name, null, type);
	}

	@SuppressWarnings("deprecation")
	public String generateFilename(IScope scope, String name, String extension, GenerationType type) {
    	logger.debug("Get stream directory: scope={}, name={}, type={}", new Object[]{scope, name, type.toString()});		
		StringBuilder path = new StringBuilder();
		// get the session id
		IConnection conn = Red5.getConnectionLocal();
		if (conn.hasAttribute("sessionId")) {
			String sessionId = conn.getStringAttribute("sessionId");
			path.append(sessionId);
			path.append('/');
		}
		// add resources name
		path.append(name);
		// add extension if we have one
        if (extension != null){
            // add extension
        	path.append(extension);
        }		
		// determine whether its playback or record
		if (type.equals(GenerationType.PLAYBACK)) {
			logger.debug("Playback path used");
			// look on s3 for the file first	
			boolean found = false;
			try {
				S3Service s3Service = new RestS3Service(awsCredentials);
				S3Bucket bucket = s3Service.getBucket(bucketName);
				String objectKey = path.toString();
				S3Object file = s3Service.getObject(bucket, objectKey);
				if (file != null) {
					S3Object details = s3Service.getObjectDetails(bucket, objectKey);
					logger.debug("Details - key: {} content type: {}", details.getKey(), details.getContentType()); 
					path.insert(0, bucket.getLocation());
					// set found flag
					found = true;
				}
			} catch (S3ServiceException e) {
				logger.warn("Error looking up media file", e);
			}
			// use local path
			if (!found) {
				logger.debug("File was not found on S3, using local playback location");
				path.insert(0, playbackPath);
			}
		} else {
			logger.debug("Record path used");
			path.insert(0, recordPath);
		}

        String fileName = path.toString();
        logger.debug("Generated filename: {}", fileName);
        return fileName;
	}
	
    public boolean resolvesToAbsolutePath() {
    	return true;
    }
    
    public void setPlaybackPath(String path) {
    	logger.debug("Set playback path: {}", path); 
    	playbackPath = path;
    }

    public void setRecordPath(String path) {
    	logger.debug("Set record path: {}", path); 
        recordPath = path;
    }
    
    public static String getPlaybackPath() {
		return playbackPath;
	}
    
    public static String getRecordPath() {
		return recordPath;
	}

	public static String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		logger.debug("Setting bucket name: {}", bucketName);
		S3FilenameGenerator.bucketName = bucketName;
	}

	public static String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		logger.debug("Setting access key: {}", accessKey);
		S3FilenameGenerator.accessKey = accessKey;
	}

	public static String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		logger.debug("Setting secret key: {}", secretKey);
		S3FilenameGenerator.secretKey = secretKey;
	}

	public static void createBucket() {
		logger.debug("Create bucket");
		try {
			S3Service s3Service = new RestS3Service(awsCredentials);
			S3Bucket bucket = s3Service.createBucket(bucketName);
			logger.debug("Created bucket: {}", bucket.getName());
		} catch (S3ServiceException e) {
			logger.error("Error creating bucket", e);
		}		
	}
	
	public static List<String> getBucketList() {
		logger.debug("Get the bucket list");
		List<String> bucketList = new ArrayList<String>(3);
		try {
			S3Service s3Service = new RestS3Service(awsCredentials); 
			S3Bucket[] buckets = s3Service.listAllBuckets();
			for (S3Bucket bucket: buckets) {
				logger.debug("Bucket: {}", bucket.getName());
				bucketList.add(bucket.getName());
			}
			logger.debug("Bucket count: {}", buckets.length);
		} catch (S3ServiceException e) {
			logger.error("Error during bucket listing", e);
		}
		return bucketList;
	}
	
	public static void upload(String sessionId, String name) {
		logger.debug("Upload - session id: {} name: {}", sessionId, name);
		try {
			// find the file
			StringBuilder sb = new StringBuilder(recordPath);
			sb.append(sessionId);
			sb.append('/');
			sb.append(name);
			sb.append(".flv");
			String filePath = sb.toString();
			logger.debug("File path: {}", filePath);
			File file = new File(filePath);
			if (file.exists()) {
				S3Service s3Service = new RestS3Service(awsCredentials);
				S3Bucket bucket = s3Service.createBucket(bucketName);
				S3Object sob = new S3Object(sessionId + "/" + name + ".flv");
				// force bucket name
				sob.setBucketName(bucketName);
				// point at file
				sob.setDataInputFile(file);
				// set type
				sob.setContentType("video/x-flv");
				// set auth / acl
				sob.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);				
				logger.debug("Pre-upload: {}", sob);
				sob = s3Service.putObject(bucket, sob);
				logger.debug("Post-upload: {}", sob);						
			} else {
				logger.warn("File was not found");
			}
			file = null;
		} catch (S3ServiceException e) {
			logger.error("Error during upload", e);
		}		
	}
	
	public static String generateCustomName() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(RandomStringUtils.randomAlphanumeric(32));
    	sb.append('_');
    	int i = rnd.nextInt(99999);
    	if (i < 10) {
    		sb.append("0000");
    	} else if (i < 100) {
       		sb.append("000");
    	} else if (i < 1000) {
       		sb.append("00");
    	} else if (i < 10000) {
       		sb.append("0");
    	}    	
    	sb.append(i);
    	return sb.toString();
    }
    
}
