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
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.util.PathUtil;

public class MkdirCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public MkdirCommand(JavaSvnConfig config) {
        super(config);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#mkdir(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.lang.String)
     */
    public void mkdir(SVNUrl url, String message) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.MKDIR);
            notificationHandler.logCommandLine("mkdir -m \"" + message + "\" "
                    + url.toString());

            ISVNEditor editor = null;
            try {
                SVNRepository repository = getRepository(url.getParent());
                editor = repository.getCommitEditor(message, null);
                editor.openRoot(-1);
                editor.addDir(PathUtil.decode(url.getLastPathSegment()), null,
                        -1);
                editor.closeDir();
                editor.closeDir();
                editor.closeEdit();
            } catch (SVNException e) {
                if (editor != null) {
                    try {
                        editor.abortEdit();
                    } catch (SVNException inner) {
                    }
                }
                throw e;
            }

        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#mkdir(java.io.File)
     */
    public void mkdir(File file) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.MKDIR);
            notificationHandler.logCommandLine("mkdir " + file.toString());

            ISVNWorkspace ws = getRootWorkspace(file);
            ws.add(getWorkspacePath(ws, file), true, false);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
    

}
