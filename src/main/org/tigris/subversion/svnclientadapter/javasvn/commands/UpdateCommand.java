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
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

public class UpdateCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public UpdateCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#update(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public long update(File path, SVNRevision revision, boolean recurse)
            throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.UPDATE);
            notificationHandler.logCommandLine("update -r "
                    + revision.toString() + ' ' + (recurse ? "" : "-N ")
                    + path.toString());

            ISVNWorkspace ws = getRootWorkspace(path);
            String wsPath = getWorkspacePath(ws, path);
            SVNRepository repository = getRepository(ws.getLocation());
            long revNumber = getRevisionNumber(revision, repository, ws, wsPath);

            return ws.update(wsPath, revNumber, recurse);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
    
}
