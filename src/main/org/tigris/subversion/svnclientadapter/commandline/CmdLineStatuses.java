/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
    	processExternalStatuses(cmdLineStatusParts);
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

    /**
     * Post-process svn:externals statuses.
     * commandline answer two sort of statuses on externals:
     * - when ignoreExternals is set to true during call to status(),
     *  the returned status has textStatus set to EXTERNAL, but the url is null.<br>
     * - when ignoreExternals is set to false during call to status(),
     *  besides the "external + null" status, the second status with url and all fields is returned too, 
     *  but this one has textStatus NORMAL.
     *  
     *  This methods unifies both statuses to be complete and has textStatus external.
     *  In case the first sort (when ignoreExternals true), the url is retrieved by call the info()
     */
    protected CmdLineStatusPart[] processExternalStatuses(CmdLineStatusPart[] statuses)
    {
    	//Collect indexes of external statuses
    	List externalStatusesIndexes = new ArrayList();
    	for (int i = 0; i < statuses.length; i++) {
    		if (SVNStatusKind.EXTERNAL.equals(statuses[i].getTextStatus())) {
    			externalStatusesIndexes.add(new Integer(i));
    		}
		}
    	
    	if (externalStatusesIndexes.isEmpty()) {
    		return statuses;
    	}
    	
    	//Check the "second" externals so their textStatus is actually external
    	for (Iterator iter = externalStatusesIndexes.iterator(); iter.hasNext();) {
    		int index = ((Integer) iter.next()).intValue();
    		CmdLineStatusPart aStatus = statuses[index];
			for (int i = 0; i < statuses.length; i++) {
				if ((statuses[i].getPath() != null) && (statuses[i].getPath().equals(aStatus.getPath()))) {
					statuses[i].setTextStatus(SVNStatusKind.EXTERNAL);
				}
			}
		}
    	
    	return statuses;
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
