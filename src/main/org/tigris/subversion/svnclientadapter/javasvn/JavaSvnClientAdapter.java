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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.CommitItem;
import org.tigris.subversion.javahl.CommitItemStateFlags;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.svnclientadapter.ISVNAnnotations;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNBaseDir;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNKeywords;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javahl.JhlConverter;
import org.tmatesoft.svn.core.ISVNCommitHandler;
import org.tmatesoft.svn.core.ISVNStatusHandler;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNStatus;
import org.tmatesoft.svn.core.SVNWorkspaceManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.ws.fs.FSEntryFactory;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;
import org.tmatesoft.svn.core.io.SVNSimpleCredentialsProvider;
import org.tmatesoft.svn.util.DebugLog;
import org.tmatesoft.svn.util.PathUtil;

/**
 * 
 * @author Cédric Chabanois (cchabanois at tigris.org)
 *  
 */
public class JavaSvnClientAdapter implements ISVNClientAdapter {
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
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#removeNotifyListener(org.tigris.subversion.svnclientadapter.ISVNNotifyListener)
     */
    public void removeNotifyListener(ISVNNotifyListener listener) {

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

    private ISVNWorkspace getWorkspace(File file) throws SVNException {
        ISVNWorkspace ws = SVNWorkspaceManager.createWorkspace("file", file
                .getAbsolutePath());
        ws.setCredentials(myUserName, myPassword);
        ws.addWorkspaceListener(notificationHandler);
        ws.setGlobalIgnore("*.o *.lo *.la #*# .*.rej *.rej .*~ *~ .#*");
        return ws;
    }

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

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addFile(java.io.File)
     */
    public void addFile(File file) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.ADD);
        notificationHandler.logCommandLine("add -N " + file.toString());

        String target = file.getName();
        try {
            ISVNWorkspace ws = getWorkspace(file.getParentFile());
            ws.add(target, false, false);
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
        String target = dir.getName();
        try {
            ISVNWorkspace ws = getWorkspace(dir.getParentFile());
            ws.add(target, false, recurse);
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
        long rev = JavaSvnConverter.convertRevision(revision);

        try {
            ISVNWorkspace ws = getWorkspace(destPath);
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

            if (paths.length > 1) {
                File rootPath = SVNBaseDir.getRootDir(paths);
                String[] relativePaths = new String[paths.length];
                for (int i = 0; i < relativePaths.length;i++) {
                    relativePaths[i] = SVNBaseDir.getRelativePath(rootPath, paths[i]);
                }
    
                ISVNWorkspace ws = getWorkspace(rootPath);
    
                return ws.commit(relativePaths, message, recurse);
            } else {
                File workspaceDir;
                String target;
                if (paths[0].isFile()) {
                    workspaceDir = paths[0].getParentFile();
                    target = paths[0].getName();
                } else {
                    workspaceDir = paths[0];
                    target = "";
                }
                ISVNWorkspace ws = getWorkspace(workspaceDir);
                return ws.commit(target, message, recurse);
            }

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
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getList(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public ISVNDirEntry[] getList(File path, SVNRevision revision,
            boolean recurse) throws SVNClientException {
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getDirEntry(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNDirEntry getDirEntry(SVNUrl url, SVNRevision revision)
            throws SVNClientException {
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getDirEntry(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNDirEntry getDirEntry(File path, SVNRevision revision)
            throws SVNClientException {
        notImplementedYet();
        return null;
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
                final ISVNWorkspace ws = getWorkspace(file.getParentFile());
                SVNStatus status = ws.status(file.getName(), false);
                statuses[i] = new JavaSvnStatus(file, ws.getProperties(file
                        .getName(), false, true), status);
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

        String target = "";
        if (!path.isDirectory()) {
            path = path.getParentFile();
            target = path.getName();
        }
        final String root = path.getAbsolutePath();
        final Collection statuses = new LinkedList();
        try {
            final ISVNWorkspace ws = getWorkspace(path);
            long revision = ws.status(target, contactServer,
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String)
     */
    public void copy(File srcPath, SVNUrl destUrl, String message)
            throws SVNClientException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
            throws SVNClientException {
        notImplementedYet();
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#remove(org.tigris.subversion.svnclientadapter.SVNUrl[],
     *      java.lang.String)
     */
    public void remove(SVNUrl[] url, String message) throws SVNClientException {
        notImplementedYet();
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
                String target = file[i].getName();
                ISVNWorkspace ws = getWorkspace(file[i].getParentFile());
                ws.delete(target);
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doExport(java.io.File,
     *      java.io.File, boolean)
     */
    public void doExport(File srcPath, File destPath, boolean force)
            throws SVNClientException {
        notImplementedYet();
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
            ISVNWorkspace ws = getWorkspace(path);
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#mkdir(java.io.File)
     */
    public void mkdir(File file) throws SVNClientException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#move(java.io.File,
     *      java.io.File, boolean)
     */
    public void move(File srcPath, File destPath, boolean force)
            throws SVNClientException {
        notImplementedYet();
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#update(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public long update(File path, SVNRevision revision, boolean recurse)
            throws SVNClientException {
        notImplementedYet();
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#revert(java.io.File,
     *      boolean)
     */
    public void revert(File path, boolean recurse) throws SVNClientException {
        notImplementedYet();
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
        notImplementedYet();
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
        notImplementedYet();
        return null;
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
            repository.getFile(url.getLastSegment(), JavaSvnConverter
                    .convertRevision(revision), null, bos);
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
        String name = path.getName();
        try {
            ISVNWorkspace ws = getWorkspace(path.getParentFile());
            if (SVNRevision.BASE.equals(revision)) {
                ws.getFileContent(name).getBaseFileContent(bos);
                return new ByteArrayInputStream(bos.toByteArray());
            } else if (SVNRevision.WORKING.equals(revision)) {
                ws.getFileContent(name).getWorkingCopyContent(bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
            SVNRepository repository = getRepository(ws.getLocation(null));
            repository.getFile(name,
                    JavaSvnConverter.convertRevision(revision), null, bos);
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertySet(java.io.File,
     *      java.lang.String, java.io.File, boolean)
     */
    public void propertySet(File path, String propertyName, File propertyFile,
            boolean recurse) throws SVNClientException, IOException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyGet(java.io.File,
     *      java.lang.String)
     */
    public ISVNProperty propertyGet(File path, String propertyName)
            throws SVNClientException {
        notImplementedYet();
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getIgnoredPatterns(java.io.File)
     */
    public List getIgnoredPatterns(File path) throws SVNClientException {
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addToIgnoredPatterns(java.io.File,
     *      java.lang.String)
     */
    public void addToIgnoredPatterns(File path, String pattern)
            throws SVNClientException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setIgnoredPatterns(java.io.File,
     *      java.util.List)
     */
    public void setIgnoredPatterns(File path, List patterns)
            throws SVNClientException {
        notImplementedYet();
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(java.io.File,
     *      java.io.File, boolean)
     */
    public void diff(File path, File outFile, boolean recurse)
            throws SVNClientException {
        notImplementedYet();
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
        notImplementedYet();
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getKeywords(java.io.File)
     */
    public SVNKeywords getKeywords(File path) throws SVNClientException {
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setKeywords(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNKeywords, boolean)
     */
    public void setKeywords(File path, SVNKeywords keywords, boolean recurse)
            throws SVNClientException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addKeywords(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNKeywords)
     */
    public SVNKeywords addKeywords(File path, SVNKeywords keywords)
            throws SVNClientException {
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#removeKeywords(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNKeywords)
     */
    public SVNKeywords removeKeywords(File path, SVNKeywords keywords)
            throws SVNClientException {
        notImplementedYet();
        return null;
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
        notImplementedYet();
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
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getProperties(java.io.File)
     */
    public ISVNProperty[] getProperties(File path) throws SVNClientException {
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#resolved(java.io.File)
     */
    public void resolved(File path) throws SVNClientException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#createRepository(java.io.File,
     *      java.lang.String)
     */
    public void createRepository(File path, String repositoryType)
            throws SVNClientException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#cancelOperation()
     */
    public void cancelOperation() throws SVNClientException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getInfo(java.io.File)
     */
    public ISVNInfo getInfo(File file) throws SVNClientException {
        notImplementedYet();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getRepositoryRoot(org.tigris.subversion.svnclientadapter.SVNUrl)
     */
    public SVNUrl getRepositoryRoot(SVNUrl url) throws SVNClientException {
        notImplementedYet();
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
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setConfigDirectory(java.io.File)
     */
    public void setConfigDirectory(File dir) throws SVNClientException {
        notImplementedYet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#cleanup(java.io.File)
     */
    public void cleanup(File dir) throws SVNClientException {
        notImplementedYet();
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
        notImplementedYet();
    }

    private void notImplementedYet() throws SVNClientException {
        throw new SVNClientException("Not implemented yet");
    }

}