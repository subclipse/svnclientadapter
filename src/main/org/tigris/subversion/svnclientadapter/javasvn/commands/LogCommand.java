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
import java.util.LinkedList;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnLogMessage;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

public class LogCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public LogCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getLogMessages(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNLogMessage[] getLogMessages(SVNUrl url,
            SVNRevision revisionStart, SVNRevision revisionEnd, boolean fetchChangePath)
            throws SVNClientException {
        try {
            // TODO: fetchChangePath is still to be implemented            
            notificationHandler.setCommand(ISVNNotifyListener.Command.LOG);
            notificationHandler.logCommandLine("log -r "
                    + revisionStart.toString() + ":" + revisionEnd.toString()
                    + " " + url.toString());

            // this seems to work even if url represents a file 
            SVNRepository repository = getRepository(url);
            long revStart = getRevisionNumber(revisionStart, repository, null,
                    null);
            long revEnd = getRevisionNumber(revisionEnd, repository, null, null);

            final LinkedList logMessages = new LinkedList();
            final ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
                public void handleLogEntry(SVNLogEntry logEntry) {
                    logMessages.add(new JavaSvnLogMessage(logEntry));
                }
            };

            repository.log(new String[] { "" }, revStart,
                    revEnd, true, false, handler);
            return (ISVNLogMessage[]) logMessages
                    .toArray(new ISVNLogMessage[logMessages.size()]);

        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getLogMessages(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNLogMessage[] getLogMessages(File path,
            SVNRevision revisionStart, SVNRevision revisionEnd)
            throws SVNClientException {

        try {
            final LinkedList logMessages = new LinkedList();
            final ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
                public void handleLogEntry(SVNLogEntry logEntry) {
                    logMessages.add(new JavaSvnLogMessage(logEntry));
                }
            };

            notificationHandler.setCommand(ISVNNotifyListener.Command.LOG);
            notificationHandler.logCommandLine("log -r "
                    + revisionStart.toString() + ":" + revisionEnd.toString()
                    + " " + path.toString());
            ISVNWorkspace workspace = getRootWorkspace(path);
            String wsPath = getWorkspacePath(workspace, path);
            SVNRepository repository = getRepository(workspace.getLocation());

            long revStart = getRevisionNumber(revisionStart, repository,
                    workspace, wsPath);
            long revEnd = getRevisionNumber(revisionEnd, repository, workspace,
                    wsPath);

            workspace.log(wsPath, revStart, revEnd, false, true, handler);
            return (ISVNLogMessage[]) logMessages
                    .toArray(new ISVNLogMessage[0]);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
    
    
}
