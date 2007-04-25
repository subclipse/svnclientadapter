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
