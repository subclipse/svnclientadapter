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
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

public class CopyCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public CopyCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File,
     *      java.io.File)
     */
    public void copy(File srcPath, File destPath) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
            notificationHandler.logCommandLine("copy " + srcPath + " "
                    + destPath);

            ISVNWorkspace ws = getRootWorkspace(srcPath);
            ws.copy(getWorkspacePath(ws, srcPath), getWorkspacePath(ws,
                    destPath), false);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String)
     */
    public void copy(File srcPath, SVNUrl destUrl, String message)
            throws SVNClientException {
        notImplementedYet("copy");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
            throws SVNClientException {
        notImplementedYet("copy");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public void copy(SVNUrl srcUrl, SVNUrl destUrl, String message,
            SVNRevision revision) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
        notificationHandler.logCommandLine("copy " + srcUrl + " " + destUrl);

        ISVNEditor editor = null;
        try {
            SVNRepository repository = getRepository(destUrl.getParent());
            long revNumber = getRevisionNumber(revision, repository, null, null);

            String srcPath = getRepositoryRootPath(repository, srcUrl);

            editor = repository.getCommitEditor(message, null);
            editor.openRoot(-1);
            editor.addDir(destUrl.getLastPathSegment(), srcPath, revNumber);
            editor.closeDir();
            editor.closeDir();
            editor.closeEdit();

        } catch (SVNException e) {
            if (editor != null) {
                try {
                    editor.abortEdit();
                } catch (SVNException es) {
                }
            }
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
    
}
