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
package org.tigris.subversion.svnclientadapter.javasvn.commands;

import java.io.File;

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;

public class ImportCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public ImportCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doImport(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String,
     *      boolean)
     */
    public void doImport(File path, SVNUrl url, String message, boolean recurse)
            throws SVNClientException {
        if (!recurse) {
            throw new SVNClientException(
                    "non-recursive import is not supported");
        }
        notificationHandler.setCommand(ISVNNotifyListener.Command.IMPORT);
        String dest = url.toString();
        notificationHandler.logCommandLine("import -m \"" + message + "\" "
                + (recurse ? "" : "-N ") + path.toString() + ' ' + dest);
        try {
            ISVNWorkspace ws = getRootWorkspace(path);
            ws.commit(SVNRepositoryLocation.parseURL(url.toString()), message);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }

    }
    
    
}
