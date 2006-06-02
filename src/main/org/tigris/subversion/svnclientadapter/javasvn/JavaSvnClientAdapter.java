/*
 *  Copyright(c) 2003-2004 by the authors indicated in the @author tags.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tigris.subversion.svnclientadapter.javasvn;

import java.io.File;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNBaseDir;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNStatusUnversioned;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.javahl.AbstractJhlClientAdapter;
import org.tigris.subversion.svnclientadapter.javahl.JhlClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.javahl.JhlConverter;
import org.tigris.subversion.svnclientadapter.javahl.JhlNotificationHandler;
import org.tmatesoft.svn.core.javahl.SVNClientImpl;

/**
 * The JavaSVN Adapter works by providing an implementation of the
 * JavaHL SVNClientInterface.  This allows to provide a common
 * JavaHL implementation (AbstractJhlClientAdapter) where the specific
 * adapters just need to initialize the correct underlying classes.
 *
 */
public class JavaSvnClientAdapter extends AbstractJhlClientAdapter {

    private ISVNClientAdapter svnAdmin;
    
    public JavaSvnClientAdapter() {
        svnClient = SVNClientImpl.newInstance();
        notificationHandler = new JhlNotificationHandler();
        svnClient.notification2(notificationHandler);        
        svnClient.setPrompt(new DefaultPromptUserPassword());
    }

    public void createRepository(File path, String repositoryType)
            throws SVNClientException {
        getSvnAdmin();
        if (svnAdmin == null)
            throw new SVNClientException("Create repository method not implemented.");
        else {
            svnAdmin.createRepository(path, repositoryType);
        }

    }
    
    private void getSvnAdmin () {
        if (svnAdmin == null) {
	        try {
	            JhlClientAdapterFactory.setup();
	        } catch (SVNClientException e) {
	        }
	        svnAdmin = SVNClientAdapterFactory.createSVNClient(JhlClientAdapterFactory.JAVAHL_CLIENT);
	        if (svnAdmin == null) {
		        try {
		            CmdLineClientAdapterFactory.setup();
		        } catch (SVNClientException ex) {
		        }
	            svnAdmin = SVNClientAdapterFactory.createSVNClient(CmdLineClientAdapterFactory.COMMANDLINE_CLIENT);
	        }
        }
        
    }
    public void addPasswordCallback(ISVNPromptUserPassword callback) {
        if (callback != null) {
	        JavaSvnPromptUserPassword prompt = new JavaSvnPromptUserPassword(callback);
	        this.setPromptUserPassword(prompt);
	        if (svnAdmin != null)
	            svnAdmin.addPasswordCallback(callback);
        }
    }
    public boolean statusReturnsRemoteInfo() {
        return true;
    }
    public long[] commitAcrossWC(File[] paths, String message, boolean recurse,
            boolean keepLocks, boolean atomic) throws SVNClientException {
        try {
            if (message == null) {
            	message = "";
            }
            notificationHandler.setCommand(ISVNNotifyListener.Command.COMMIT);
            String[] files = new String[paths.length];
            String commandLine = "commit -m \""+message+"\"";
            if (!recurse)
                commandLine+=" -N";
            if (keepLocks)
                commandLine+=" --no-unlock";

            for (int i = 0; i < paths.length; i++) {
                files[i] = fileToSVNPath(paths[i], false);
                commandLine+=" "+ files[i];
            }
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir();

            long[] newRev = ((SVNClientImpl)svnClient).commit(files, message, recurse, keepLocks, atomic);
            return newRev;
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }

     }
    
    public boolean canCommitAcrossWC() {
        return true;
    }

    /**
     * Returns the status of files and directory recursively
     *
     * @param path File to gather status.
     * @param descend get recursive status information
     * @param getAll get status information for all files
     * @param contactServer contact server to get remote changes
     *  
     * @return a Status
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll, boolean contactServer) throws SVNClientException {
		notificationHandler.setCommand(ISVNNotifyListener.Command.STATUS);
		String filePathSVN = fileToSVNPath(path, false);
		notificationHandler.logCommandLine("status " + (contactServer?"-u ":"")+ filePathSVN);
		notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
		try {
			Status[] statuses = 
                svnClient.status(
                        filePathSVN,  
                        descend,            // If descend is true, recurse fully, else do only immediate children.
                        contactServer,      // If update is set, contact the repository and augment the status structures with information about out-of-dateness     
    					getAll,getAll);    // retrieve all entries; otherwise, retrieve only "interesting" entries (local mods and/or out-of-date).
			if (statuses.length == 0)
				return new ISVNStatus[] {new SVNStatusUnversioned(path)};
			else
			return JhlConverter.convert(statuses);
		} catch (ClientException e) {
			if (e.getAprError() == SVN_ERR_WC_NOT_DIRECTORY) {
				// when there is no .svn dir, an exception is thrown ...
				return new ISVNStatus[] {new SVNStatusUnversioned(path)};
			} else {
				notificationHandler.logException(e);
				throw new SVNClientException(e);
			}
		}
    }

   
}
