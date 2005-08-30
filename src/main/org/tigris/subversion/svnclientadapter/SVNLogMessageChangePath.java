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

import org.tigris.subversion.javahl.ChangePath;

/**
 * This class has been copied from javahl and modified a bit.
 * We cannot use original ChangePath because constructor visibility is package
 */
public class SVNLogMessageChangePath implements ISVNLogMessageChangePath
{
    public SVNLogMessageChangePath(ChangePath changePath)
    {
        this.path = changePath.getPath();
        this.copySrcPath = changePath.getCopySrcPath();
        this.action = changePath.getAction();
        this.copySrcRevision = null;
        if (changePath.getCopySrcRevision() != -1) {
            this.copySrcRevision = new SVNRevision.Number(changePath.getCopySrcRevision());	
        }
    }

	public SVNLogMessageChangePath(String path, SVNRevision.Number copySrcRevision, String copySrcPath, char action)
    {
        this.path = path;
        this.copySrcRevision = copySrcRevision;
        this.copySrcPath = copySrcPath;
        this.action = action;
    }

    /** Path of commited item */
    private String path;

    /** Source revision of copy (if any). */
    private SVNRevision.Number copySrcRevision;

    /** Source path of copy (if any). */
    private String copySrcPath;

    /** 'A'dd, 'D'elete, 'R'eplace, 'M'odify */
    private char action;

    /**
     * Retrieve the path to the commited item
     * @return  the path to the commited item
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Retrieve the copy source revision (if any)
     * @return  the copy source revision (if any)
     */
    public SVNRevision.Number getCopySrcRevision()
    {
    	return copySrcRevision;    
    }

    /**
     * Retrieve the copy source path (if any)
     * @return  the copy source path (if any)
     */
    public String getCopySrcPath()
    {
        return copySrcPath;
    }

    /**
     * Retrieve action performed
     * @return  action performed
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
