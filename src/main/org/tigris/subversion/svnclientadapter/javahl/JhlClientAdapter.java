/*******************************************************************************
 * Copyright (c) 2003, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
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
        svnClient.notification2(notificationHandler);
        svnClient.setPrompt(new DefaultPromptUserPassword());
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

	public boolean statusReturnsRemoteInfo() {
		return true;
	}
}
