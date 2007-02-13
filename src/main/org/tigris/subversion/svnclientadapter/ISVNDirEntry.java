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
package org.tigris.subversion.svnclientadapter;
import java.util.Date;

/**
 * An interface describing subversion directory entry.
 * (E.g. a record returned by call to svn list)
 * 
 * @author Cédric Chabanois
 */
public interface ISVNDirEntry {

    /**
     * @return the pathname of the entry
     */
	String getPath();

    /**
     * @return the date of the last change
     */
	Date getLastChangedDate();

    /**
     * @return the revision number of the last change
     */
	SVNRevision.Number getLastChangedRevision();

    /**
     * @return true if the item has properties managed by subversion
     */
	boolean getHasProps();

    /**
     * @return the name of the author of the last change
     */
	String getLastCommitAuthor();

    /**
     * @return the kind of the node (directory or file)
     */
	SVNNodeKind getNodeKind();

    /**
     * @return length of file text, or 0 for directories
     */
	long getSize();
}
