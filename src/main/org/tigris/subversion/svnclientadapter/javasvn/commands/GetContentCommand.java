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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

public class GetContentCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public GetContentCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getContent(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public InputStream getContent(SVNUrl url, SVNRevision revision)
            throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.CAT);
            notificationHandler.logCommandLine("cat -r " + revision.toString()
                    + " " + url.toString());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SVNRepository repository = getRepository(url.getParent());
            long rev = getRevisionNumber(revision, repository, null, url
                    .getLastPathSegment());
            repository.getFile(url.getLastPathSegment(), rev, null, bos);
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getContent(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public InputStream getContent(File path, SVNRevision revision)
            throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.CAT);
        notificationHandler.logCommandLine("cat -r " + revision.toString()
                + " " + path.toString());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ISVNWorkspace ws = getRootWorkspace(path);
            String workspacePath = getWorkspacePath(ws, path);
            if (SVNRevision.BASE.equals(revision)) {
                ws.getFileContent(workspacePath).getBaseFileContent(bos);
                return new ByteArrayInputStream(bos.toByteArray());
            } else if (SVNRevision.WORKING.equals(revision)) {
                ws.getFileContent(workspacePath).getWorkingCopyContent(bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
            SVNRepository repository = getRepository(ws.getLocation(null));
            long rev = getRevisionNumber(revision, repository, ws,
                    workspacePath);
            repository.getFile(workspacePath, rev, null, bos);
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
    
}
