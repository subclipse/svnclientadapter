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
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnUtils;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;

public class AddCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public AddCommand(JavaSvnConfig config) {
        super(config);
    }

    public void addFile(File file) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.ADD);
        notificationHandler.logCommandLine("add -N " + file.toString());

        try {
            ISVNWorkspace ws = getRootWorkspace(file.getParentFile());
            ws.add(JavaSvnUtils.getWorkspacePath(ws, file), false, false);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    public void addDirectory(File dir, boolean recurse)
            throws SVNClientException {
        try {
            ISVNWorkspace ws = getRootWorkspace(dir.getParentFile());
            ws.add(getWorkspacePath(ws, dir), false, recurse);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }    
    
}
