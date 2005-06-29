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

import java.util.logging.Logger;

import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNStatus;

/**
 * Convert from javasvn types to subversion.svnclientadapter.* types
 * 
 * @author Cédric Chabanois
 */
public class JavaSvnConverter {
    private static Logger log = Logger.getLogger(JavaSvnConverter.class.getName());

    static final SVNStatusKind[] STATUS_CONVERTION_TABLE = new SVNStatusKind[0x11];

    static {
        STATUS_CONVERTION_TABLE[SVNStatus.NOT_MODIFIED] = SVNStatusKind.NORMAL;
        STATUS_CONVERTION_TABLE[SVNStatus.ADDED] = SVNStatusKind.ADDED;
        STATUS_CONVERTION_TABLE[SVNStatus.CONFLICTED] = SVNStatusKind.CONFLICTED;
        STATUS_CONVERTION_TABLE[SVNStatus.DELETED] = SVNStatusKind.DELETED;
        STATUS_CONVERTION_TABLE[SVNStatus.MERGED] = SVNStatusKind.MERGED;
        STATUS_CONVERTION_TABLE[SVNStatus.IGNORED] = SVNStatusKind.IGNORED;
        STATUS_CONVERTION_TABLE[SVNStatus.MODIFIED] = SVNStatusKind.MODIFIED;
        STATUS_CONVERTION_TABLE[SVNStatus.REPLACED] = SVNStatusKind.REPLACED;
        STATUS_CONVERTION_TABLE[SVNStatus.UNVERSIONED] = SVNStatusKind.UNVERSIONED;
        STATUS_CONVERTION_TABLE[SVNStatus.MISSING] = SVNStatusKind.MISSING;
        STATUS_CONVERTION_TABLE[SVNStatus.OBSTRUCTED] = SVNStatusKind.OBSTRUCTED;
    }

    public static SVNStatusKind convertStatusKind(int javaSvnStatus) {
        if (javaSvnStatus >= 0
                && javaSvnStatus < STATUS_CONVERTION_TABLE.length) {
            return STATUS_CONVERTION_TABLE[javaSvnStatus];
        } else {
            log.severe("unknown status kind :" + javaSvnStatus);
            return SVNStatusKind.NONE;
        }
    }

    public static SVNNodeKind convertNodeKind(
            org.tmatesoft.svn.core.io.SVNNodeKind tmateNodeKind) {
        if (tmateNodeKind == org.tmatesoft.svn.core.io.SVNNodeKind.DIR) {
            return SVNNodeKind.DIR;
        } else if (tmateNodeKind == org.tmatesoft.svn.core.io.SVNNodeKind.FILE) {
            return SVNNodeKind.FILE;
        } else if (tmateNodeKind == org.tmatesoft.svn.core.io.SVNNodeKind.NONE) {
            return SVNNodeKind.FILE;
        } else if (tmateNodeKind == org.tmatesoft.svn.core.io.SVNNodeKind.UNKNOWN) {
            return SVNNodeKind.UNKNOWN;
        } else {
            return SVNNodeKind.UNKNOWN;
        }
    }

    public static SVNNodeKind convertNodeKind(String javasvnNodeKind) {
        return convertNodeKind(org.tmatesoft.svn.core.io.SVNNodeKind
                .parseKind(javasvnNodeKind));
    }

    public static SVNRevision.Number convertRevisionNumber(long revision) {
        if (revision == -1) {
            return null;
        } else {
            return new SVNRevision.Number(revision);
        }
    }

}