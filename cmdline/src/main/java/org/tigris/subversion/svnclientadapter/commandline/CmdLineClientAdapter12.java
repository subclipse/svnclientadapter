/*******************************************************************************
 * Copyright (c) 2006 svnClientAdapter project and others.
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

import org.tigris.subversion.svnclientadapter.ISVNAnnotations;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.utils.StringUtils;

/**
 * Implements functions compatible with svn client version 1.2 or earlier
 *
 */
public class CmdLineClientAdapter12 extends CmdLineClientAdapter {

    private static boolean availabilityCached = false;
    private static boolean available;

    /**
     * Constructor
     * @param notificationHandler
     */
    public CmdLineClientAdapter12(CmdLineNotificationHandler notificationHandler)
    {
    	super(notificationHandler,
				new SvnCommandLine12("svn", notificationHandler),
				new SvnMultiArgCommandLine("svn", notificationHandler),
				new SvnAdminCommandLine("svnadmin", notificationHandler));
    }

	public static boolean isAvailable() {
		// availabilityCached flag must be reset if location of client changes
		if (!availabilityCached) {
			// this will need to be fixed when path to svn will be customizable
			SvnCommandLine cmd = new SvnCommandLine("svn", new CmdLineNotificationHandler());
			try {
				cmd.version();
	    		available = true;
			} catch (Exception e) {
				available = false;
			}
			availabilityCached = true;
		}
		return available;
	}
	
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getStatus(java.io.File, boolean, boolean, boolean)
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll, boolean contactServer) throws SVNClientException {
        notImplementedYet();
        return null;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapter#getStatus(java.io.File, boolean, boolean)
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll) throws SVNClientException {
    	return super.getStatus(path, descend, getAll, false);
	}

    protected CmdLineStatusPart[] getCmdStatuses(String[] paths, boolean descend, boolean getAll, boolean contactServer, boolean ignoreExternals) throws CmdLineException
    {
    	//Beware! the contactServer parameter is ignored, always treated as false.
    	if (paths.length == 0) {
    		return new CmdLineStatusPart[0];
    	}
    	String statusLinesString = ((SvnCommandLine12) _cmd).statusByStdout(paths, descend, getAll, false, ignoreExternals);
        String[] parts = StringUtils.split(statusLinesString,Helper.NEWLINE);
        CmdLineStatusPart[] cmdLineStatusParts = new CmdLineStatusPart[parts.length];
        for (int i = 0; i < parts.length;i++) {
            cmdLineStatusParts[i] = new CmdLineStatusPart.CmdLineStatusPartFromStdout(parts[i]);
        }
        return cmdLineStatusParts;
    }
 
	protected ISVNAnnotations annotate(String target, SVNRevision revisionStart, SVNRevision revisionEnd) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.ANNOTATE);
            if(revisionStart == null)
                revisionStart = new SVNRevision.Number(1);
            if(revisionEnd == null)
                revisionEnd = SVNRevision.HEAD;

            String annotations = ((SvnCommandLine12) _cmd).annotateByStdout(target,toString(revisionStart),toString(revisionEnd));
            
            return CmdLineAnnotations.createFromStdOut(annotations,Helper.NEWLINE);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}
}
