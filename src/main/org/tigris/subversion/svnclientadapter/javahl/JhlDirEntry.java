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

import java.util.Date;

import org.tigris.subversion.javahl.DirEntry;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;

/**
 * adapter : convert from DirEntry to ISVNDirEntry 
 *  
 * @author philip schatz
 */
public class JhlDirEntry implements ISVNDirEntry {

	private DirEntry _d;

	public JhlDirEntry(DirEntry d) {
		super();
		_d = d;
	}

	public SVNNodeKind getNodeKind() {
        return JhlConverter.convertNodeKind(_d.getNodeKind());
	}

	public boolean getHasProps() {
		return _d.getHasProps();
	}

	public SVNRevision.Number getLastChangedRevision() {
		return (SVNRevision.Number)JhlConverter.convert(_d.getLastChangedRevision());
	}

	public Date getLastChangedDate() {
		return _d.getLastChanged();
	}

	public String getLastCommitAuthor() {
		return _d.getLastAuthor();
	}

	public String getPath() {
		return _d.getPath();
	}

    public long getSize() {
        return _d.getSize();
    }

    public Date getLastChanged() {
        return _d.getLastChanged();
    }

}
