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

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.SVNRevision.Number;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNStatus;
import org.tmatesoft.svn.util.TimeUtil;

/**
 * adapter : convert from javasvn SVNStatus to ISVNStatus
 *  
 * @author Cédric Chabanois (cchab at tigris.org)
 */
public class JavaSvnStatus implements ISVNStatus {
    private SVNUrl url;
    private long lastChangedRevision;
    private Date lastChangedDate;
    private String lastCommitAuthor;
    private SVNStatusKind textStatus;
    private SVNStatusKind propStatus;
    private SVNStatusKind repositoryTextStatus;
    private SVNStatusKind repositoryPropStatus;
    private long revision;
    private File file;
    private SVNNodeKind nodeKind;
    private boolean copied;
    private SVNUrl urlCopiedFrom;
    private String conflictNew;
    private String conflictOld;
    private String conflictWorking;
    
    public JavaSvnStatus(File file, Map properties, SVNStatus status) {
        try {
            this.url = new SVNUrl((String)properties.get(SVNProperty.URL));
        } catch (MalformedURLException e) {
            this.url = null;
        }

        this.lastChangedRevision = SVNProperty.longValue((String) properties.get(SVNProperty.COMMITTED_REVISION));

        String lastChangedDateString = (String) properties.get(SVNProperty.COMMITTED_DATE);
        if (lastChangedDateString != null) {
            this.lastChangedDate = TimeUtil.parseDate(lastChangedDateString);
        } else {
            this.lastChangedDate = null;
        }
        
        this.lastCommitAuthor = (String) properties.get(SVNProperty.LAST_AUTHOR);        
        
        this.textStatus = JavaSvnConverter.convertStatusKind(status.getContentsStatus());

        this.propStatus = JavaSvnConverter.convertStatusKind(status.getPropertiesStatus());
        
        this.repositoryTextStatus = JavaSvnConverter.convertStatusKind(status.getRepositoryContentsStatus());
        this.repositoryPropStatus = JavaSvnConverter.convertStatusKind(status.getRepositoryPropertiesStatus());
        
        this.revision = SVNProperty.longValue((String) properties.get(SVNProperty.REVISION));        
        
        this.file = file;
        
        this.nodeKind = JavaSvnConverter.convertNodeKind((String)properties.get(SVNProperty.KIND));
        
        boolean copied = SVNProperty.booleanValue((String) properties.get(SVNProperty.COPIED));

        if (copied) {
            try {
                this.urlCopiedFrom = new SVNUrl((String)properties.get(SVNProperty.COPYFROM_URL));
            } catch (MalformedURLException e) {
                this.urlCopiedFrom = null;
            }
        }
        
        this.conflictNew = (String)properties.get(SVNProperty.CONFLICT_NEW);
        this.conflictOld = (String) properties.get(SVNProperty.CONFLICT_OLD);
        this.conflictWorking = (String) properties.get(SVNProperty.CONFLICT_WRK);
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrl()
     */
    public SVNUrl getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastChangedRevision()
     */
    public Number getLastChangedRevision() {
        return JavaSvnConverter.convertRevisionNumber(lastChangedRevision);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastChangedDate()
     */
    public Date getLastChangedDate() {
        return lastChangedDate;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getLastCommitAuthor()
     */
    public String getLastCommitAuthor() {
        return lastCommitAuthor;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getTextStatus()
     */
    public SVNStatusKind getTextStatus() {
        return textStatus;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRepositoryTextStatus()
     */
    public SVNStatusKind getRepositoryTextStatus() {
        return repositoryTextStatus;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getPropStatus()
     */
    public SVNStatusKind getPropStatus() {
        return propStatus;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRepositoryPropStatus()
     */
    public SVNStatusKind getRepositoryPropStatus() {
        return repositoryPropStatus;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getRevision()
     */
    public Number getRevision() {
        return JavaSvnConverter.convertRevisionNumber(revision);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getPath()
     */
    public String getPath() {
        return file.getAbsolutePath();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getFile()
     */
    public File getFile() {
        return file.getAbsoluteFile();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getNodeKind()
     */
    public SVNNodeKind getNodeKind() {
        return nodeKind;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#isCopied()
     */
    public boolean isCopied() {
        return copied;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getUrlCopiedFrom()
     */
    public SVNUrl getUrlCopiedFrom() {
        return urlCopiedFrom;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictNew()
     */
    public File getConflictNew() {
        if (conflictNew != null) {
            return new File(getFile().getParent(), conflictNew).getAbsoluteFile();    
        } else {
            return null;
        }
    }    

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictOld()
     */
    public File getConflictOld() {
        if (conflictOld != null) {
            return new File(getFile().getParent(), conflictOld).getAbsoluteFile();    
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNStatus#getConflictWorking()
     */
    public File getConflictWorking() {
        if (conflictWorking != null) {
            return new File(getFile().getParent(), conflictWorking).getAbsoluteFile();    
        } else {
            return null;
        }
    }

}
