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
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.File;
import java.util.Date;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
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
 */
class CmdLineStatus  implements ISVNStatus {
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
	CmdLineStatus(CmdLineStatusPart statusPart, CmdLineInfoPart infoPart) {
        this.statusPart = statusPart;
        this.infoPart = infoPart;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isIgnored()
	 */
	public boolean isIgnored() {
		return statusPart.isIgnored();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isManaged()
	 */
	public boolean isManaged() {
		return statusPart.isManaged();
	}

	/**
	 * tells if the resource has a remote counter-part
	 * @return
	 */
	public boolean hasRemote() {
		return statusPart.hasRemote();
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
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isMerged()
	 */
	public boolean isMerged() {
		return statusPart.isMerged();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isDeleted()
	 */
	public boolean isDeleted() {
		return statusPart.isDeleted();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isModified()
	 */
	public boolean isModified() {
		return statusPart.isModified();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isAdded()
	 */
	public boolean isAdded() {
		return statusPart.isAdded();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isCopied()
	 */
	public boolean isCopied() {
		return statusPart.isCopied();
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
		return infoPart.getPath();
	}
    
    public File getFile() {
        return new File(getPath()).getAbsoluteFile();
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

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRepositoryTextStatus()
	 */
    public SVNStatusKind getRepositoryTextStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRepositoryPropStatus()
     */
    public SVNStatusKind getRepositoryPropStatus() {
        // TODO Auto-generated method stub
        return null;
    }

}
