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

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictNew()
     */
    public File getConflictNew() {
        String path = _s.getConflictNew();
        if (path != null) {
            return new File(getFile().getParent(), path).getAbsoluteFile();    
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictOld()
     */
    public File getConflictOld() {
        String path = _s.getConflictOld();
        if (path != null) {
            return new File(getFile().getParent(),path).getAbsoluteFile();    
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictWorking()
     */
    public File getConflictWorking() {
        String path = _s.getConflictWorking();
        if (path != null) {
            return new File(getFile().getParent(),path).getAbsoluteFile();    
        } else {
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLockCreationDate()
     */
    public Date getLockCreationDate() {
        return _s.getLockCreationDate();
    }
 
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLockOwner()
     */
    public String getLockOwner() {
        return _s.getLockOwner();
    }
 
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLockComment()
     */
    public String getLockComment() {
        return _s.getLockComment();
    }
}
