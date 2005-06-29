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
package org.tigris.subversion.svnclientadapter.javasvn;

import java.util.Date;

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision.Number;
import org.tmatesoft.svn.core.io.SVNDirEntry;

/**
 * 
 */
public class JavaSvnDirEntry implements ISVNDirEntry {
    private SVNDirEntry svnDirEntry;
    private String parent;
    
    public JavaSvnDirEntry(String parent, SVNDirEntry svnDirEntry) {
        this.svnDirEntry = svnDirEntry;
        this.parent = parent;
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getPath()
     */
    public String getPath() {
        if ("".equals(parent)) {
            return svnDirEntry.getName();
        } else {
            return parent+"/"+svnDirEntry.getName();
        }
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getLastChangedDate()
     */
    public Date getLastChangedDate() {
        return new Date(svnDirEntry.getDate().getTime() * 1000);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getLastChangedRevision()
     */
    public Number getLastChangedRevision() {
        return JavaSvnConverter.convertRevisionNumber(svnDirEntry.getRevision());
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getHasProps()
     */
    public boolean getHasProps() {
        return svnDirEntry.hasProperties();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getLastCommitAuthor()
     */
    public String getLastCommitAuthor() {
        return svnDirEntry.getAuthor();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getNodeKind()
     */
    public SVNNodeKind getNodeKind() {
        return JavaSvnConverter.convertNodeKind(svnDirEntry.getKind());
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNDirEntry#getSize()
     */
    public long getSize() {
        return svnDirEntry.size();
    }

    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getPath()+":"+getLastChangedRevision();
    }
}
