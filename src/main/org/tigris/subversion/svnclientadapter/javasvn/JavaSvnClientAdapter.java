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
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.PropertyData;
import org.tigris.subversion.javahl.Revision;
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
import org.tigris.subversion.svnclientadapter.javahl.JhlConverter;
import org.tigris.subversion.svnclientadapter.javahl.JhlPropertyData;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnDirEntry;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnInfo;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnLogMessage;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnNotificationHandler;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnPropertyData;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnStatus;
import org.tigris.subversion.svnclientadapter.javasvn.commands.AddCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.CheckoutCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.CommitCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.CopyCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.ExportCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.GetContentCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.ImportCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.InfoCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.ListCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.LogCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.MkdirCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.MoveCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.PropertyDelCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.PropertyGetCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.PropertySetCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.RemoveCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.ResolveCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.RevertCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.StatusCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.SwitchCommand;
import org.tigris.subversion.svnclientadapter.javasvn.commands.UpdateCommand;
import org.tmatesoft.svn.core.ISVNStatusHandler;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNStatus;
import org.tmatesoft.svn.core.SVNWorkspaceManager;
import org.tmatesoft.svn.core.diff.ISVNDiffGenerator;
import org.tmatesoft.svn.core.diff.ISVNDiffGeneratorFactory;
import org.tmatesoft.svn.core.diff.SVNDiffManager;
import org.tmatesoft.svn.core.diff.SVNUniDiffGenerator;
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
import org.tmatesoft.svn.util.DebugLog;
import org.tmatesoft.svn.util.PathUtil;
import org.tmatesoft.svn.util.SVNUtil;

/**
 * 
 * @author Cédric Chabanois (cchabanois at tigris.org)
 *  
 */
public class JavaSvnClientAdapter extends AbstractClientAdapter {
    private JavaSvnConfig javaSvnConfig;

    public JavaSvnClientAdapter() {
        JavaSvnUtils.setupJavaSvn();

        javaSvnConfig = new JavaSvnConfig();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addNotifyListener(org.tigris.subversion.svnclientadapter.ISVNNotifyListener)
     */
    public void addNotifyListener(ISVNNotifyListener listener) {
        javaSvnConfig.addNotifyListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#removeNotifyListener(org.tigris.subversion.svnclientadapter.ISVNNotifyListener)
     */
    public void removeNotifyListener(ISVNNotifyListener listener) {
        javaSvnConfig.removeNotifyListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setUsername(java.lang.String)
     */
    public void setUsername(String username) {
        javaSvnConfig.setUsername(username);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setPassword(java.lang.String)
     */
    public void setPassword(String password) {
        javaSvnConfig.setPassword(password);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addFile(java.io.File)
     */
    public void addFile(File file) throws SVNClientException {
        new AddCommand(javaSvnConfig).addFile(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addDirectory(java.io.File,
     *      boolean)
     */
    public void addDirectory(File dir, boolean recurse)
            throws SVNClientException {
        new AddCommand(javaSvnConfig).addDirectory(dir, recurse);
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
        new CheckoutCommand(javaSvnConfig).checkout(moduleName, destPath, revision, recurse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#commit(java.io.File[],
     *      java.lang.String, boolean)
     */
    public long commit(File[] paths, String message, boolean recurse)
            throws SVNClientException {
        return new CommitCommand(javaSvnConfig).commit(paths, message, recurse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getList(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public ISVNDirEntry[] getList(SVNUrl url, SVNRevision revision,
            boolean recurse) throws SVNClientException {
        return new ListCommand(javaSvnConfig).getList(url, revision, recurse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getList(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public ISVNDirEntry[] getList(File path, SVNRevision revision,
            boolean recurse) throws SVNClientException {
        return new ListCommand(javaSvnConfig).getList(path, revision, recurse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getDirEntry(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNDirEntry getDirEntry(SVNUrl url, SVNRevision revision)
            throws SVNClientException {
        return new ListCommand(javaSvnConfig).getDirEntry(url, revision);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getDirEntry(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public ISVNDirEntry getDirEntry(File path, SVNRevision revision)
            throws SVNClientException {
        return new ListCommand(javaSvnConfig).getDirEntry(path, revision);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getSingleStatus(java.io.File)
     */
    public ISVNStatus getSingleStatus(File path) throws SVNClientException {
        return new StatusCommand(javaSvnConfig).getSingleStatus(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getStatus(java.io.File[])
     */
    public ISVNStatus[] getStatus(File[] path) throws SVNClientException {
        return new StatusCommand(javaSvnConfig).getStatus(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getStatus(java.io.File,
     *      boolean, boolean)
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll)
            throws SVNClientException {
        return new StatusCommand(javaSvnConfig).getStatus(path, descend, getAll);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getStatus(java.io.File,
     *      boolean, boolean, boolean)
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll,
            boolean contactServer) throws SVNClientException {
        return new StatusCommand(javaSvnConfig).getStatus(path, descend, getAll, contactServer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File,
     *      java.io.File)
     */
    public void copy(File srcPath, File destPath) throws SVNClientException {
        new CopyCommand(javaSvnConfig).copy(srcPath, destPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String)
     */
    public void copy(File srcPath, SVNUrl destUrl, String message)
            throws SVNClientException {
        new CopyCommand(javaSvnConfig).copy(srcPath, destUrl, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
            throws SVNClientException {
        new CopyCommand(javaSvnConfig).copy(srcUrl, destPath, revision);
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
        new CopyCommand(javaSvnConfig).copy(srcUrl, destUrl, message, revision);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#remove(org.tigris.subversion.svnclientadapter.SVNUrl[],
     *      java.lang.String)
     */
    public void remove(SVNUrl[] url, String message) throws SVNClientException {
        new RemoveCommand(javaSvnConfig).remove(url, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#remove(java.io.File[],
     *      boolean)
     */
    public void remove(File[] file, boolean force) throws SVNClientException {
        new RemoveCommand(javaSvnConfig).remove(file, force);
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
        new ExportCommand(javaSvnConfig).doExport(srcUrl, destPath, revision, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doExport(java.io.File,
     *      java.io.File, boolean)
     */
    public void doExport(File srcPath, File destPath, boolean force)
            throws SVNClientException {
        new ExportCommand(javaSvnConfig).doExport(srcPath, destPath, force);
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
        new ImportCommand(javaSvnConfig).doImport(path, url, message, recurse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#mkdir(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      java.lang.String)
     */
    public void mkdir(SVNUrl url, String message) throws SVNClientException {
        new MkdirCommand(javaSvnConfig).mkdir(url, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#mkdir(java.io.File)
     */
    public void mkdir(File file) throws SVNClientException {
        new MkdirCommand(javaSvnConfig).mkdir(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#move(java.io.File,
     *      java.io.File, boolean)
     */
    public void move(File srcPath, File destPath, boolean force)
            throws SVNClientException {
        new MoveCommand(javaSvnConfig).move(srcPath, destPath, force);
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
        new MoveCommand(javaSvnConfig).move(srcUrl, destUrl, message, revision);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#update(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public long update(File path, SVNRevision revision, boolean recurse)
            throws SVNClientException {
        return new UpdateCommand(javaSvnConfig).update(path, revision, recurse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#revert(java.io.File,
     *      boolean)
     */
    public void revert(File path, boolean recurse) throws SVNClientException {
        new RevertCommand(javaSvnConfig).revert(path, recurse);
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
        return new LogCommand(javaSvnConfig).getLogMessages(url, revisionStart, revisionEnd, fetchChangePath);
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
        return new LogCommand(javaSvnConfig).getLogMessages(path, revisionStart, revisionEnd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getContent(org.tigris.subversion.svnclientadapter.SVNUrl,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public InputStream getContent(SVNUrl url, SVNRevision revision)
            throws SVNClientException {
        return new GetContentCommand(javaSvnConfig).getContent(url, revision);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getContent(java.io.File,
     *      org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public InputStream getContent(File path, SVNRevision revision)
            throws SVNClientException {
        return new GetContentCommand(javaSvnConfig).getContent(path, revision);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertySet(java.io.File,
     *      java.lang.String, java.lang.String, boolean)
     */
    public void propertySet(File path, String propertyName,
            String propertyValue, boolean recurse) throws SVNClientException {
        new PropertySetCommand(javaSvnConfig).propertySet(path, propertyName, propertyValue, recurse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertySet(java.io.File,
     *      java.lang.String, java.io.File, boolean)
     */
    public void propertySet(File path, String propertyName, File propertyFile,
            boolean recurse) throws SVNClientException, IOException {
        new PropertySetCommand(javaSvnConfig).propertySet(path, propertyName, propertyFile, recurse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyGet(java.io.File,
     *      java.lang.String)
     */
    public ISVNProperty propertyGet(File path, String propertyName)
            throws SVNClientException {
        return new PropertyGetCommand(javaSvnConfig).propertyGet(path, propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyDel(java.io.File,
     *      java.lang.String, boolean)
     */
    public void propertyDel(File path, String propertyName, boolean recurse)
            throws SVNClientException {
        new PropertyDelCommand(javaSvnConfig).propertyDel(path, propertyName, recurse);
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
    class DiffHandler implements ISVNStatusHandler {
        
        private Revision myRevision1; 
        private Revision myRevision2;
        private Writer myWriter;
        private ISVNWorkspace myWorkspace;
        
        public DiffHandler(ISVNWorkspace ws,
                           Revision revision1,
                           Revision revision2,
                           Writer outWriter) {
            myRevision1 = revision1;
            myRevision2 = revision2;
            myWriter = outWriter;
            myWorkspace = ws;
        }
        
        public void handleStatus(String path, SVNStatus status) {
            try {
                if (status.getContentsStatus() != SVNStatus.UNVERSIONED) {
                    String absPath = SVNUtil.getAbsolutePath(myWorkspace,
                                                             status.getPath());
                    if (status.isDirectory()) {
                        return;
                    }
                    diff (path, absPath, myRevision1,myRevision2, myWriter);
                }
            }
            catch (ClientException ce) {
                DebugLog.error(ce);
            }
        }
    }    

    
    private void diff(String wsPath, String path, Revision revision1, Revision revision2, Writer outWriter) throws ClientException {
        byte byteArray1[] = fileContent(path, revision1, SVNProperty.EOL_STYLE_LF, true);
        byte byteArray2[] = fileContent(path, revision2, SVNProperty.EOL_STYLE_LF, true);

        ByteArrayInputStream is1 = new ByteArrayInputStream(byteArray1);
        ByteArrayInputStream is2 = new ByteArrayInputStream(byteArray2);

        Map properties = new HashMap();
        properties.put(ISVNDiffGeneratorFactory.COMPARE_EOL_PROPERTY, Boolean.TRUE.toString());
        properties.put(ISVNDiffGeneratorFactory.WHITESPACE_PROPERTY, Boolean.FALSE.toString());
        properties.put(ISVNDiffGeneratorFactory.EOL_PROPERTY, System.getProperty("line.separator"));
        
        String encoding = System.getProperty("file.encoding", "US-ASCII");

        try {
            ISVNWorkspace ws = createWorkspace(path);
            ISVNWorkspace root = ws.getRootWorkspace(true,true);
            String targetPath = SVNUtil.getWorkspacePath(root, path);
            String osTargetPath = targetPath;

            if (FSUtil.isWindows) {
                osTargetPath = targetPath.replace('/', File.separatorChar);
            }
            ISVNDiffGenerator diff = SVNDiffManager.getDiffGenerator(SVNUniDiffGenerator.TYPE, properties);
            if (diff == null) {
                throwException(new SVNException("no suitable diff generator found"));
                return;
            }
            outWriter.write("Index: " + wsPath);
            outWriter.write(System.getProperty("line.separator", "\n"));
            outWriter.write("===================================================================");
            outWriter.write(System.getProperty("line.separator", "\n"));
            String rev1Str = revision1.toString();
            if (revision1 == Revision.WORKING) {
                rev1Str = "working copy";
            } else {
                rev1Str = "revision " + getRevisionNumber(revision1, null, root, targetPath);
            }
            String rev2Str = revision1.toString();
            if (revision2 == Revision.WORKING) {
                rev2Str = "working copy";
            } else {
                rev2Str = "revision " + getRevisionNumber(revision2, null, root, targetPath);
            }
            diff.generateDiffHeader(osTargetPath,
                                    "(" + rev1Str + ")",
                                    "(" + rev2Str + ")",
                                    outWriter);
            String mimeType = ws.getPropertyValue(targetPath,
                                                  SVNProperty.MIME_TYPE);
            if (mimeType != null && !mimeType.startsWith("text")) {
                diff.generateBinaryDiff(is1, is2, encoding, outWriter);
            } else {
                DebugLog.log("generating text diff");
                diff.generateTextDiff(is1, is2, encoding, outWriter);
            }
        } catch (SVNException e) {
            throwException(e);
        } catch (IOException ioe) {
            throw new ClientException(ioe.getMessage(), "", 0);            
        }
    }        
*/    
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
        return new PropertyGetCommand(javaSvnConfig).getProperties(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#resolved(java.io.File)
     */
    public void resolved(File path) throws SVNClientException {
        new ResolveCommand(javaSvnConfig).resolved(path);
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
        return new InfoCommand(javaSvnConfig).getInfo(file);
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
        new SwitchCommand(javaSvnConfig).switchToUrl(path, url, revision, recurse);
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
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#lock(java.io.File[], java.lang.String, boolean)
     */
    public void lock(File[] paths, String comment, boolean force)
            throws SVNClientException {
        notImplementedYet("lock");

    }
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#unlock(java.io.File[], boolean)
     */
    public void unlock(File[] paths, boolean force) throws SVNClientException {
        notImplementedYet("unlock");
    }
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#commit(java.io.File[], java.lang.String, boolean, boolean)
     */
    public long commit(File[] paths, String message, boolean recurse,
            boolean keepLocks) throws SVNClientException {
        return commit(paths, message, recurse);
    }
}