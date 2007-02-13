/*******************************************************************************
 * Copyright (c) 2006 Subclipse project and others.
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
