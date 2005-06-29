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
import org.tigris.subversion.svnclientadapter.SVNUrlUtils;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

public class MoveCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public MoveCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#move(java.io.File,
     *      java.io.File, boolean)
     */
    public void move(File srcPath, File destPath, boolean force)
            throws SVNClientException {
        // use force when you want to move file even if there are local
        // modifications
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.MOVE);
            notificationHandler.logCommandLine("move " + srcPath + ' '
                    + destPath);

            ISVNWorkspace ws = getRootWorkspace(srcPath);
            ws.copy(getWorkspacePath(ws, srcPath), getWorkspacePath(ws,
                    destPath), true);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#move(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public void move(SVNUrl srcUrl, SVNUrl destUrl, String message,
            SVNRevision revision) throws SVNClientException {
        ISVNEditor editor = null;
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.MOVE);
            notificationHandler.logCommandLine("move -m \"" + message
                    + "\" -r " + revision.toString() + ' ' + srcUrl + ' '
                    + destUrl);

            SVNUrl rootUrl = SVNUrlUtils.getCommonRootUrl(srcUrl, destUrl);
            if (rootUrl == null) {
                throw new SVNException(
                        "srcUrl and destUrl should be within the same repository");
            }
            SVNRepository repository = getRepository(rootUrl);
            long revNumber = getRevisionNumber(revision, repository, null, null);

            String deletePath = getRepositoryPath(repository, srcUrl);
            String destPath = getRepositoryPath(repository, destUrl);

            editor = repository.getCommitEditor(message, null);
            editor.openRoot(-1);

            editor.addDir(destPath, deletePath, revNumber);
            editor.closeDir();
            editor.deleteEntry(deletePath, revNumber);

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
