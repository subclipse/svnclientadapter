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

    boolean isIgnored();

	boolean isManaged();

	boolean hasRemote();

	SVNUrl getUrl();

	/**
	 * get the last changed revision or null if resource is not managed 
	 */
	SVNRevision.Number getLastChangedRevision();

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
	 * will return either Kind.NORMAL, Kind.CONFLICTED or Kind.MODIFIED
	 * 
	 */
	SVNStatusKind getPropStatus();

	SVNStatusKind getRepositoryPropStatus();

	boolean isMerged();

	boolean isDeleted();

	/**
	 * returns true if the resource has been modified.
	 * modifications to properties are not taken into account.
	 */
	boolean isModified();

	boolean isAdded();

	/**
	 * get the revision of the resource or null if not managed 
	 */
	SVNRevision.Number getRevision();

	boolean isCopied();
	
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

	SVNUrl getUrlCopiedFrom();
	
}