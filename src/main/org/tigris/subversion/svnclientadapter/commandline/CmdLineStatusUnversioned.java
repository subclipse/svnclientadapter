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
package org.tigris.subversion.svnclientadapter.commandline;

import java.util.Date;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * <p>
 * A special status class that is used if a File/Folder is not versioned.</p>
 * 
 * @author Philip Schatz <a href="mailto:schatzp@purdue.edu">schatzp@purdue.edu</a>
 * @since Sep 13, 2003
 */
public class CmdLineStatusUnversioned implements ISVNStatus {

	/**
	 * 
	 */
	public CmdLineStatusUnversioned() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isIgnored()
	 */
	public boolean isIgnored() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isManaged()
	 */
	public boolean isManaged() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#hasRemote()
	 */
	public boolean hasRemote() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getUrl()
	 */
	public SVNUrl getUrl() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getLastChangedRevision()
	 */
	public SVNRevision.Number getLastChangedRevision() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getLastChangedDate()
	 */
	public Date getLastChangedDate() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getLastCommitAuthor()
	 */
	public String getLastCommitAuthor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getTextStatus()
	 */
	public ISVNStatus.Kind getTextStatus() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isMerged()
	 */
	public boolean isMerged() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isDeleted()
	 */
	public boolean isDeleted() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isModified()
	 */
	public boolean isModified() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isAdded()
	 */
	public boolean isAdded() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getRevision()
	 */
	public SVNRevision.Number getRevision() {
		return SVNRevision.INVALID_REVISION;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isCopied()
	 */
	public boolean isCopied() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getPath()
	 */
	public String getPath() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getNodeKind()
	 */
	public SVNNodeKind getNodeKind() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getUrlCopiedFrom()
	 */
	public String getUrlCopiedFrom() {
		// TODO Auto-generated method stub
		return null;
	}

}
