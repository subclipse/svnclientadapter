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

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;

import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
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
		// note that status.textStatus must be different than 0 (the resource must exist)
        super();
		_s = status;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isIgnored()
	 */
	public boolean isIgnored() {
		return _s.isIgnored();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isManaged()
	 */
	public boolean isManaged() {
		return _s.isManaged();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#hasRemote()
	 */
	public boolean hasRemote() {
		return _s.hasRemote();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrl()
	 */
	public SVNUrl getUrl() {
		try {
            String url = _s.getUrl();
            if (url == null)
                return null;
            else
                return new SVNUrl(url);
        } catch (MalformedURLException e) {
            //should never happen.
            return null;
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastChangedRevision()
	 */
	public SVNRevision.Number getLastChangedRevision() {
        // we don't use 
        // return (SVNRevision.Number)JhlConverter.convert(_s.getLastChangedRevision());
        // as _s.getLastChangedRevision() is currently broken if revision is -1 
        return (SVNRevision.Number)JhlConverter.convertRevisionNumber(_s.getLastChangedRevisionNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastChangedDate()
	 */
	public Date getLastChangedDate() {
		return _s.getLastChangedDate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastCommitAuthor()
	 */
	public String getLastCommitAuthor() {
		return _s.getLastCommitAuthor();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getTextStatus()
	 */
	public SVNStatusKind getTextStatus() {
        return JhlConverter.convertStatusKind(_s.getTextStatus());
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getPropStatus()
	 */
	public SVNStatusKind getPropStatus() {
		SVNStatusKind kind = JhlConverter.convertStatusKind(_s.getPropStatus());
		if (kind.equals(SVNStatusKind.NONE)) {
			// javahl returns NONE when there are no properties
			// we want to have the same behaviour for command line interface and 
			// javahl, so we return NORMAL 
			kind =  SVNStatusKind.NORMAL;
		}
		return kind;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isMerged()
	 */
	public boolean isMerged() {
		return _s.isMerged();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isDeleted()
	 */
	public boolean isDeleted() {
		return _s.isDeleted();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isModified()
	 */
	public boolean isModified() {
		return _s.isModified();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isAdded()
	 */
	public boolean isAdded() {
		return _s.isAdded();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRevision()
	 */
	public SVNRevision.Number getRevision() {
		return (SVNRevision.Number)JhlConverter.convertRevisionNumber(_s.getRevisionNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isCopied()
	 */
	public boolean isCopied() {
		return _s.isCopied();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getPath()
	 */
	public String getPath() {
		return _s.getPath();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getFile()
	 */
    public File getFile() {
        return new File(getPath()).getAbsoluteFile();
    }

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getNodeKind()
	 */
	public SVNNodeKind getNodeKind() {
        SVNNodeKind nodeKind = JhlConverter.convertNodeKind(_s.getNodeKind());
        return nodeKind;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrlCopiedFrom()
	 */
	public SVNUrl getUrlCopiedFrom() {
		try {
            String url = _s.getUrlCopiedFrom();
            if (url == null)
                return null;
            else
                return new SVNUrl(url);
        } catch (MalformedURLException e) {
            //should never happen.
            return null;
        }
	}

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRepositoryTextStatus()
     */
    public SVNStatusKind getRepositoryTextStatus() {
        return JhlConverter.convertStatusKind(_s.getRepositoryTextStatus());
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRepositoryPropStatus()
     */
    public SVNStatusKind getRepositoryPropStatus() {
        return JhlConverter.convertStatusKind(_s.getRepositoryPropStatus());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getPath() + " "+getTextStatus().toString();
    }
    
    
}
