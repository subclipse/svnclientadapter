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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnDirEntry;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNDirEntry;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

public class ListCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public ListCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getList(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public ISVNDirEntry[] getList(SVNUrl url, SVNRevision revision,
            boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.LS);
            String commandLine = "list -r " + revision.toString()
                    + (recurse ? "-R" : "") + " " + url.toString();
            notificationHandler.logCommandLine(commandLine);

            SVNRepository repository = getRepository(url);
            long revNumber = getRevisionNumber(revision, repository, null, "");
            List entries = new ArrayList();
            getList(repository, "", "", revNumber, recurse, entries);
            return (ISVNDirEntry[]) entries.toArray(new ISVNDirEntry[0]);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        } catch (MalformedURLException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    private void getList(SVNRepository repository, String path,
            String parentPath, long revision, boolean recurse, List dirEntries)
            throws MalformedURLException, SVNException {

        Collection entries = repository.getDir(path, revision, null,
                (Collection) null);
        for (Iterator svnEntries = entries.iterator(); svnEntries.hasNext();) {
            SVNDirEntry svnEntry = (SVNDirEntry) svnEntries.next();
            dirEntries.add(new JavaSvnDirEntry(parentPath, svnEntry));

            if (recurse && svnEntry.getKind() == SVNNodeKind.DIR) {
                String newParenPath = parentPath;
                if (!"".equals(newParenPath)) {
                    newParenPath += "/";
                }
                newParenPath += svnEntry.getName();
                getList(repository, path + "/" + svnEntry.getName(),
                        newParenPath, revision, recurse, dirEntries);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getList(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public ISVNDirEntry[] getList(File path, SVNRevision revision,
            boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.LS);
            String commandLine = "list -r " + revision.toString()
                    + (recurse ? "-R" : "") + " " + path.toString();
            notificationHandler.logCommandLine(commandLine);

            Collection allEntries = new LinkedList();

            ISVNWorkspace ws = getRootWorkspace(path);
            String wsPath = getWorkspacePath(ws, path);
            SVNRepository repository = getRepository(ws.getLocation());
            long revNumber = getRevisionNumber(revision, repository, ws, wsPath);

            List entries = new ArrayList();
            getList(repository, wsPath, "", revNumber, recurse, entries);
            return (ISVNDirEntry[]) entries.toArray(new ISVNDirEntry[0]);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        } catch (MalformedURLException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getDirEntry(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNDirEntry getDirEntry(SVNUrl url, SVNRevision revision)
            throws SVNClientException {
        // list give the DirEntrys of the elements of a directory or the
        // DirEntry of a file
        ISVNDirEntry[] entries = getList(url.getParent(), revision, false);
        String expectedPath = url.getLastPathSegment();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getPath().equals(expectedPath)) {
                return entries[i];
            }
        }
        return null; // not found
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getDirEntry(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNDirEntry getDirEntry(File path, SVNRevision revision)
            throws SVNClientException {
        // list give the DirEntrys of the elements of a directory or the
        // DirEntry
        // of a file
        ISVNDirEntry[] entries = getList(path.getParentFile(), revision, false);
        String expectedPath = path.getName();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getPath().equals(expectedPath)) {
                return entries[i];
            }
        }
        return null; // not found
    }
    
    
}
