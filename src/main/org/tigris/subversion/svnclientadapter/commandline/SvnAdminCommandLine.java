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
	void create(String path, String repositoryType) throws CmdLineException {
		notificationHandler.setCommand(ISVNNotifyListener.Command.CREATE_REPOSITORY);
		CmdArguments args = new CmdArguments();
		args.add("create");
		if (repositoryType != null) {
			// repository type is for svnadmin >= 1.1
			args.add("--fs-type");
			args.add(repositoryType);
		}
		args.add(path);
		execVoid(args);
	}
	
}
