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

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword;
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
        getSvnAdmin();
    }

    public void createRepository(File path, String repositoryType)
            throws SVNClientException {
        if (svnAdmin == null)
            throw new SVNClientException("Create repository method not implemented.");
        else {
            svnAdmin.createRepository(path, repositoryType);
        }

    }
    
    private void getSvnAdmin () {
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
    public void addPasswordCallback(ISVNPromptUserPassword callback) {
        super.addPasswordCallback(callback);
        if (svnAdmin != null)
            svnAdmin.addPasswordCallback(callback);
    }
}
