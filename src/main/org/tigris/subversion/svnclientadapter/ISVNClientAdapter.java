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
package org.tigris.subversion.svnclientadapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ISVNClientAdapter {

    public final static String REPOSITORY_BDB = "bdb";
    public final static String REPOSITORY_FSFS = "fsfs";
	
	
	/**
	 * Add a notification listener
	 */
	public abstract void addNotifyListener(ISVNNotifyListener listener);
	/**
	 * Remove a notification listener 
	 */
	public abstract void removeNotifyListener(ISVNNotifyListener listener);
	/**
	 * Sets the username.
	 */
	public abstract void setUsername(String username);
	/**
	 * Sets the password.
	 */
	public abstract void setPassword(String password);
	
    
    /**
	 * Register callback interface to supply username and password on demand
	 */
//	public abstract void setPromptUserPassword(PromptUserPassword prompt);
    
    
	/**
	 * Adds a file (or directory) to the repository.
	 * @exception ClientException
	 */
	public abstract void addFile(File file) throws SVNClientException;
	/**
	 * Adds a directory to the repository.
	 * @exception ClientException
	 */
	public abstract void addDirectory(File dir, boolean recurse)
		throws SVNClientException;
	/**
	 * Executes a revision checkout.
	 * @param moduleName name of the module to checkout.
	 * @param destPath destination directory for checkout.
	 * @param revision the revision number to checkout. If the number is -1
	 *                 then it will checkout the latest revision.
	 * @param recurse whether you want it to checkout files recursively.
	 * @exception ClientException
	 */
	public abstract void checkout(
		SVNUrl moduleName,
		File destPath,
		SVNRevision revision,
		boolean recurse)
		throws SVNClientException;
	/**
	 * Commits changes to the repository. This usually requires
	 * authentication, see Auth.
	 * @return Returns a long representing the revision. It returns a
	 *         -1 if the revision number is invalid.
	 * @param path files to commit.
	 * @param message log message.
	 * @param recurse whether the operation should be done recursively.
	 * @exception ClientException
	 */
	public abstract long commit(File[] paths, String message, boolean recurse)
		throws SVNClientException;
	/**
	 * List directory entries of a URL
	 * @param url
	 * @param revision
	 * @param recurse
	 * @return
	 * @throws ClientException
	 */
	public abstract ISVNDirEntry[] getList(
		SVNUrl url,
		SVNRevision revision,
		boolean recurse)
		throws SVNClientException;

	/**
	 * List directory entries of a directory
	 * @param url
	 * @param revision
	 * @param recurse
	 * @return
	 * @throws ClientException
	 */	
	public ISVNDirEntry[] getList(File path, SVNRevision revision, boolean recurse) 
    	throws SVNClientException;	
	
	/**
	 * get the dirEntry for the given url
	 * @param url
	 * @param revision
	 * @return
	 * @throws SVNClientException
	 */
	public ISVNDirEntry getDirEntry(SVNUrl url, SVNRevision revision)
			throws SVNClientException;

	/**
	 * get the dirEntry for the given directory 
	 * @param path
	 * @param revision
	 * @return
	 */
	public ISVNDirEntry getDirEntry(File path, SVNRevision revision)
			throws SVNClientException;
	
	/**
	 * Returns the status of a single file in the path.
	 *
	 * @param path File to gather status.
	 * @return a Status
	 */
    public abstract ISVNStatus getSingleStatus(File path)
        throws SVNClientException;
        
    /**
     * Returns the status of given resources
     * @param path
     * @return
     * @throws SVNClientException
     */    
	public abstract ISVNStatus[] getStatus(File[] path)
		throws SVNClientException;
	/**
	 * Returns the status of path and its children.
     * If descend is true, recurse fully, else do only immediate children.
     * If getAll is set, retrieve all entries; otherwise, retrieve only 
     * "interesting" entries (local mods and/or out-of-date).
     *
	 * @param path File to gather status.
     * @param descend get recursive status information
     * @param getAll get status information for all files
	 * @return a Status
	 */
	public abstract ISVNStatus[] getStatus(File path, boolean descend, boolean getAll)
		throws SVNClientException;

	/**
	 * Returns the status of path and its children.
     * If descend is true, recurse fully, else do only immediate children.
     * If getAll is set, retrieve all entries; otherwise, retrieve only 
     * "interesting" entries (local mods and/or out-of-date). Use the
     * contactServer option to get server change information.
     *
	 * @param path File to gather status.
     * @param descend get recursive status information
     * @param getAll get status information for all files
     * @param contactServer contact server to get remote changes
	 * @return a Status
	 */
	public abstract ISVNStatus[] getStatus(File path, boolean descend, boolean getAll, boolean contactServer)
	throws SVNClientException;

	/**
	 * copy and schedule for addition (with history)
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */
	public abstract void copy(File srcPath, File destPath)
		throws SVNClientException;
	/**
	 * immediately commit a copy of WC to URL
	 * @param srcPath
	 * @param destUrl
	 * @throws ClientException
	 */
	public abstract void copy(File srcPath, SVNUrl destUrl, String message)
		throws SVNClientException;
	/**
	 * check out URL into WC, schedule for addition
	 * @param srcUrl
	 * @param destPath
	 * @throws ClientException
	 */
	public abstract void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
		throws SVNClientException;
	/**
	 * complete server-side copy;  used to branch & tag
	 * @param srcUrl
	 * @param destUrl
	 * @throws ClientException
	 */
	public abstract void copy(
		SVNUrl srcUrl,
		SVNUrl destUrl,
		String message,
		SVNRevision revision)
		throws SVNClientException;
	/**
	 * item is deleted from the repository via an immediate commit.
	 * @param url
	 * @param message
	 * @throws ClientException
	 */
	public abstract void remove(SVNUrl url[], String message)
		throws SVNClientException;
	/**
	 * the item is scheduled for deletion upon the next commit.  
	 * Files, and directories that have not been committed, are immediately 
	 * removed from the working copy.  The command will not remove TARGETs 
	 * that are, or contain, unversioned or modified items; 
	 * use the force option to override this behaviour.
	 * @param file
	 * @param force
	 * @throws ClientException
	 */
	public abstract void remove(File file[], boolean force)
		throws SVNClientException;
	/**
	 * Exports a clean directory tree from the repository specified by
	 * srcUrl, at revision revision 
	 * @param srcUrl
	 * @param destPath
	 * @param revision
	 * @throws ClientException
	 */
	public abstract void doExport(
		SVNUrl srcUrl,
		File destPath,
		SVNRevision revision,
		boolean force)
		throws SVNClientException;
	/**
	 * Exports a clean directory tree from the working copy specified by
	 * PATH1 into PATH2.  all local changes will be preserved, but files
	 * not under revision control will not be copied.
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */
	public abstract void doExport(File srcPath, File destPath, boolean force)
		throws SVNClientException;
	/**
	 * Import file or directory PATH into repository directory URL at head
	 * @param path
	 * @param url
	 * @param newEntry new directory in which the contents of <i>path</i> are imported.
	 * 		  if null, copy top-level contents of PATH into URL directly
	 * @param message
	 * @param recurse
	 * @throws ClientException
	 */
	public abstract void doImport(
		File path,
		SVNUrl url,
		String message,
		boolean recurse)
		throws SVNClientException;
	/**
	 * Creates a directory directly in a repository
	 * @param url
	 * @param message
	 * @throws ClientException
	 */
	public abstract void mkdir(SVNUrl url, String message)
		throws SVNClientException;
	/**
	 * creates a directory on disk and schedules it for addition.
	 * @param file
	 * @throws ClientException
	 */
	public abstract void mkdir(File file) throws SVNClientException;
	/**
	 * Moves or renames a file.
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */
	public abstract void move(File srcPath, File destPath, boolean force)
		throws SVNClientException;
	/**
	 * Moves or renames a file.
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */
	public abstract void move(
		SVNUrl srcUrl,
		SVNUrl destUrl,
		String message,
		SVNRevision revision)
		throws SVNClientException;
	/**
	 * Update a file or a directory
	 * @param path
	 * @param revision
	 * @param recurse
     * @return Returns a long representing the revision. It returns a
     *         -1 if the revision number is invalid.
	 * @throws ClientException
	 */
	public abstract long update(File path, SVNRevision revision, boolean recurse)
		throws SVNClientException;
	/**
	 * Restore pristine working copy file (undo all local edits)
	 * @param path
	 * @param recurse
	 * @throws ClientException
	 */
	public abstract void revert(File path, boolean recurse)
		throws SVNClientException;
	/**
	 * Get the log messages for a set of revision(s) 
	 * @param url
	 * @param revisionStart
	 * @param revisionEnd
	 * @return
	 */
	public abstract ISVNLogMessage[] getLogMessages(
		SVNUrl url,
		SVNRevision revisionStart,
		SVNRevision revisionEnd)
		throws SVNClientException;
	/**
	 * Get the log messages for a set of revision(s)
	 * @param path
	 * @param revisionStart
	 * @param revisionEnd
	 * @return
	 */
	public abstract ISVNLogMessage[] getLogMessages(
		File path,
		SVNRevision revisionStart,
		SVNRevision revisionEnd)
		throws SVNClientException;
	/**
	 * get the content of a file
	 * @param url
	 * @param revision
	 */
	public abstract InputStream getContent(SVNUrl url, SVNRevision revision)
		throws SVNClientException;
		
	/**
	 * get the content of a file
	 * @param path
	 * @param revision
	 * @return
	 * @throws SVNClientException
	 */
	public InputStream getContent(File path, SVNRevision revision) 
		throws SVNClientException;
		
	/**
	 * set a property
	 * @param path
	 * @param propertyName
	 * @param propertyValue
	 * @param recurse
	 * @throws ClientException
	 */
	public abstract void propertySet(
		File path,
		String propertyName,
		String propertyValue,
		boolean recurse)
		throws SVNClientException;
	/**
	 * set a property using the content of a file 
	 */
	public abstract void propertySet(
		File path,
		String propertyName,
		File propertyFile,
		boolean recurse)
		throws SVNClientException, IOException;
	/**
	 * get a property or null if property is not found
	 * @param path
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 * @throws ClientException
	 */
	public abstract ISVNProperty propertyGet(File path, String propertyName)
		throws SVNClientException;
	/**
	 * delete a property
	 * @param path
	 * @param propertyName
	 * @param recurse
	 * @throws ClientException
	 */
	public abstract void propertyDel(
		File path,
		String propertyName,
		boolean recurse)
		throws SVNClientException;
	/**
	 * get the ignored patterns for the given directory
	 * if path is not a directory, returns null 
	 */
	public abstract List getIgnoredPatterns(File path)
		throws SVNClientException;
	/**
	 * add a pattern to svn:ignore property 
	 */
	public abstract void addToIgnoredPatterns(File path, String pattern)
		throws SVNClientException;
	/**
	 * set the ignored patterns for the given directory 
	 */
	public abstract void setIgnoredPatterns(File path, List patterns)
		throws SVNClientException;
	/**
	 * display the differences between two paths. 
	 */
	public abstract void diff(
		File oldPath,
		SVNRevision oldPathRevision,
		File newPath,
		SVNRevision newPathRevision,
		File outFile,
		boolean recurse)
		throws SVNClientException;
	public abstract void diff(File path, File outFile, boolean recurse)
		throws SVNClientException;
	/**
	 * display the differences between two urls. 
	 */
	public abstract void diff(
		SVNUrl oldUrl,
		SVNRevision oldUrlRevision,
		SVNUrl newUrl,
		SVNRevision newUrlRevision,
		File outFile,
		boolean recurse)
		throws SVNClientException;
	public abstract void diff(
		SVNUrl url,
		SVNRevision oldUrlRevision,
		SVNRevision newUrlRevision,
		File outFile,
		boolean recurse)
		throws SVNClientException;

    /**
     * returns the keywords used for substitution for the given resource
     * @param path
     * @return
     * @throws SVNClientException
     */         
    public abstract SVNKeywords getKeywords(File path) throws SVNClientException;    

    /**
     * set the keywords substitution for the given resource
     * @param path
     * @param keywords
     * @param recurse
     * @throws SVNClientException
     */    
    public abstract void setKeywords(File path, SVNKeywords keywords, boolean recurse) throws SVNClientException;

    /**
     * add some keyword to the keywords substitution list
     * @param path
     * @param keywords
     * @return
     * @throws SVNClientException
     */    
    public abstract SVNKeywords addKeywords(File path, SVNKeywords keywords) throws SVNClientException;

    /**
     * remove some keywords to the keywords substitution list
     * @param path
     * @param keywords
     * @return
     * @throws SVNClientException
     */    
    public SVNKeywords removeKeywords(File path, SVNKeywords keywords) throws SVNClientException;

    /**
     * Output the content of specified url with revision and 
     * author information in-line. 
     */
    public ISVNAnnotations annotate(SVNUrl url, SVNRevision revisionStart, SVNRevision revisionEnd)
        throws SVNClientException;

    /**
     * Output the content of specified file with revision and 
     * author information in-line. 
     */
    public ISVNAnnotations annotate(File file, SVNRevision revisionStart, SVNRevision revisionEnd)
        throws SVNClientException;
    
    /**
     * Get all the properties for the given file or dir
     * @param path
     * @return
     * @throws SVNClientException
     */    
	public abstract ISVNProperty[] getProperties(File path) throws SVNClientException;

	/**
	 * Remove 'conflicted' state on working copy files or directories
	 * @param path
	 * @throws SVNClientException
	 */ 	
	public abstract void resolved(File path) throws SVNClientException;
    
	/**
	 * Create a new, empty repository at path 
	 * 
	 * @param path
	 * @param repositoryType either {@link ISVNClientAdapter#REPOSITORY_BDB} or
	 *        {@link ISVNClientAdapter#REPOSITORY_FSFS} or null (will use svnadmin default)
	 * @throws SVNClientException
	 */
	public abstract void createRepository(File path, String repositoryType) throws SVNClientException;
	
	/**
	 * Cancel the current operation
	 * 
	 * @throws SVNClientException
	 */
	public void cancelOperation() throws SVNClientException;
	
	/**
	 * get information about a file or directory.
	 * @param file
	 * @return
	 * @throws SVNClientException
	 */
	public ISVNInfo getInfo(File file) throws SVNClientException;
	
    
    public SVNUrl getRepositoryRoot(SVNUrl url);

    
    /**
     * Update the working copy to mirror a new URL within the repository.
     * This behaviour is similar to 'svn update', and is the way to
     * move a working copy to a branch or tag within the same repository.
     * @param url
     * @param path
     * @param revision
     * @param recurse
     * @throws SVNClientException
     */
    public void switchToUrl(File path, SVNUrl url, SVNRevision revision, boolean recurse) throws SVNClientException;
    
    /**
     * Set the configuration directory.
     * @param dir
     * @throws SVNClientException
     */
    public void setConfigDirectory(File dir) throws SVNClientException;
    
    /**
     * Perform a clanup on the working copy.  This will remove any stale transactions
     * @param dir
     * @throws SVNClientException
     */
    public abstract void cleanup(File dir) throws SVNClientException;
    
}