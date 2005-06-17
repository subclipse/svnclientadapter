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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNRevision.Number;
import org.tmatesoft.svn.core.io.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNLogEntryPath;

/**
 *  
 */
public class JavaSvnLogMessage implements ISVNLogMessage {
    private SVNLogEntry svnLogEntry;

    private ISVNLogMessageChangePath[] changedPaths;

    public JavaSvnLogMessage(SVNLogEntry svnLogEntry) {
        this.svnLogEntry = svnLogEntry;
    }

    private static ISVNLogMessageChangePath createChangePath(
            SVNLogEntryPath svnPath) {
        String path = svnPath.getPath();
        long copySrcRevision = svnPath.getCopyRevision();
        
        String copySrcPath = svnPath.getCopyPath();
        /** 'A'dd, 'D'elete, 'R'eplace, 'M'odify */
        char action = svnPath.getType();
        return new SVNLogMessageChangePath(path, JavaSvnConverter.convertRevisionNumber(copySrcRevision), copySrcPath, action);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getRevision()
     */
    public Number getRevision() {
        return JavaSvnConverter.convertRevisionNumber(svnLogEntry.getRevision());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getAuthor()
     */
    public String getAuthor() {
        return svnLogEntry.getAuthor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getDate()
     */
    public Date getDate() {
        return svnLogEntry.getDate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getMessage()
     */
    public String getMessage() {
        return svnLogEntry.getMessage();
    }

    /**
     * @return The value of {@link #getMesssage()}.
     */
    public String toString() {
        return getMessage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getChangedPaths()
     */
    public ISVNLogMessageChangePath[] getChangedPaths() {
        if (changedPaths == null) {
            Map paths = svnLogEntry.getChangedPaths();
            List changedPathsList = new LinkedList();
            if (paths != null) {
                for (Iterator keys = paths.keySet().iterator(); keys.hasNext();) {
                    String path = (String) keys.next();
                    SVNLogEntryPath svnPath = (SVNLogEntryPath) paths.get(path);
                    changedPathsList.add(createChangePath(svnPath));
                }
            }
            changedPaths = (ISVNLogMessageChangePath[]) changedPathsList
                    .toArray(new ISVNLogMessageChangePath[changedPathsList.size()]);
        }
        return changedPaths;
    }

}
