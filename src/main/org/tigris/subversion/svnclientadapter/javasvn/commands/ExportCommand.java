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
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.internal.ws.fs.FSUtil;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;

public class ExportCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public ExportCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doExport(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision,
     *      boolean)
     */
    public void doExport(SVNUrl srcUrl, File destPath, SVNRevision revision,
            boolean force) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.EXPORT);
            notificationHandler.logCommandLine("export -r "
                    + revision.toString() + ' ' + srcUrl.toString() + ' '
                    + destPath.toString());

            if (force) {
                FSUtil.deleteAll(destPath);
            }
            destPath.mkdirs();

            ISVNWorkspace ws = getWorkspace(destPath);
            SVNRepositoryLocation location = SVNRepositoryLocation
                    .parseURL(srcUrl.toString());
            SVNRepository repository = getRepository(location);

            long revNumber = getRevisionNumber(revision, repository, null, null);
            ws.checkout(location, revNumber, true);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doExport(java.io.File,
     *      java.io.File, boolean)
     */
    public void doExport(File srcPath, File destPath, boolean force)
            throws SVNClientException {
        notImplementedYet("doExport");
    }
    
    
}
