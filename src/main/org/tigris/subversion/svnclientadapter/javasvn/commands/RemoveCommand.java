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
import org.tigris.subversion.svnclientadapter.SVNUrlUtils;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.util.PathUtil;

public class RemoveCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public RemoveCommand(JavaSvnConfig config) {
        super(config);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#remove(org.tigris.subversion.svnclientadapter.SVNUrl[],
     *      java.lang.String)
     */
    public void remove(SVNUrl[] url, String message) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.REMOVE);

            String commandLine = "delete -m \"" + message + "\"";

            String targets[] = new String[url.length];
            for (int i = 0; i < url.length; i++) {
                commandLine += " " + url[i];
            }
            notificationHandler.logCommandLine(commandLine);

            SVNUrl rootUrl = SVNUrlUtils.getCommonRootUrl(url);
            if (rootUrl == null) {
                throw new SVNException(
                        "all locations should be within the same repository");
            }
            SVNRepository repository = getRepository(rootUrl);
            ISVNEditor editor = repository.getCommitEditor(message, null);
            editor.openRoot(-1);
            for (int i = 0; i < url.length; i++) {
                String relPath = SVNUrlUtils.getRelativePath(rootUrl, url[i]);
                editor.deleteEntry(PathUtil.decode(relPath), -1);
            }
            editor.closeEdit();

        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#remove(java.io.File[],
     *      boolean)
     */
    public void remove(File[] file, boolean force) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.REMOVE);

            String commandLine = "delete" + (force ? " --force" : "");
            String targets[] = new String[file.length];

            for (int i = 0; i < file.length; i++) {
                commandLine += " " + file[i].toString();
            }
            notificationHandler.logCommandLine(commandLine);

            for (int i = 0; i < file.length; i++) {
                ISVNWorkspace ws = getRootWorkspace(file[i]);
                String workspacePath = getWorkspacePath(ws, file[i]);
                ws.delete(workspacePath);
            }
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

}
