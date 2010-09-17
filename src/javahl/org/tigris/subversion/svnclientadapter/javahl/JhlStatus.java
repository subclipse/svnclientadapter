/*******************************************************************************
 * Copyright (c) 2003, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;

import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNConflictDescriptor;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * A JavaHL based implementation of {@link ISVNStatus}.
 * Actually just an adapter from {@link org.tigris.subversion.javahl.Status}
 *  
 * @author philip schatz
 */
public class JhlStatus implements ISVNStatus {

	protected Status _s;
	private SVNRevision.Number lastChangedRevision;
	private String lastChangedAuthor;
	private Date lastChangedDate;

	/**
	 * Constructor
	 * @param status
	 */
	public JhlStatus(Status status) {
		// note that status.textStatus must be different than 0 (the resource must exist)
        super();
		_s = status;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrl()
	 */
	public SVNUrl getUrl() {
		try {
            String urlString = getUrlString();
            return (urlString != null) ? new SVNUrl(urlString) : null;
        } catch (MalformedURLException e) {
            //should never happen.
            return null;
        }
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrlString()
	 */
	public String getUrlString()
	{
		return _s.getUrl();
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastChangedRevision()
	 */
	public SVNRevision.Number getLastChangedRevision() {
        // we don't use 
        // return (SVNRevision.Number)JhlConverter.convert(_s.getLastChangedRevision());
        // as _s.getLastChangedRevision() is currently broken if revision is -1 
		if (lastChangedRevision != null)
			return lastChangedRevision;
		if (_s.getReposLastCmtAuthor() == null)
			return JhlConverter.convertRevisionNumber(_s.getLastChangedRevisionNumber());
		else
			if (_s.getReposLastCmtRevisionNumber() == 0)
				return null;
			return JhlConverter.convertRevisionNumber(_s.getReposLastCmtRevisionNumber());
	}
	
	public SVNRevision.Number getReposLastChangedRevision() {
		return JhlConverter.convertRevisionNumber(_s.getReposLastCmtRevisionNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastChangedDate()
	 */
	public Date getLastChangedDate() {
		if (lastChangedDate != null)
			return lastChangedDate;
		if (_s.getReposLastCmtAuthor() == null)
			return _s.getLastChangedDate();
		else
			return _s.getReposLastCmtDate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastCommitAuthor()
	 */
	public String getLastCommitAuthor() {
		if (lastChangedAuthor != null)
			return lastChangedAuthor;
		if (_s.getReposLastCmtAuthor() == null)
			return _s.getLastCommitAuthor();
		else
			return _s.getReposLastCmtAuthor();
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
		return JhlConverter.convertStatusKind(_s.getPropStatus());
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRevision()
	 */
	public SVNRevision.Number getRevision() {
		return JhlConverter.convertRevisionNumber(_s.getRevisionNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isCopied()
	 */
	public boolean isCopied() {
		return _s.isCopied();
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isWcLocked()
	 */
	public boolean isWcLocked() {
		return _s.isLocked();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isSwitched()
	 */
	public boolean isSwitched() {
		return _s.isSwitched();
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
		SVNNodeKind nodeKind;
		if (_s.getReposLastCmtAuthor() == null)
			nodeKind = JhlConverter.convertNodeKind(_s.getNodeKind());
		else
			nodeKind = JhlConverter.convertNodeKind(_s.getReposKind());
        return nodeKind;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrlCopiedFrom()
	 */
	public SVNUrl getUrlCopiedFrom() {
		try {
            String url = _s.getUrlCopiedFrom();
            return (url != null) ? new SVNUrl(url) : null;
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
		return (path != null) ? new File(getFile().getParent(), path)
				.getAbsoluteFile() : null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictOld()
	 */
    public File getConflictOld() {
		String path = _s.getConflictOld();
		return (path != null) ? new File(getFile().getParent(), path)
				.getAbsoluteFile() : null;
	}

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictWorking()
	 */
    public File getConflictWorking() {
		String path = _s.getConflictWorking();
		return (path != null) ? new File(getFile().getParent(), path)
				.getAbsoluteFile() : null;
	}
    
    /*
	 * (non-Javadoc)
	 * 
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

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getTreeConflicted()
     */
	public boolean hasTreeConflict() {
		return _s.hasTreeConflict();
	}
	
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isFileExternal()
     */
	public boolean isFileExternal() {
		return _s.isFileExternal();
	}	

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictDescriptor()
     */
	public SVNConflictDescriptor getConflictDescriptor() {
		return JhlConverter.convertConflictDescriptor(_s.getConflictDescriptor());
	}
    
    public void updateFromInfo(ISVNInfo info) {
    	lastChangedRevision = info.getLastChangedRevision();
    	lastChangedAuthor = info.getLastCommitAuthor();
    	lastChangedDate = info.getLastChangedDate();
    }
    
    public void updateFromStatus(JhlStatus info) {
    	lastChangedRevision = info.getLastChangedRevision();
    	lastChangedAuthor = info.getLastCommitAuthor();
    	lastChangedDate = info.getLastChangedDate();
    }
    
    /**
     * A special JhlStatus subclass representing svn:external resource.
     * (JavaHL answer two sort of statuses on externals:
     * - when ignoreExternals is set to true during call to status(),
     *  the return status has textStatus set to EXTERNAL, but the url is null.<br>
     * - when ignoreExternals is set to false during call to status(),
     *  besides the "external" status, second status with url and all fields is returned too, 
     *  but this one has textStatus NORMAL)
     */
    public static class JhlStatusExternal extends JhlStatus
    {
    	private String url;

    	/**
    	 * Constructor
    	 * @param status
    	 */
    	public JhlStatusExternal(JhlStatus status) {
            this(status, null);
    	}

    	/**
    	 * Constructor
    	 * @param status
    	 * @param url
    	 */
    	public JhlStatusExternal(JhlStatus status, String url) {
            super(status._s);
            this.url = url;
    	}

    	public SVNStatusKind getTextStatus() {
            return SVNStatusKind.EXTERNAL;
    	}    	
    	
    	public String getUrlString()
    	{
    		return (url != null) ? url : super.getUrlString();
    	}
    }

}
