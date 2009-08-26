/*******************************************************************************
 * Copyright (c) 2006 svnClientAdapter project and others.
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
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.File;
import java.util.Date;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNConflictDescriptor;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.SVNRevision.Number;

/**
 * <p>
 * Implements a ISVNStatus using "svn status" and "svn info".</p>
 * 
 * @author Philip Schatz (schatz at tigris)
 * @author Cédric Chabanois (cchabanois at no-log.org)
 * @author Daniel Rall
 */
class CmdLineStatusComposite  implements ISVNStatus {
    private CmdLineStatusPart statusPart;
    private CmdLineInfoPart infoPart;

	/**
	 * <p>
	 * Creates a new status 
	 * </p>
     * Don't use this constructor if statusPart is null : use CmdLineStatusUnversioned instead 
	 * @param statusLinePart Generated from "svn status"
	 * @param infoLinePart Generated from "svn info"
	 */
	CmdLineStatusComposite(CmdLineStatusPart statusPart, CmdLineInfoPart infoPart) {
        this.statusPart = statusPart;
        this.infoPart = infoPart;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getTextStatus()
	 */
	public SVNStatusKind getTextStatus() {
        return statusPart.getTextStatus();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getPropStatus()
	 */
	public SVNStatusKind getPropStatus() {
		return statusPart.getPropStatus();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getUrlCopiedFrom()
	 */
	public SVNUrl getUrlCopiedFrom() {
		return infoPart.getCopyUrl();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastChangedDate()
	 */
	public Date getLastChangedDate() {
		return infoPart.getLastChangedDate();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastChangedRevision()
	 */
	public Number getLastChangedRevision() {
		return infoPart.getLastChangedRevision();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastCommitAuthor()
	 */
	public String getLastCommitAuthor() {
		return infoPart.getLastCommitAuthor();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getNodeKind()
	 */
	public SVNNodeKind getNodeKind() {
		return infoPart.getNodeKind();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getPath()
	 */
	public String getPath() {
		return (infoPart != null) ? infoPart.getPath() : statusPart.getPath();
	}
    
    /**
     * @return The absolute path to this item.
     */
    public File getFile() {
        return statusPart.getFile();
    }
    
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRevision()
	 */
	public Number getRevision() {
		return infoPart.getRevision();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrl()
	 */
	public SVNUrl getUrl() {
		return infoPart.getUrl();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrlString()
	 */
	public String getUrlString()
	{
		return infoPart.getUrlString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRepositoryTextStatus()
	 */
    public SVNStatusKind getRepositoryTextStatus() {
        return statusPart.getRepositoryTextStatus();
    }

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRepositoryPropStatus()
     */
    public SVNStatusKind getRepositoryPropStatus() {
        return statusPart.getRepositoryPropStatus();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictNew()
     */
    public File getConflictNew() {
        return infoPart.getConflictNew();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictOld()
     */
    public File getConflictOld() {
        return infoPart.getConflictOld();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictWorking()
     */
    public File getConflictWorking() {
        return infoPart.getConflictWorking();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isCopied()
     */
    public boolean isCopied() {
        return statusPart.isCopied();
    }

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isWcLocked()
	 */
	public boolean isWcLocked() {
		return statusPart.isWcLocked();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isSwitched()
	 */
	public boolean isSwitched() {
		return statusPart.isSwitched();
	}

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLockCreationDate()
	 */
    public Date getLockCreationDate() {
        return infoPart.getLockCreationDate();
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLockOwner()
     */
    public String getLockOwner() {
        return infoPart.getLockOwner();
    }
 
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLockComment()
     */
    public String getLockComment() {
        return infoPart.getLockComment();
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return statusPart.getPath() + "  T: " + statusPart.getTextStatus()
				+ " P: " + statusPart.getPropStatus();
	}

	public SVNConflictDescriptor getConflictDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasTreeConflict() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFileExternal() {
		// TODO Auto-generated method stub
		return false;
	}
}
