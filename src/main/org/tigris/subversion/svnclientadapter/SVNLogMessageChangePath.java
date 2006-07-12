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

/**
 * A generic implementation of the {@link ISVNLogMessageChangePath} interface.
 * 
 */
public class SVNLogMessageChangePath implements ISVNLogMessageChangePath
{
    /** Path of commited item */
    private String path;

    /** Source revision of copy (if any). */
    private SVNRevision.Number copySrcRevision;

    /** Source path of copy (if any). */
    private String copySrcPath;

    /** 'A'dd, 'D'elete, 'R'eplace, 'M'odify */
    private char action;

    /**
     * Constructor
     * @param path
     * @param copySrcRevision
     * @param copySrcPath
     * @param action
     */
	public SVNLogMessageChangePath(String path, SVNRevision.Number copySrcRevision, String copySrcPath, char action)
    {
        this.path = path;
        this.copySrcRevision = copySrcRevision;
        this.copySrcPath = copySrcPath;
        this.action = action;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath#getPath()
     */
    public String getPath()
    {
        return path;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath#getCopySrcRevision()
     */
    public SVNRevision.Number getCopySrcRevision()
    {
    	return copySrcRevision;    
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath#getCopySrcPath()
     */
    public String getCopySrcPath()
    {
        return copySrcPath;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath#getAction()
     */
    public char getAction()
    {
        return action;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return getPath();
    }
}
