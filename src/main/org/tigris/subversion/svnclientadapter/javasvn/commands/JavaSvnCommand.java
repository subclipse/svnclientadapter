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

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnNotificationHandler;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnUtils;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;

abstract class JavaSvnCommand {
    protected JavaSvnConfig javaSvnConfig;
    protected JavaSvnNotificationHandler notificationHandler; 
    
    public JavaSvnCommand(JavaSvnConfig config) {
        this.javaSvnConfig = config;
        this.notificationHandler = config.getNotificationHandler();
    }

    protected void notImplementedYet(String command) throws SVNClientException {
        throw new SVNClientException("Not implemented yet : " + command);
    }
    
    /**
     * Get the root workspace for given file or directory
     */
    public ISVNWorkspace getRootWorkspace(File file) throws SVNException {
        return JavaSvnUtils.getRootWorkspace(javaSvnConfig, file);
    }

    /**
     * get the root workspace for the given set of files or directories
     * @param javaSvnConfig
     * @param files
     * @return
     * @throws SVNException
     */
    public ISVNWorkspace getRootWorkspace(File[] files) throws SVNException {
        return JavaSvnUtils.getRootWorkspace(javaSvnConfig, files);
    }

    /**
     * Get the workspace for the given directory
     * 
     * @param dir
     * @return @throws
     *         SVNException
     */
    public ISVNWorkspace getWorkspace(File dir) throws SVNException {
        return JavaSvnUtils.getWorkspace(javaSvnConfig, dir);
    }

    /**
     * Get the path of given file in the workspace
     * 
     * @param ws
     * @param file
     * @return
     */
    public String getWorkspacePath(ISVNWorkspace ws, File file) {
        return JavaSvnUtils.getWorkspacePath(ws, file);
    }

    /**
     * Get the path of given url in the given repository
     * 
     * @param repository
     * @param url
     * @return @throws
     *         SVNException
     */
    public String getRepositoryPath(SVNRepository repository, SVNUrl url)
            throws SVNException {
        return JavaSvnUtils.getRepositoryPath(repository, url);
    }

    /**
     * Get the path of given url relative to the repository root 
     *  
     * @param repository
     * @param url
     * @return
     * @throws SVNException
     */
    public String getRepositoryRootPath(SVNRepository repository, SVNUrl url)
            throws SVNException {
        return JavaSvnUtils.getRepositoryRootPath(repository, url);
    }

    /**
     * Get the repository at the given location
     * 
     * @param reposLocation
     * @return @throws
     *         SVNException
     */
    public SVNRepository getRepository(SVNRepositoryLocation reposLocation)
            throws SVNException {
        return JavaSvnUtils.getRepository(javaSvnConfig, reposLocation);
    }

    /**
     * Get the repository at the given location
     * 
     * @param javaSvnConfig
     * @param url
     * @return
     * @throws SVNException
     */
    public SVNRepository getRepository(SVNUrl url) throws SVNException {
        return JavaSvnUtils.getRepository(javaSvnConfig, url);
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
    public long getRevisionNumber(SVNRevision revision,
            SVNRepository resourceRepository, ISVNWorkspace workspace,
            String path) throws SVNException {
        return JavaSvnUtils.getRevisionNumber(revision, resourceRepository, workspace, path); 
    }
        
    
}
