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
package org.tigris.subversion.svnclientadapter.javahl;

import java.net.MalformedURLException;
import java.util.Date;

import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * adapter : convert from Status to ISVNStatus
 *  
 * @author philip schatz
 */
public class JhlStatus implements ISVNStatus {

	private Status _s;

	public JhlStatus(Status status) {
		super();
		_s = status;
	}

	public boolean isIgnored() {
		return _s.isIgnored();
	}

	public boolean isManaged() {
		return _s.isManaged();
	}

	public boolean hasRemote() {
		return _s.hasRemote();
	}

	public SVNUrl getUrl() {
		try {
            return new SVNUrl(_s.getUrl());
        } catch (MalformedURLException e) {
            //should never happen.
            return null;
        }
	}

	public SVNRevision.Number getLastChangedRevision() {
		return (SVNRevision.Number)JhlConverter.convert(_s.getLastChangedRevision());
	}

	public Date getLastChangedDate() {
		return _s.getLastChangedDate();
	}

	public String getLastCommitAuthor() {
		return _s.getLastCommitAuthor();
	}

	public ISVNStatus.Kind getTextStatus() {
        return JhlConverter.convertStatusKind(_s.getTextStatus());
	}

	public boolean isMerged() {
		return _s.isMerged();
	}

	public boolean isDeleted() {
		return _s.isDeleted();
	}

	public boolean isModified() {
		return _s.isModified();
	}

	public boolean isAdded() {
		return _s.isAdded();
	}

	public SVNRevision.Number getRevision() {
		return (SVNRevision.Number)JhlConverter.convert(_s.getRevision());
	}

	public boolean isCopied() {
		return _s.isCopied();
	}

	public String getPath() {
		return _s.getPath();
	}

	public SVNNodeKind getNodeKind() {
        return JhlConverter.convertNodeKind(_s.getNodeKind());
	}

	public String getUrlCopiedFrom() {
		return _s.getUrlCopiedFrom();
	}

}
