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

import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNScheduleKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.SVNRevision.Number;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.util.TimeUtil;

/**
 */
public class JavaSvnInfo implements ISVNInfo {
    private File file;

    private SVNUrl url;

    private String uuid;

    private SVNScheduleKind scheduleKind;

    private SVNNodeKind nodeKind;

    private String lastCommitAuthor;

    private long revision;

    private long lastChangedRevision;

    private Date lastChangedDate;

    private Date lastTextUpdate;

    private Date lastPropsUpdate;

    private boolean copied;

    private long copyRev;
    
    private SVNUrl urlCopiedFrom;

    public JavaSvnInfo(File file, Map properties) {
        this.file = file;

        String name = (String) properties.get(SVNProperty.NAME);

        try {
            this.url = new SVNUrl((String) properties.get(SVNProperty.URL));
        } catch (MalformedURLException e) {
            this.url = null;
        }

        this.uuid = (String) properties.get(SVNProperty.UUID);

        this.scheduleKind = SVNScheduleKind.NORMAL;
        String scheduleKindStr = (String) properties.get(SVNProperty.SCHEDULE);
        if (SVNProperty.SCHEDULE_ADD.equals(scheduleKindStr)) {
            this.scheduleKind = SVNScheduleKind.ADD;
        } else if (SVNProperty.SCHEDULE_DELETE.equals(scheduleKindStr)) {
            this.scheduleKind = SVNScheduleKind.DELETE;
        } else if (SVNProperty.SCHEDULE_REPLACE.equals(scheduleKindStr)) {
            this.scheduleKind = SVNScheduleKind.REPLACE;
        }

        this.nodeKind = SVNNodeKind.UNKNOWN;
        if (SVNProperty.KIND_DIR.equals(properties.get(SVNProperty.KIND))) {
            this.nodeKind = SVNNodeKind.DIR;
        } else if (SVNProperty.KIND_FILE.equals(properties
                .get(SVNProperty.KIND))) {
            this.nodeKind = SVNNodeKind.FILE;
        }

        this.lastCommitAuthor = (String) properties
                .get(SVNProperty.LAST_AUTHOR);

        this.revision = SVNProperty.longValue((String) properties
                .get(SVNProperty.REVISION));

        this.lastChangedRevision = SVNProperty.longValue((String) properties
                .get(SVNProperty.COMMITTED_REVISION));

        this.lastChangedDate = TimeUtil.parseDate((String) properties
                .get(SVNProperty.COMMITTED_DATE));
        this.lastTextUpdate = TimeUtil.parseDate((String) properties
                .get(SVNProperty.TEXT_TIME));
        this.lastPropsUpdate = TimeUtil.parseDate((String) properties
                .get(SVNProperty.PROP_TIME));
        this.copied = SVNProperty.booleanValue((String) properties
                .get(SVNProperty.COPIED));

        this.copyRev = SVNProperty.longValue((String) properties
                .get(SVNProperty.COPYFROM_REVISION));

        if (copied) {
            try {
                this.urlCopiedFrom = new SVNUrl((String) properties
                        .get(SVNProperty.COPYFROM_URL));
            } catch (MalformedURLException e) {
                this.urlCopiedFrom = null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getFile()
     */
    public File getFile() {
        return file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getUrl()
     */
    public SVNUrl getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getUuid()
     */
    public String getUuid() {
        return uuid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getRepository()
     */
    public SVNUrl getRepository() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getSchedule()
     */
    public SVNScheduleKind getSchedule() {
        return scheduleKind;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getNodeKind()
     */
    public SVNNodeKind getNodeKind() {
        return nodeKind;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastCommitAuthor()
     */
    public String getLastCommitAuthor() {
        return lastCommitAuthor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getRevision()
     */
    public Number getRevision() {
        return new SVNRevision.Number(revision);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastChangedRevision()
     */
    public Number getLastChangedRevision() {
        return new SVNRevision.Number(lastChangedRevision);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastChangedDate()
     */
    public Date getLastChangedDate() {
        return lastChangedDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastDateTextUpdate()
     */
    public Date getLastDateTextUpdate() {
        return lastTextUpdate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLastDatePropsUpdate()
     */
    public Date getLastDatePropsUpdate() {
        return lastPropsUpdate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#isCopied()
     */
    public boolean isCopied() {
        return copied;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getCopyRev()
     */
    public Number getCopyRev() {
        return new SVNRevision.Number(copyRev);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getCopyUrl()
     */
    public SVNUrl getCopyUrl() {
        return urlCopiedFrom;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLockCreationDate()
     */
    public Date getLockCreationDate() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLockOwner()
     */
    public String getLockOwner() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNInfo#getLockComment()
     */
    public String getLockComment() {
        // TODO Auto-generated method stub
        return null;
    }
}