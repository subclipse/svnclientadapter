/*
 * Created on 10 juil. 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tigris.subversion.svnclientadapter.commandline;

import java.util.ArrayList;

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;

/**
 * Call svnadmin
 * 
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
public class SvnAdminCommandLine extends CommandLine {

	//Constructors
	SvnAdminCommandLine(String svnPath,CmdLineNotificationHandler notificationHandler) {
		super(svnPath,notificationHandler);
	}	
	
	/**
	 * Create a new, empty repository at path
	 * 
	 */
	void create(String path) throws CmdLineException {
		notificationHandler.setCommand(ISVNNotifyListener.Command.CREATE_REPOSITORY);
		ArrayList args = new ArrayList();
		args.add("create");
		args.add(path);
		execVoid(args);
	}
	
}
