/*******************************************************************************
 * Copyright (c) 2003, 2006 svnClientAdapter project and others.
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
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.File;
import java.text.MessageFormat;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.SVNAdmin;
import org.tigris.subversion.javahl.SVNClient;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * A JavaHL base implementation of {@link org.tigris.subversion.svnclientadapter.ISVNClientAdapter}.
 *
 * @author C�dric Chabanois (cchabanois at no-log.org)
 * @author Panagiotis Korros (pkorros at bigfoot.com) 
 *
 */
public class JhlClientAdapter extends AbstractJhlClientAdapter {

    private SVNAdmin svnAdmin;
    
	/**
	 * Default constructor
	 */
    public JhlClientAdapter() {
        svnClient = new SVNClient();
        svnAdmin = new SVNAdmin();
        notificationHandler = new JhlNotificationHandler();
        progressListener = new JhlProgressListener();
        svnClient.notification2(notificationHandler);
        svnClient.setPrompt(new DefaultPromptUserPassword());
        svnClient.setProgressListener(progressListener);
    }

	public boolean isThreadsafe() {
		return true;
	}

	/**
     * tells if JhlClientAdapter is usable
     * @return true if Jhl client adapter is available
     * @deprecated
     */
    public static boolean isAvailable() {
       	return JhlClientAdapterFactory.isAvailable();
    }
    
    /**
     * @return an error string describing problems during loading platform native libraries (if any)
     * @deprecated
     */
    public static String getLibraryLoadErrors() {
    	return JhlClientAdapterFactory.getLibraryLoadErrors();
    }

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#createRepository(java.io.File)
	 */
	public void createRepository(File path, String repositoryType) throws SVNClientException {
		try {
			String fsType = (repositoryType == null) ? REPOSITORY_FSTYPE_FSFS : repositoryType; 
		    notificationHandler.setCommand(ISVNNotifyListener.Command.CREATE_REPOSITORY);
		     
		    String target = fileToSVNPath(path,false);
		    notificationHandler.logCommandLine(
		    		MessageFormat.format(
		    				"create --fstype {0} {1}", 
							new String[] { fsType, target }));
		    svnAdmin.create(target, false, false, null, fsType);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);            
		}        
	    
	}

	/**
	 * @param logLevel
	 * @param filePath
	 */
	public static void enableLogging(int logLevel,File filePath) {
		SVNClient.enableLogging(logLevel,fileToSVNPath(filePath, false));	
	}

	public boolean statusReturnsRemoteInfo() {
		return true;
	}

	public String getNativeLibraryVersionString() {
		return svnClient.getVersion().toString();
	}
}
