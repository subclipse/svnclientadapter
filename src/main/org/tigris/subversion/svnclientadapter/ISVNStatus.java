/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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