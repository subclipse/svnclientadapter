/*******************************************************************************
 * Copyright (c) 2003, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.javahl;

import java.util.Date;

import org.tigris.subversion.javahl.DirEntry;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;

/**
 * A JavaHL based implementation of {@link ISVNDirEntry}.
 * Actually just an adapter from {@link org.tigris.subversion.javahl.DirEntry}
 *  
 * @author philip schatz
 */
public class JhlDirEntry implements ISVNDirEntry {

	private DirEntry _d;

	/**
	 * Constructor
	 * @param d
	 */
	public JhlDirEntry(DirEntry d) {
		super();
		_d = d;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getNodeKind()
	 */
	public SVNNodeKind getNodeKind() {
        return JhlConverter.convertNodeKind(_d.getNodeKind());
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getHasProps()
	 */
	public boolean getHasProps() {
		return _d.getHasProps();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getLastChangedRevision()
	 */
	public SVNRevision.Number getLastChangedRevision() {
		return (SVNRevision.Number)JhlConverter.convert(_d.getLastChangedRevision());
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getLastChangedDate()
	 */
	public Date getLastChangedDate() {
		return _d.getLastChanged();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getLastCommitAuthor()
	 */
	public String getLastCommitAuthor() {
		return _d.getLastAuthor();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getPath()
	 */
	public String getPath() {
		return _d.getPath();
	}

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getSize()
     */
    public long getSize() {
        return _d.getSize();
    }
}
