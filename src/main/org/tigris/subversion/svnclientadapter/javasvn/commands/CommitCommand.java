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
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;

public class CommitCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public CommitCommand(JavaSvnConfig config) {
        super(config);
    }

    public long commit(File[] paths, String message, boolean recurse)
            throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.COMMIT);
            String[] files = new String[paths.length];
            String commandLine = "commit -m \"" + message + "\"";
            if (!recurse)
                commandLine += " -N";
            for (int i = 0; i < paths.length; i++) {
                commandLine += " " + paths[i].toString();
            }
            notificationHandler.logCommandLine(commandLine);

            ISVNWorkspace ws = getRootWorkspace(paths);
            String[] workspacePaths = new String[paths.length];
            for (int i = 0; i < workspacePaths.length; i++) {
                workspacePaths[i] = getWorkspacePath(ws, paths[i]);
            }
            return ws.commit(workspacePaths, message, recurse);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

}