/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
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

/**
 * A very simple sample
 * see svnant task for more samples and unit tests
 *  
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
public class Sample {

	public class NotifyListener implements ISVNNotifyListener {
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
	};

	public void run() {
		// first create the SVNClient from factory
		// SVNClientAdapterFactory.JAVAHL_CLIENT to use JNI client (recommanded)
		// SVNClientAdapterFactory.COMMANDLINE_CLIENT to use command line client
		// You can also get the best client type interface using getBestSVNClientType
		ISVNClientAdapter svnClient;
		try {
			int bestClientType = SVNClientAdapterFactory.getBestSVNClientType();
			svnClient = SVNClientAdapterFactory.createSVNClient(bestClientType);
		} catch (SVNClientException e) {
			System.out.println(e.getMessage());
			return;
		}
		

		// set username and password if necessary (this is not necessary for this sample)
		// svnClient.setUsername(username);
		// svnClient.setPassword(password);

		//	add a listener if you wish
		NotifyListener listener = new Sample.NotifyListener();
		svnClient.addNotifyListener(listener);

		try {
			//	use the svn commands
			InputStream is = svnClient.getContent(new SVNUrl("http://svn.collab.net/repos/subclipse/trunk/svnClientAdapter/readme.txt"),SVNRevision.HEAD);
			
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
