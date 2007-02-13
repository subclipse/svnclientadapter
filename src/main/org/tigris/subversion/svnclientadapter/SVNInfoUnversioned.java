/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter;

import java.io.File;
import java.util.Date;

import org.tigris.subversion.svnclientadapter.SVNRevision.Number;

/**
 * A special {@link ISVNInfo} implementation that is used if a File/Folder is not versioned.
 * 
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
public class SVNInfoUnversioned implements ISVNInfo {
	private File file;
    
	/**
	 * Constructor
	 * @param file
	 */
    public SVNInfoUnversioned(File file) {
    	this.file = file;
    }
    
    
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getFile()
	 */
	public File getFile() {
		return file;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getUrl()
	 */
	public SVNUrl getUrl() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getUrlString()
	 */
	public String getUrlString() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getUuid()
	 */
	public String getUuid() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getRepository()
	 */
	public SVNUrl getRepository() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getSchedule()
	 */
	public SVNScheduleKind getSchedule() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getNodeKind()
	 */
	public SVNNodeKind getNodeKind() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastCommitAuthor()
	 */
	public String getLastCommitAuthor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getRevision()
	 */
	public Number getRevision() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastChangedRevision()
	 */
	public Number getLastChangedRevision() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastChangedDate()
	 */
	public Date getLastChangedDate() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastDateTextUpdate()
	 */
	public Date getLastDateTextUpdate() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastDatePropsUpdate()
	 */
	public Date getLastDatePropsUpdate() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#isCopied()
	 */
	public boolean isCopied() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getCopyRev()
	 */
	public Number getCopyRev() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getCopyUrl()
	 */
	public SVNUrl getCopyUrl() {
		return null;
	}

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLockCreationDate()
     */
    public Date getLockCreationDate() {
        return null;
    }
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLockOwner()
     */
    public String getLockOwner() {
        return null;
    }
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLockComment()
     */
    public String getLockComment() {
        return null;
    }
}
