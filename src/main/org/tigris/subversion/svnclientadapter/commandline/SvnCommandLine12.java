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

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;

/**
 * SvnCommandLine subclass providing features compatible with version 1.2 of svn client.
 *  
 * @author Martin Letenay
 */
public class SvnCommandLine12 extends SvnCommandLine {

	//Constructors
	SvnCommandLine12(String svnPath,CmdLineNotificationHandler notificationHandler) {
		super(svnPath,notificationHandler);
	}	

	/**
	 * info: Display info about a resource.
	 * usage: info [PATH [PATH ... ]]
	 *
	 *   Print information about PATHs.
	 *
	 * Valid options:
	 *   --targets arg            : pass contents of file ARG as additional args
	 *   -R [--recursive]         : descend recursively
	 * 
	 * @param path
	 * @return String with the info call result
	 */
	String info(String[] target) throws CmdLineException {
        if (target.length == 0) {
            // otherwise we would do a "svn info" without args
            return ""; 
        }
        
        setCommand(ISVNNotifyListener.Command.INFO, false);
        CmdArguments args = new CmdArguments();
		args.add("info");
        args.addConfigInfo(this.configDir);
        for (int i = 0;i < target.length;i++) {
            args.add(target[i]);
        }

		return execString(args,false);
	}

	/**
	 * <p>
	 * Print the status of working copy files and directories.</p>
	 *   
	 * @param path Local path of resource to get status of.
	 * @param allEntries if false, only interesting entries will be get (local mods and/or out-of-date).
	 * @param checkUpdates Check for updates on server.
	 */
	String statusByStdout(String path[], boolean descend, boolean allEntries, boolean checkUpdates, boolean ignoreExternals) throws CmdLineException {
        if (path.length == 0) {
            // otherwise we would do a "svn status" without args
            return ""; 
        }
        setCommand(ISVNNotifyListener.Command.STATUS, false);
        CmdArguments args = new CmdArguments();
		args.add("status");
        args.add("-v");
        if (!allEntries) {
            args.add("-q");
        }
		if (!descend) 
            args.add("-N");
		if (checkUpdates)
			args.add("-u");
        if (allEntries) {
        	args.add("--no-ignore"); // disregard default and svn:ignore property ignores
        }
        if (ignoreExternals) {
        	args.add("--ignore-externals");
        }
		
        for (int i = 0; i < path.length;i++) { 
            args.add(path[i]);
        }
		
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
		return execString(args,false);
	}

	/**
	 * Output the content of specified files or URLs with revision and 
	 * author information in-line.
	 * @param path
	 * @param revisionStart
	 * @param revisionEnd
	 * @return String with the annotate data
	 * @throws CmdLineException
	 */
	String annotateByStdout(String path,String revisionStart, String revisionEnd) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.ANNOTATE, false);
		CmdArguments args = new CmdArguments();
		args.add("annotate");
		args.add("-r");
		if ((revisionStart != null) && (revisionStart.length() > 0))
		{
			args.add(validRev(revisionStart) + ":" + validRev(revisionEnd));	
		}
		else
		{
			args.add(validRev(revisionEnd));			
		}
		args.add(path);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
		return execString(args,false);
	}

}
