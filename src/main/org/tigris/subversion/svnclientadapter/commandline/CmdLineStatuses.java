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
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNStatusUnversioned;

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

    CmdLineStatuses(String infoLines, CmdLineStatusPart[] cmdLineStatusParts) {
    	this.cmdLineStatusParts = cmdLineStatusParts;

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
                    isIgnored = SVNStatusKind.IGNORED.equals(cmdLineStatusPart.getTextStatus());
                }
                statuses.add(new SVNStatusUnversioned(absPath, isIgnored));
            } else {
                CmdLineInfoPart cmdLineInfoPart =
                    getCorrespondingInfoPart(absPath);
                if (cmdLineInfoPart != null) {
                	statuses.add(new CmdLineStatusComposite(cmdLineStatusPart,
                                               cmdLineInfoPart));
                }
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
