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

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamAwareScopeHandler;
import org.slf4j.Logger;

/**
 * Main application.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class Application extends MultiThreadedApplicationAdapter implements	IStreamAwareScopeHandler {

	private static Logger log = Red5LoggerFactory.getLogger(Application.class, "examples"); 
	
	// whether or not we are want to persist our flv files to S3
	private static boolean persistToS3 = false;
	
	@Override
	public void streamBroadcastClose(IBroadcastStream stream) {
		log.debug("Broadcast close called. Stream name: {}", stream.getName());
		super.streamBroadcastClose(stream);
		// TODO: call the process to move the flv to S3 because its not possible to stream the bytes directly to S3 during flv write
		if (persistToS3) {
    		// move the completed flv to S3
    		S3FilenameGenerator.upload("Session-" + System.currentTimeMillis(), stream.getPublishedName());
		}
	}
		
	public static boolean isPersistToS3() {
		return persistToS3;
	}

	public void setPersistToS3(boolean persistToS3) {
		log.debug("Updating S3 persist flag: {}", persistToS3);
		Application.persistToS3 = persistToS3;
	}

}
