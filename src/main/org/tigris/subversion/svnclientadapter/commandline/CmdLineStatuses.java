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
 * Statuses
 * 
 * @author Cédric Chabanois (cchabanois at no-log.org)
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
            String[] parts = StringUtils.split(infoLines,Helper.NEWLINE+Helper.NEWLINE);
            cmdLineInfoParts = new CmdLineInfoPart[parts.length];
            for (int i = 0; i < parts.length;i++) {
                cmdLineInfoParts[i] = new CmdLineInfoPart(parts[i]);
            }
        }        
        getStatuses();
    }
    
    CmdLineStatuses(CmdLineInfoPart[] cmdLineInfoParts, CmdLineStatusPart[] cmdLineStatusParts) {
        this.cmdLineInfoParts = cmdLineInfoParts;
        this.cmdLineStatusParts = cmdLineStatusParts;
        getStatuses();
    }

    private void getStatuses() {
        List statuses = new LinkedList();
        for (int i = 0; i < cmdLineInfoParts.length;i++) {
            CmdLineInfoPart cmdLineInfoPart = cmdLineInfoParts[i];
            
            // find the corresponding status
            CmdLineStatusPart cmdLineStatusPart = getCmdLineStatusPart(cmdLineInfoPart.getFile());
            if (!cmdLineInfoPart.isVersioned()) {
                boolean isIgnored;
                if (cmdLineStatusPart == null) {
                    // when resource is not managed and resource is not ignored, there is no
                    // corresponding cmdLineInfoPart 
                    isIgnored = false;
                } else {
                    isIgnored = cmdLineStatusPart.isIgnored(); 
                }
                statuses.add(new SVNStatusUnversioned(cmdLineInfoPart.getFile(),isIgnored));              
            } else {
                statuses.add(new CmdLineStatus(cmdLineStatusPart,cmdLineInfoPart)); 
            }
        }
        cmdLineStatuses = (ISVNStatus[])statuses.toArray(new ISVNStatus[statuses.size()]);        
    }

    /**
     * @return the statusPart corresponding to the given file or null if none found
     */
    private CmdLineStatusPart getCmdLineStatusPart(File file) {
        for (int j = 0;j < cmdLineStatusParts.length;j++) {
            if (file.equals(cmdLineStatusParts[j].getFile())) {
                return cmdLineStatusParts[j];             
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
