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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnStatus;
import org.tmatesoft.svn.core.ISVNStatusHandler;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.SVNStatus;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.util.PathUtil;

public class StatusCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public StatusCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getSingleStatus(java.io.File)
     */
    public ISVNStatus getSingleStatus(File path) throws SVNClientException {
        return getStatus(new File[] { path })[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getStatus(java.io.File[])
     */
    public ISVNStatus[] getStatus(File[] path) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.STATUS);
        String commandLine = "status -N --no-ignore";
        for (int i = 0; i < path.length; i++) {
            commandLine += " " + path[i];
        }
        notificationHandler.logCommandLine(commandLine);

        ISVNStatus[] statuses = new ISVNStatus[path.length];
        for (int i = 0; i < path.length; i++) {
            File file = path[i];
            try {
                ISVNWorkspace ws = getRootWorkspace(file);
                String workspacePath = getWorkspacePath(ws, file);
                SVNStatus status = ws.status(workspacePath, false);
                statuses[i] = new JavaSvnStatus(file, ws.getProperties(
                        workspacePath, false, true), status);
            } catch (SVNException e) {
                notificationHandler.logException(e);
                throw new SVNClientException(e);
            }
        }
        return statuses;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getStatus(java.io.File,
     *      boolean, boolean)
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll)
            throws SVNClientException {
        return getStatus(path, descend, getAll, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getStatus(java.io.File,
     *      boolean, boolean, boolean)
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll,
            boolean contactServer) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.STATUS);
        notificationHandler.logCommandLine("status "
                + (contactServer ? "-u " : "") + path.toString());

        final String root = path.getAbsolutePath();
        final Collection statuses = new LinkedList();
        try {
            final ISVNWorkspace ws = getRootWorkspace(path);
            String workspacePath = getWorkspacePath(ws, path);
            long revision = ws.status(workspacePath, contactServer,
                    new ISVNStatusHandler() {
                        public void handleStatus(String p, SVNStatus status) {
                            try {
                                Map properties = ws.getProperties(p, false,
                                        true);
                                if (properties == null) {
                                    properties = Collections.EMPTY_MAP;
                                }
                                p = PathUtil.append(root, p);
                                p = p.replace('/', File.separatorChar);
                                statuses.add(new JavaSvnStatus(new File(p),
                                        properties, status));
                            } catch (SVNException e) {
                            }
                        }
                    }, descend, getAll, getAll);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
        return (ISVNStatus[]) statuses.toArray(new ISVNStatus[statuses.size()]);
    }
    
    
}
