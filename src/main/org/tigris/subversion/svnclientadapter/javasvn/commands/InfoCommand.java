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
import java.util.Map;

import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNInfoUnversioned;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnInfo;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;

public class InfoCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public InfoCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getInfo(java.io.File)
     */
    public ISVNInfo getInfo(File file) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.INFO);

            notificationHandler.logCommandLine("info " + file.toString());

            Map properties = null;
            ISVNWorkspace ws = getRootWorkspace(file);
            properties = ws.getProperties(getWorkspacePath(ws, file), false,
                    true);

            if (properties != null) {
                return new JavaSvnInfo(file, properties);
            } else {
                return new SVNInfoUnversioned(file);
            }
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }    
    
}
