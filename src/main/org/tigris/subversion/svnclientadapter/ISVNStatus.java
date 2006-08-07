/*******************************************************************************
 * Copyright (c) 2003, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter;

import java.io.File;
import java.util.Date;

/**
 * An interface defining the status of one subversion item (file or directory) in
 * the working copy or repository.
 * 
 * @author philip schatz
 */
public interface ISVNStatus {

    /**
     * @return the SVNUrl instance of url of the resource on repository
     */
	SVNUrl getUrl();
	
	/**
	 * @return the url (String) of the resource in repository
	 */
	String getUrlString();

	/**
	 * @return the last changed revision or null if resource is not managed 
	 */
	SVNRevision.Number getLastChangedRevision();

    /**
     * @return date this resource last changed
     */
	Date getLastChangedDate();

	/**
	 * get the last commit author or null if resource is not versionned
	 * or if last commit author is unknown
	 * @return the last commit author or null 
	 */
	String getLastCommitAuthor();

    /**
     * @return the file or directory status
     */
	SVNStatusKind getTextStatus();

    /**
     * @return the file or directory status of base
     */
	SVNStatusKind getRepositoryTextStatus();
	
	/**
     * @return status of properties (either Kind.NORMAL, Kind.CONFLICTED or Kind.MODIFIED)
	 */
	SVNStatusKind getPropStatus();

    /**
     * @return the status of the properties base (either Kind.NORMAL, Kind.CONFLICTED or Kind.MODIFIED)
     */
	SVNStatusKind getRepositoryPropStatus();

	/**
	 * @return the revision of the resource or null if not managed 
	 */
	SVNRevision.Number getRevision();

    /**
     * @return The path to this item relative to the directory from
     * which <code>status</code> was run.
     */
	String getPath();
    
    /**
     * @return The absolute path to this item.
     */
    File getFile();

    /**
     * @return The node kind of the managed resource, or {@link
     * SVNNodeKind#UNKNOWN} not managed.
     */
	SVNNodeKind getNodeKind();

    /**
     * @return true when the resource was copied
     */
    boolean isCopied();    
    
    /**
     * @return true when the working copy directory is locked. 
     */
    boolean isWcLocked();
    
    /**
     * @return true when the resource was switched relative to its parent.
     */
    boolean isSwitched();
    
    /**
     * @return the url of the copy source if copied, null otherwise
     */
	SVNUrl getUrlCopiedFrom();

    /**
     * Returns in case of conflict, the file of the most recent repository
     * version
     * @return the filename of the most recent repository version
     */
    public File getConflictNew();

    /**
     * Returns in case of conflict, the file of the common base version
     * @return the filename of the common base version
     */
    public File getConflictOld();

    /**
     * Returns in case of conflict, the file of the former working copy
     * version
     * @return the filename of the former working copy version
     */
    public File getConflictWorking();

    /**
     * Returns the lock  owner
     * @return the lock owner
     */
    public String getLockOwner();

    /**
     * Returns the lock creation date
     * @return the lock creation date
     */
    public Date getLockCreationDate();

    /**
     * Returns the lock  comment
     * @return the lock comment
     */
    public String getLockComment();

}
