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
        if (isIgnored) {
        	return SVNStatusKind.IGNORED;
        }
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
    public boolean isLocked() {
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

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictNew()
     */
    public File getConflictNew() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictOld()
     */
    public File getConflictOld() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictWorking()
     */
    public File getConflictWorking() {
        return null;
    }

}
