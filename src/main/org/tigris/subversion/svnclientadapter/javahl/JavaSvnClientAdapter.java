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
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.File;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword;
import org.tigris.subversion.svnclientadapter.SVNBaseDir;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapterFactory;
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
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(paths));

            long[] newRev = ((SVNClientImpl)svnClient).commit(files, message, recurse, keepLocks, atomic);
            if (newRev != null){
                for (int i = 0; i < newRev.length; i++) {
                    if (newRev[i] > 0)
                        notificationHandler.logCompleted("Committed revision " + newRev[i] + ".");
                }
            }
            return newRev;
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }

     }
    
    public boolean canCommitAcrossWC() {
        return true;
    }

   
}
