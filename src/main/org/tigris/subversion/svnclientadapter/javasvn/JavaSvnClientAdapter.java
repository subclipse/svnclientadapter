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
package org.tigris.subversion.svnclientadapter.javasvn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tigris.subversion.svnclientadapter.AbstractClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNAnnotations;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNBaseDir;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNInfoUnversioned;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.SVNUrlUtils;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnDirEntry;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnInfo;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnLogMessage;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnNotificationHandler;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnPropertyData;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnStatus;
import org.tmatesoft.svn.core.ISVNStatusHandler;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNStatus;
import org.tmatesoft.svn.core.SVNWorkspaceManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.ws.fs.FSEntryFactory;
import org.tmatesoft.svn.core.internal.ws.fs.FSUtil;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.io.SVNDirEntry;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;
import org.tmatesoft.svn.core.io.SVNSimpleCredentialsProvider;
import org.tmatesoft.svn.util.PathUtil;
import org.tmatesoft.svn.util.SVNUtil;

/**
 * 
 * @author Cédric Chabanois (cchabanois at tigris.org)
 *  
 */
public class JavaSvnClientAdapter extends AbstractClientAdapter {
    private String myUserName;

    private String myPassword;

    private JavaSvnNotificationHandler notificationHandler;

    public JavaSvnClientAdapter() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSEntryFactory.setup();

        notificationHandler = new JavaSvnNotificationHandler();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addNotifyListener(org.tigris.subversion.svnclientadapter.ISVNNotifyListener)
     */
    public void addNotifyListener(ISVNNotifyListener listener) {
        notificationHandler.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#removeNotifyListener(org.tigris.subversion.svnclientadapter.ISVNNotifyListener)
     */
    public void removeNotifyListener(ISVNNotifyListener listener) {
        notificationHandler.remove(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setUsername(java.lang.String)
     */
    public void setUsername(String username) {
        this.myUserName = username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setPassword(java.lang.String)
     */
    public void setPassword(String password) {
        this.myPassword = password;
    }

    /**
     * Get the root workspace for given file or directory
     */
    private ISVNWorkspace getRootWorkspace(File file) throws SVNException {
        ISVNWorkspace ws = SVNUtil.createWorkspace(file.getAbsolutePath());
        ws.setCredentials(myUserName, myPassword);
        ws.addWorkspaceListener(notificationHandler);
        ws.setGlobalIgnore("*.o *.lo *.la #*# .*.rej *.rej .*~ *~ .#*");
        return ws;
    }

    private ISVNWorkspace getRootWorkspace(File[] files) throws SVNException {
        ISVNWorkspace ws;
        if (files.length == 1) {
            ws = getRootWorkspace(files[0]);
        } else {
            ws = getRootWorkspace(SVNBaseDir.getRootDir(files));
        }
        return ws;
    }

    /**
     * Get the workspace for the given directory
     * 
     * @param dir
     * @return @throws
     *         SVNException
     */
    private ISVNWorkspace getWorkspace(File dir) throws SVNException {
        ISVNWorkspace ws = SVNWorkspaceManager.createWorkspace("file", dir
                .getAbsolutePath());
        ws.setCredentials(myUserName, myPassword);
        ws.addWorkspaceListener(notificationHandler);
        ws.setGlobalIgnore("*.o *.lo *.la #*# .*.rej *.rej .*~ *~ .#*");
        return ws;
    }

    /**
     * Get the path of given file in the workspace
     * 
     * @param ws
     * @param file
     * @return
     */
    private String getWorkspacePath(ISVNWorkspace ws, File file) {
        return SVNUtil.getWorkspacePath(ws, file.getAbsolutePath());
    }

    /**
     * Get the path of given url in the repository
     * 
     * @param repository
     * @param url
     * @return @throws
     *         SVNException
     */
    private String getRepositoryPath(SVNRepository repository, SVNUrl url)
            throws SVNException {
        SVNRepositoryLocation reposLocation = repository.getLocation();
        SVNRepositoryLocation urlLocation = SVNRepositoryLocation.parseURL(url
                .toString());

        if ((!reposLocation.getHost().equals(urlLocation.getHost()))
                || (reposLocation.getPort() != urlLocation.getPort())
                || (!reposLocation.getProtocol().equals(
                        urlLocation.getProtocol()))) {
            return null;
        }

        String pathRepos = PathUtil.decode(reposLocation.getPath());
        String pathUrl = PathUtil.decode(urlLocation.getPath());

        if (pathUrl.startsWith(pathRepos)) {
            pathUrl = pathUrl.substring(pathRepos.length());
            return pathUrl;
        }
        return null;
    }

    private String getRepositoryRootPath(SVNRepository repository, SVNUrl url)
            throws SVNException {

        SVNRepositoryLocation location = SVNRepositoryLocation.parseURL(url
                .toString());
        String path = location.getPath();
        path = PathUtil.decode(path);
        if (repository.getRepositoryRoot() == null) {
            repository.testConnection();
        }
        String repositoryRoot = repository.getRepositoryRoot();

        if (path.startsWith(repositoryRoot)) {
            path = path.substring(repository.getRepositoryRoot().length());
            return path;
        }
        return null;
    }

    /**
     * Get the repository at the given location
     * 
     * @param reposLocation
     * @return @throws
     *         SVNException
     */
    private SVNRepository getRepository(SVNRepositoryLocation reposLocation)
            throws SVNException {
        SVNRepository repository = SVNRepositoryFactory.create(reposLocation);
        if (myUserName != null && myPassword != null) {
            repository.setCredentialsProvider(new SVNSimpleCredentialsProvider(
                    myUserName, myPassword));
        }
        return repository;
    }

    private SVNRepository getRepository(SVNUrl url) throws SVNException {
        return getRepository(SVNRepositoryLocation.parseURL(url.toString()));
    }

    /**
     * Get the revision number corresponding to given revision for resource at
     * given path in given repository and workspace
     * 
     * @param revision
     * @param resourceRepository
     * @param workspace
     * @param path
     * @return @throws
     *         SVNException
     */
    public static long getRevisionNumber(SVNRevision revision,
            SVNRepository resourceRepository, ISVNWorkspace workspace,
            String path) throws SVNException {
        if (revision == null) {
            return -2;
        }
        int kind = revision.getKind();
        if (kind == SVNRevision.Kind.number) {
            return ((SVNRevision.Number) revision).getNumber();
        } else if (kind == SVNRevision.Kind.head && resourceRepository != null) {
            return resourceRepository.getLatestRevision();
        } else if (kind == SVNRevision.Kind.date && resourceRepository != null) {
            Date date = ((SVNRevision.DateSpec) revision).getDate();
            return resourceRepository.getDatedRevision(date);
        } else if ((kind == SVNRevision.Kind.committed
                || kind == SVNRevision.Kind.working
                || kind == SVNRevision.Kind.previous || kind == SVNRevision.Kind.base)
                && workspace != null && path != null) {
            if (kind == SVNRevision.Kind.base
                    || kind == SVNRevision.Kind.working) {
                String revisionStr = workspace.getPropertyValue(path,
                        SVNProperty.REVISION);
                if (revisionStr != null) {
                    return SVNProperty.longValue(revisionStr);
                }
            } else {
                String revisionStr = workspace.getPropertyValue(path,
                        SVNProperty.COMMITTED_REVISION);
                if (revisionStr != null) {
                    long rev = SVNProperty.longValue(revisionStr);
                    if (kind == SVNRevision.Kind.previous) {
                        rev--;
                    }
                    return rev;
                }
            }
        }
        throw new SVNException("Invalid revision");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addFile(java.io.File)
     */
    public void addFile(File file) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.ADD);
        notificationHandler.logCommandLine("add -N " + file.toString());

        try {
            ISVNWorkspace ws = getRootWorkspace(file.getParentFile());
            ws.add(getWorkspacePath(ws, file), false, false);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addDirectory(java.io.File,
     *      boolean)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#checkout(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision,
     *      boolean)
     */
    public void checkout(SVNUrl moduleName, File destPath,
            SVNRevision revision, boolean recurse) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.CHECKOUT);
        notificationHandler.logCommandLine("checkout" + (recurse ? "" : " -N")
                + " -r " + revision.toString() + " " + moduleName.toString());
        if (!destPath.exists()) {
            destPath.mkdirs();
        }
        try {
            ISVNWorkspace ws = getWorkspace(destPath);
            SVNRepositoryLocation location = SVNRepositoryLocation
                    .parseURL(moduleName.toString());
            SVNRepository repository = getRepository(location);
            long rev = getRevisionNumber(revision, repository, null, null);
            ws.checkout(SVNRepositoryLocation.parseURL(moduleName.toString()),
                    rev, false, recurse);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#commit(java.io.File[],
     *      java.lang.String, boolean)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File,
     *      java.io.File)
     */
    public void copy(File srcPath, File destPath) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
            notificationHandler.logCommandLine("copy " + srcPath + " "
                    + destPath);

            ISVNWorkspace ws = getRootWorkspace(srcPath);
            ws.copy(getWorkspacePath(ws, srcPath), getWorkspacePath(ws,
                    destPath), false);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String)
     */
    public void copy(File srcPath, SVNUrl destUrl, String message)
            throws SVNClientException {
        notImplementedYet("copy");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
            throws SVNClientException {
        notImplementedYet("copy");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public void copy(SVNUrl srcUrl, SVNUrl destUrl, String message,
            SVNRevision revision) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
        notificationHandler.logCommandLine("copy " + srcUrl + " " + destUrl);

        ISVNEditor editor = null;
        try {
            SVNRepository repository = getRepository(destUrl.getParent());
            long revNumber = getRevisionNumber(revision, repository, null, null);

            String srcPath = getRepositoryRootPath(repository, srcUrl);

            editor = repository.getCommitEditor(message, null);
            editor.openRoot(-1);
            editor.addDir(destUrl.getLastPathSegment(), srcPath, revNumber);
            editor.closeDir();
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

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doExport(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision,
     *      boolean)
     */
    public void doExport(SVNUrl srcUrl, File destPath, SVNRevision revision,
            boolean force) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.EXPORT);
            notificationHandler.logCommandLine("export -r "
                    + revision.toString() + ' ' + srcUrl.toString() + ' '
                    + destPath.toString());

            if (force) {
                FSUtil.deleteAll(destPath);
            }
            destPath.mkdirs();

            ISVNWorkspace ws = getWorkspace(destPath);
            SVNRepositoryLocation location = SVNRepositoryLocation
                    .parseURL(srcUrl.toString());
            SVNRepository repository = getRepository(location);

            long revNumber = getRevisionNumber(revision, repository, null, null);
            ws.checkout(location, revNumber, true);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doExport(java.io.File,
     *      java.io.File, boolean)
     */
    public void doExport(File srcPath, File destPath, boolean force)
            throws SVNClientException {
        notImplementedYet("doExport");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doImport(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String,
     *      boolean)
     */
    public void doImport(File path, SVNUrl url, String message, boolean recurse)
            throws SVNClientException {
        if (!recurse) {
            throw new SVNClientException(
                    "non-recursive import is not supported");
        }
        notificationHandler.setCommand(ISVNNotifyListener.Command.IMPORT);
        String dest = url.toString();
        notificationHandler.logCommandLine("import -m \"" + message + "\" "
                + (recurse ? "" : "-N ") + path.toString() + ' ' + dest);
        try {
            ISVNWorkspace ws = getRootWorkspace(path);
            ws.commit(SVNRepositoryLocation.parseURL(url.toString()), message);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }

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

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#update(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public long update(File path, SVNRevision revision, boolean recurse)
            throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.UPDATE);
            notificationHandler.logCommandLine("update -r "
                    + revision.toString() + ' ' + (recurse ? "" : "-N ")
                    + path.toString());

            ISVNWorkspace ws = getRootWorkspace(path);
            String wsPath = getWorkspacePath(ws, path);
            SVNRepository repository = getRepository(ws.getLocation());
            long revNumber = getRevisionNumber(revision, repository, ws, wsPath);

            return ws.update(wsPath, revNumber, recurse);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#revert(java.io.File,
     *      boolean)
     */
    public void revert(File path, boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.REVERT);
            notificationHandler.logCommandLine("revert "
                    + (recurse ? "" : "-N ") + path);

            ISVNWorkspace ws = getRootWorkspace(path);
            ws.revert(getWorkspacePath(ws, path), recurse);

        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getLogMessages(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNLogMessage[] getLogMessages(SVNUrl url,
            SVNRevision revisionStart, SVNRevision revisionEnd)
            throws SVNClientException {
        notImplementedYet("getLogMessages");
        return null;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertySet(java.io.File,
     *      java.lang.String, java.lang.String, boolean)
     */
    public void propertySet(File path, String propertyName,
            String propertyValue, boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.PROPSET);

            notificationHandler.logCommandLine("propset "
                    + (recurse ? "-R " : "") + propertyName + " \""
                    + propertyValue + "\" " + path.toString());

            ISVNWorkspace ws = getRootWorkspace(path);
            ws.setPropertyValue(getWorkspacePath(ws, path), propertyName,
                    propertyValue, recurse);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertySet(java.io.File,
     *      java.lang.String, java.io.File, boolean)
     */
    public void propertySet(File path, String propertyName, File propertyFile,
            boolean recurse) throws SVNClientException, IOException {
        notImplementedYet("propertySet");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyGet(java.io.File,
     *      java.lang.String)
     */
    public ISVNProperty propertyGet(File path, String propertyName)
            throws SVNClientException {
        notImplementedYet("propertyGet");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyDel(java.io.File,
     *      java.lang.String, boolean)
     */
    public void propertyDel(File path, String propertyName, boolean recurse)
            throws SVNClientException {
        notImplementedYet("propertyDel");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File,
     *      boolean)
     */
    public void diff(File oldPath, SVNRevision oldPathRevision, File newPath,
            SVNRevision newPathRevision, File outFile, boolean recurse)
            throws SVNClientException {
        notImplementedYet("diff");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(java.io.File,
     *      java.io.File, boolean)
     */
    public void diff(File path, File outFile, boolean recurse)
            throws SVNClientException {
        notImplementedYet("diff");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File,
     *      boolean)
     */
    public void diff(SVNUrl oldUrl, SVNRevision oldUrlRevision, SVNUrl newUrl,
            SVNRevision newUrlRevision, File outFile, boolean recurse)
            throws SVNClientException {
        notImplementedYet("diff");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File,
     *      boolean)
     */
    public void diff(SVNUrl url, SVNRevision oldUrlRevision,
            SVNRevision newUrlRevision, File outFile, boolean recurse)
            throws SVNClientException {
        notImplementedYet("diff");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#annotate(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNAnnotations annotate(SVNUrl url, SVNRevision revisionStart,
            SVNRevision revisionEnd) throws SVNClientException {
        notImplementedYet("annotate");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#annotate(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNAnnotations annotate(File file, SVNRevision revisionStart,
            SVNRevision revisionEnd) throws SVNClientException {
        notImplementedYet("annotate");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getProperties(java.io.File)
     */
    public ISVNProperty[] getProperties(File path) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.PROPLIST);
            notificationHandler.logCommandLine("proplist " + path);

            Map properties = null;
            ISVNWorkspace ws = getRootWorkspace(path);
            properties = ws.getProperties(getWorkspacePath(ws, path), true,
                    false);

            if (properties == null) {
                return new ISVNProperty[0];
            }
            Collection result = new LinkedList();
            for (Iterator names = properties.keySet().iterator(); names
                    .hasNext();) {
                String name = (String) names.next();
                String value = (String) properties.get(name);
                result.add(new JavaSvnPropertyData(path, name, value,
                        (value != null) ? value.getBytes() : null));
            }
            return (ISVNProperty[]) result.toArray(new ISVNProperty[result
                    .size()]);

        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#resolved(java.io.File)
     */
    public void resolved(File path) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.RESOLVED);
            notificationHandler.logCommandLine("resolved " + path.toString());

            ISVNWorkspace ws = getRootWorkspace(path);
            ws.markResolved(getWorkspacePath(ws, path), false);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#createRepository(java.io.File,
     *      java.lang.String)
     */
    public void createRepository(File path, String repositoryType)
            throws SVNClientException {
        notImplementedYet("createRepository");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#cancelOperation()
     */
    public void cancelOperation() throws SVNClientException {
        notImplementedYet("cancelOperation");
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

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getRepositoryRoot(org.tigris.subversion.svnclientadapter.SVNUrl)
     */
    public SVNUrl getRepositoryRoot(SVNUrl url) throws SVNClientException {
        notImplementedYet("getRepositoryRoot");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#switchToUrl(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public void switchToUrl(File path, SVNUrl url, SVNRevision revision,
            boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.SWITCH);

            String commandLine = "switch " + url + " " + path + " " + "-r"
                    + revision.toString();
            if (!recurse) {
                commandLine += " -N";
            }
            notificationHandler.logCommandLine(commandLine);

            ISVNWorkspace ws = getRootWorkspace(path);
            String relativePath = getWorkspacePath(ws, path);
            long revNumber = getRevisionNumber(revision, getRepository(ws
                    .getLocation()), ws, relativePath);
            ws.update(SVNRepositoryLocation.parseURL(url.toString()),
                    relativePath, revNumber, recurse);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setConfigDirectory(java.io.File)
     */
    public void setConfigDirectory(File dir) throws SVNClientException {
        notImplementedYet("setConfigDirectory");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#cleanup(java.io.File)
     */
    public void cleanup(File dir) throws SVNClientException {
        notImplementedYet("cleanup");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#merge(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File,
     *      boolean, boolean)
     */
    public void merge(SVNUrl path1, SVNRevision revision1, SVNUrl path2,
            SVNRevision revision2, File localPath, boolean force,
            boolean recurse) throws SVNClientException {
        notImplementedYet("merge");
    }

    private void notImplementedYet(String command) throws SVNClientException {
        throw new SVNClientException("Not implemented yet : " + command);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#merge(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision,
     *      org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File,
     *      boolean, boolean, boolean)
     */
    public void merge(SVNUrl path1, SVNRevision revision1, SVNUrl path2,
            SVNRevision revision2, File localPath, boolean force,
            boolean recurse, boolean dryRun) throws SVNClientException {
        notImplementedYet("merge");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addPasswordCallback(org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword)
     */
    public void addPasswordCallback(ISVNPromptUserPassword callback) {
        // Default is to do nothing. If JavaSVN has a way to do callbacks
        // for authentication, it could be added here.
    }
}