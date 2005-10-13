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
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNStatusUnversioned;
import org.tigris.subversion.svnclientadapter.StringUtils;

/**
 * Digests <code>status</code> and <code>info</code> information from
 * the command-line.
 * 
 * @author Cédric Chabanois (cchabanois at no-log.org)
 * @author Daniel Rall
 */
public class CmdLineStatuses {
    private CmdLineInfoPart[] cmdLineInfoParts;
    private CmdLineStatusPart[] cmdLineStatusParts;
    private ISVNStatus[] cmdLineStatuses;

    CmdLineStatuses(String infoLines, String statusLines) {
        if (statusLines.length() == 0) {
            cmdLineStatusParts = new CmdLineStatusPart[0];
        } else {
            String[] parts = StringUtils.split(statusLines,Helper.NEWLINE);
             cmdLineStatusParts = new CmdLineStatusPart[parts.length];
             for (int i = 0; i < parts.length;i++) {
                 cmdLineStatusParts[i] = new CmdLineStatusPart(parts[i]);
             }
        }

        if (infoLines.length() == 0) {
            cmdLineInfoParts = new CmdLineInfoPart[0]; 
        } else {
            String[] parts = CmdLineInfoPart.parseInfoParts(infoLines);
            cmdLineInfoParts = new CmdLineInfoPart[parts.length];
            for (int i = 0; i < parts.length;i++) {
                cmdLineInfoParts[i] = new CmdLineInfoPart(parts[i]);
            }
        }        
        this.cmdLineStatuses = buildStatuses();
    }
    
    CmdLineStatuses(CmdLineInfoPart[] cmdLineInfoParts,
                    CmdLineStatusPart[] cmdLineStatusParts) {
        this.cmdLineInfoParts = cmdLineInfoParts;
        this.cmdLineStatusParts = cmdLineStatusParts;
        this.cmdLineStatuses = buildStatuses();
    }

    /**
     * Procures status objects for the {@link #cmdLineStatuses}
     * instance field.
     */
    private ISVNStatus[] buildStatuses() {
        List statuses = new LinkedList();
        for (int i = 0; i < cmdLineStatusParts.length; i++) {
            CmdLineStatusPart cmdLineStatusPart = cmdLineStatusParts[i];
            File absPath = cmdLineStatusPart.getFile();
            if (cmdLineStatusPart == null || !cmdLineStatusPart.isManaged()) {
                boolean isIgnored = false;
                if (cmdLineStatusPart != null) {
                    isIgnored = cmdLineStatusPart.isIgnored();
                }
                statuses.add(new SVNStatusUnversioned(absPath, isIgnored));
            } else {
                CmdLineInfoPart cmdLineInfoPart =
                    getCorrespondingInfoPart(absPath);
                statuses.add(new CmdLineStatus(cmdLineStatusPart,
                                               cmdLineInfoPart));
            }
        }

        return (ISVNStatus [])
            statuses.toArray(new ISVNStatus[statuses.size()]);
    }

    /**
     * @param absPath The absolute path to an item which we have a
     * status for.
     * @return The info corresponding to the specified path, or
     * <code>null</code> if not found.
     */
    private CmdLineInfoPart getCorrespondingInfoPart(File absPath) {
        for (int i = 0; i < cmdLineInfoParts.length; i++) {
            if (absPath.equals(cmdLineInfoParts[i].getFile())) {
                return cmdLineInfoParts[i];
            }
        }
        return null;
    }

    public ISVNStatus get(int i) {
        return cmdLineStatuses[i];
    }

    public int size() {
        return cmdLineStatuses.length;
    }
    
    public ISVNStatus[] toArray() {
        return cmdLineStatuses;
    }
    
}
