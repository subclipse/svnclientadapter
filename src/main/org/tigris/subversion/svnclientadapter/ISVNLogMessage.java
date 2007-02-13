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
 * An interface defining a single subversion commit with log message, 
 * author, date and paths changed within the commit.
 * 
 * @author Philip Schatz <a href="mailto:schatzp@purdue.edu">schatzp@purdue.edu</a>
 */
public interface ISVNLogMessage {

    /**
     * Returns the revision number
     * @return the revision number
     */
	public abstract SVNRevision.Number getRevision();

    /**
     * Returns the author of the commit
     * @return the author of the commit
     */
	public abstract String getAuthor();

    /**
     * Returns the date of the commit
     * @return the date of the commit
     */
	public abstract Date getDate();

    /**
     * Return the log message text
     * @return the log message text
     */
	public abstract String getMessage();
    
    /**
     * Returns the changes items by this commit
     * @return the changes items by this commit
     */
    public abstract ISVNLogMessageChangePath[] getChangedPaths();    
}