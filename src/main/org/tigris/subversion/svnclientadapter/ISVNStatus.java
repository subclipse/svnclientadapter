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
import java.util.Date;

/**
 * 
 * @author philip schatz
 */
public interface ISVNStatus {

    /**
     * get the url of the resource on repository
     * @return
     */
	SVNUrl getUrl();

	/**
	 * get the last changed revision or null if resource is not managed 
	 */
	SVNRevision.Number getLastChangedRevision();

    /**
     * date this resource last changed
     * @return
     */
	Date getLastChangedDate();

	/**
	 * get the last commit author or null if resource is not versionned
	 * or if last commit author is unknown
	 * @return
	 */
	String getLastCommitAuthor();

	SVNStatusKind getTextStatus();

	SVNStatusKind getRepositoryTextStatus();
	
	/**
     * status of properties
	 * will return either Kind.NORMAL, Kind.CONFLICTED or Kind.MODIFIED
	 */
	SVNStatusKind getPropStatus();

	SVNStatusKind getRepositoryPropStatus();

	/**
	 * get the revision of the resource or null if not managed 
	 */
	SVNRevision.Number getRevision();

	String getPath();
    
    /**
     * 
     * @return the absolute file corresponding to this resource
     */
    File getFile();

    /**
     * @return return the nodekind of the managed resource
     * if resource is not managed, SVNNodeKind.UNKNOWN is returned 
     */
	SVNNodeKind getNodeKind();

    /**
     * 
     * @return
     */
    boolean isCopied();    
    
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

}