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
 * <p>
 * A special status class that is used if a File/Folder is not versioned.</p>
 * 
 * @author Philip Schatz (schatz at tigris)
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
public class SVNStatusUnversioned implements ISVNStatus {
    private File file;
    private boolean isIgnored = false;
	
    public SVNStatusUnversioned(File file, boolean isIgnored) {
        this.file = file;
        this.isIgnored = isIgnored; // a file can be unversioned and ignored ...
    }
    
	public SVNStatusUnversioned(File file) {
		this.file = file;
	}
    
    public boolean isIgnored() {
		return isIgnored;
	}
	public boolean isManaged() {
		return false;
	}
	public boolean hasRemote() {
		return false;
	}
	public SVNUrl getUrl() {
		return null;
	}
	public SVNRevision.Number getLastChangedRevision() {
		return null;
	}
	public Date getLastChangedDate() {
		return null;
	}
	public String getLastCommitAuthor() {
		return null;
	}
	public SVNStatusKind getTextStatus() {
		return SVNStatusKind.UNVERSIONED;
	}
	
	public SVNStatusKind getPropStatus() {
		return SVNStatusKind.NORMAL;
	}
	
    public SVNStatusKind getRepositoryTextStatus() {
        return SVNStatusKind.UNVERSIONED;
    }

    public SVNStatusKind getRepositoryPropStatus() {
        return SVNStatusKind.UNVERSIONED;
    }

    public boolean isMerged() {
		return false;
	}
	public boolean isDeleted() {
		return false;
	}
	public boolean isModified() {
		return false;
	}
	public boolean isAdded() {
		return false;
	}
	public SVNRevision.Number getRevision() {
		return SVNRevision.INVALID_REVISION;
	}
	public boolean isCopied() {
		return false;
	}
	public String getPath() {
		return file.getPath();
	}
    public File getFile() {
        return file.getAbsoluteFile();
    }
    
	public SVNNodeKind getNodeKind() {
        // getNodeKind returns the kind of the managed resource. If file is
        // not managed we must return UNKNOWN
        return SVNNodeKind.UNKNOWN;
	}
	public SVNUrl getUrlCopiedFrom() {
		return null;
	}

}
