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

import java.io.File;
import java.util.Date;

import org.tigris.subversion.svnclientadapter.SVNBaseDir;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNWorkspaceManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.ws.fs.FSEntryFactory;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;
import org.tmatesoft.svn.core.io.SVNSimpleCredentialsProvider;
import org.tmatesoft.svn.util.PathUtil;
import org.tmatesoft.svn.util.SVNUtil;

/**
 * Some utilities for JavaSvn client adapter
 */
public class JavaSvnUtils {

    public static void setupJavaSvn() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSEntryFactory.setup();
    }
    
    /**
     * Get the root workspace for given file or directory
     */
    public static ISVNWorkspace getRootWorkspace(JavaSvnConfig javaSvnConfig, File file) throws SVNException {
        ISVNWorkspace ws = SVNUtil.createWorkspace(file.getAbsolutePath());
        ws.setCredentials(javaSvnConfig.getUsername(), javaSvnConfig.getPassword());
        ws.addWorkspaceListener(javaSvnConfig.getNotificationHandler());
        ws.setGlobalIgnore("*.o *.lo *.la #*# .*.rej *.rej .*~ *~ .#*");
        return ws;
    }

    /**
     * get the root workspace for the given set of files or directories
     * @param javaSvnConfig
     * @param files
     * @return
     * @throws SVNException
     */
    public static ISVNWorkspace getRootWorkspace(JavaSvnConfig javaSvnConfig, File[] files) throws SVNException {
        ISVNWorkspace ws;
        if (files.length == 1) {
            ws = getRootWorkspace(javaSvnConfig, files[0]);
        } else {
            ws = getRootWorkspace(javaSvnConfig, SVNBaseDir.getRootDir(files));
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
    public static ISVNWorkspace getWorkspace(JavaSvnConfig javaSvnConfig,File dir) throws SVNException {
        ISVNWorkspace ws = SVNWorkspaceManager.createWorkspace("file", dir
                .getAbsolutePath());
        ws.setCredentials(javaSvnConfig.getUsername(), javaSvnConfig.getPassword());
        ws.addWorkspaceListener(javaSvnConfig.getNotificationHandler());
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
    public static String getWorkspacePath(ISVNWorkspace ws, File file) {
        return SVNUtil.getWorkspacePath(ws, file.getAbsolutePath());
    }

    /**
     * Get the path of given url in the given repository
     * 
     * @param repository
     * @param url
     * @return @throws
     *         SVNException
     */
    public static String getRepositoryPath(SVNRepository repository, SVNUrl url)
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

    /**
     * Get the path of given url relative to the repository root 
     *  
     * @param repository
     * @param url
     * @return
     * @throws SVNException
     */
    public static String getRepositoryRootPath(SVNRepository repository, SVNUrl url)
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
    public static SVNRepository getRepository(JavaSvnConfig javaSvnConfig,SVNRepositoryLocation reposLocation)
            throws SVNException {
        SVNRepository repository = SVNRepositoryFactory.create(reposLocation);
        if (javaSvnConfig.getUsername() != null && javaSvnConfig.getPassword() != null) {
            repository.setCredentialsProvider(new SVNSimpleCredentialsProvider(
                    javaSvnConfig.getUsername(), javaSvnConfig.getPassword()));
        }
        return repository;
    }

    /**
     * Get the repository at the given location
     * 
     * @param javaSvnConfig
     * @param url
     * @return
     * @throws SVNException
     */
    public static SVNRepository getRepository(JavaSvnConfig javaSvnConfig,SVNUrl url) throws SVNException {
        return getRepository(javaSvnConfig, SVNRepositoryLocation.parseURL(url.toString()));
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
    
    
}
