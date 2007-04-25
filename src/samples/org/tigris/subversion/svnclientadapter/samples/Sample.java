/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.samples;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.javahl.JhlClientAdapterFactory;

/**
 * A very simple sample
 * see svnant task for more samples and unit tests
 *  
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
public class Sample {

	public static class NotifyListener implements ISVNNotifyListener {
		public void setCommand(int cmd) {
			// the command that is being executed. See ISVNNotifyListener.Command
			// ISVNNotifyListener.Command.ADD for example 
		}
		public void logMessage(String message) {
			System.out.println(message);
		}

		public void logCommandLine(String message) {
			// the command line used
			System.out.println(message);
		}

		public void logError(String message) {
			// when an error occurs
			System.out.println("error :" +message);
		}
    
        public void logRevision(long revision, String path) {
			// when command completes against revision
			System.out.println("revision :" +revision);
        }

        public void logCompleted(String message) {
			// when command completed
			System.out.println(message);
		}

		public void onNotify(File path, SVNNodeKind nodeKind) {
			// each time the status of a file or directory changes (file added, reverted ...)
			// nodeKind is SVNNodeKind.FILE or SVNNodeKind.DIR
			
			// this is the function we use in subclipse to know which files need to be refreshed
			
			System.out.println("Status of "+path.toString()+" has changed");
		}
	}

    public void setup() {
        try {
            JhlClientAdapterFactory.setup();
        } catch (SVNClientException e) {
            // can't register this factory
        }
        try {
            CmdLineClientAdapterFactory.setup();
        } catch (SVNClientException e1) {
            // can't register this factory
        }
        
    }
    
	public void run() {
        // register the factories
        setup();
		
        // first create the SVNClient from factory
		// SVNClientAdapterFactory.JAVAHL_CLIENT to use JNI client (recommanded)
		// SVNClientAdapterFactory.COMMANDLINE_CLIENT to use command line client
		// You can also get the best client type interface using getBestSVNClientType
		ISVNClientAdapter svnClient;
		try {
			String bestClientType = SVNClientAdapterFactory.getPreferredSVNClientType();
            System.out.println("Using "+bestClientType+" factory");
			svnClient = SVNClientAdapterFactory.createSVNClient(bestClientType);
		} catch (SVNClientException e) {
			System.out.println(e.getMessage());
			return;
		}
		

		// set username and password if necessary
		svnClient.setUsername("guest");
		svnClient.setPassword(" ");

		//	add a listener if you wish
		NotifyListener listener = new Sample.NotifyListener();
		svnClient.addNotifyListener(listener);

		try {
			//	use the svn commands
			InputStream is = svnClient.getContent(new SVNUrl("http://subclipse.tigris.org/svn/subclipse/trunk/svnClientAdapter/readme.txt"),SVNRevision.HEAD);
			
			System.out.println("The beginning of the file is :");
			byte[] bytes = new byte[100];
			is.read(bytes);
			System.out.println(new String(bytes));
		} catch (IOException e) {
			System.out.println("An exception occured while getting remote file :"+e.getMessage());
		} catch (SVNClientException e) {
			System.out.println("An exception occured while getting remote file :"+e.getMessage());
		}
	}


	public static void main(String[] args) {
		Sample sample = new Sample();
		sample.run();
	}
}
