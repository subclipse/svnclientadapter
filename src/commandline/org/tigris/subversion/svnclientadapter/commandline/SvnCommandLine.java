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

import java.io.InputStream;

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.commandline.parser.SvnOutputParser;

/**
 * <p>
 * Performs the gruntwork of calling "svn".
 * Is a bare-bones interface to using the Subversion commandline client.</p>
 *  
 * @author Philip Schatz (schatz at tigris)
 * @author C�dric Chabanois (cchabanois at no-log.org)
 * @author John M Flinchbaugh (john at hjsoft.com)
 */
public class SvnCommandLine extends CommandLine {
	protected String user;
	protected String pass;	
    protected SvnOutputParser svnOutputParser = new SvnOutputParser();
    protected long rev = SVNRevision.SVN_INVALID_REVNUM;
    protected boolean parseSvnOutput = false;
    protected String configDir = null;
    
    
	//Constructors
	SvnCommandLine(String svnPath,CmdLineNotificationHandler notificationHandler) {
		super(svnPath,notificationHandler);
	}	
	
    /**
     * 
     * @param revision
     * @return "HEAD" if revision is a null or empty string, return revision otherwise
     */
	protected static String validRev(String revision) {
		return (revision == null || "".equals(revision)) ? "HEAD" : revision;
	}	
	
	/**
	 * <p>
	 * Sets the username used by this client.</p>
	 * 
	 * @param username The username to use for authentication.
	 */
	void setUsername(String username) {
		user = username;
	}

	/**
	 * <p>
	 * Sets the password used by this client.</p>
	 * 
	 * @param password The password to use for authentication.
	 */
	void setPassword(String password) {
		pass = password;
	}	

    
    /**
     * set the directory from which user configuration files will be read
     */
    void setConfigDirectory(String dir) {
    	configDir = dir;
    }
    
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.commandline.CommandLine#version()
	 */
	String version() throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.UNDEFINED, false);
		return super.version();
	}
	
	/**
	 * <p>
	 * Adds an unversioned file into the repository.</p>
	 * 
	 * @param resource Local path of resource to add.
	 * @param recursive true if this is a directory
	 *   and its children should be traversed
	 *   recursively.
	 * @param force true if this is a directory that 
	 *   should be scanned even if it's already added
	 *	 to the repository
	 */
	String add(String path, boolean recursive, boolean force) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.ADD, true);
		CmdArguments args = new CmdArguments();
		args.add("add");
		if (!recursive)
			args.add("-N");
		if (force)
			args.add("--force");
		args.add(path);
		return execString(args,false);
	}

	/**
	 * <p>
	 * Output the content of specified file or URL.</p>
	 * 
	 * @param url Either the local path to a file, or URL
	 *   to print the contents of.
	 * @return An stream containing the contents of the file.
	 */
	InputStream cat(String url, String revision) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.CAT, false);
		CmdArguments args = new CmdArguments();
		args.add("cat");
		args.add("-r");
		args.add(validRev(revision));
		args.add(url);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
		return execInputStream(args);
	}

	/**
	 * <p>
	 * Send changes from your working copy to the 
	 * repository.</p>
	 *   
	 * @param path The local path to the folder(s)/file(s)
	 *   to commit.
	 * @param message The message associated with the
	 *   committed resources.
	 * @throws CmdLineException
	 */
	String checkin(String[] path, String message, boolean keepLocks) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.COMMIT, true);
		CmdArguments args = new CmdArguments();
		args.add("ci");
		if (keepLocks)
		    args.add("--no-unlock");
		args.addLogMessage(message);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
		        
        for (int i = 0; i < path.length;i++) {
        	args.add(path[i]);
        }
        
		return execString(args,false);
	}

	/**
	 * <p>
	 * Recursively clean up the working copy, 
	 * removing locks, resuming unfinished operations.</p>
	 * 
	 * @param path The local path to clean up.
	 */
	void cleanup(String path) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.CLEANUP, true);
		CmdArguments args = new CmdArguments();
		args.add("cleanup");
		args.add(path);
        args.addConfigInfo(this.configDir);        
		execVoid(args);
	}

	/**
	 * <p>
	 * Check out a working copy from a repository.</p>
	 *
	 * @param url The URL to check out from.
	 * @param destination The local directory to check out to.
	 * @param revision The revision to check out.
	 *   Defaults to <tt>"HEAD"</tt>.
	 * @param recursive true if subdirs should be checked out recursively.
	 * @throws CmdLineException
	 */
	String checkout(String url, String destination, String revision, boolean recursive)
		throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.CHECKOUT, true);
		CmdArguments args = new CmdArguments();
		args.add("co");
		args.add("-r");
		args.add(validRev(revision));
		args.add(url + "@" + validRev(revision));
		args.add(destination);
		
		if (!recursive)
			args.add("-N");
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
		return execString(args,false);
	}

	/**
	 * <p>
	 * Duplicate something in working copy or repos,
	 * remembering history.</p>
	 * 
	 * <p>
	 * <tt>src</tt> and <tt>dest</tt> can each be either a working copy (WC) path or URL.</p>
	 * <dl>
	 * <dt>WC -&gt; WC</dt>
	 * <dd>copy and schedule for addition (with history)</dd>
	 * 
	 * <dt>WC -&gt; URL</dt>
	 * <dd>immediately commit a copy of WC to URL</dd>
	 * 
	 * <dt>URL -&gt; WC</dt>
	 * <dd>check out URL into WC, schedule for addition</dd>
	 * 
	 * <dt>URL -&gt; URL</dt>
	 * <dd>complete server-side copy;  used to branch and tag</dd>
	 * </dl>
	 * 
	 * @param src Local path or URL to copy from.
	 * @param dest Local path or URL to copy to.
	 * @param message Commit message.
	 * @param revision Optional revision to copy from. 
	 * @param makeparents   <code>true</code> <=> Create parents first when copying from source url to dest url.
	 */
	void copy(String src, String dest, String message, String revision, boolean makeparents) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.COPY, true);        
		CmdArguments args = new CmdArguments();
		args.add("cp");
		if (revision != null) {
			args.add("-r");
			args.add(validRev(revision));
		}
		if (makeparents) {
		  args.add("--parents");
		}
		if (message != null)
			args.addLogMessage(message);
		args.add(src);
		args.add(dest);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);		
		execVoid(args);
	}

	/**
	 * <p>
	 * Duplicate a resource in local file system.</p>
	 * 
	 * @param src Local path to copy from.
	 * @param dest Local destination path.
	 * @throws CmdLineException
	 */
	void copy(String src, String dest) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.COPY, true);
		CmdArguments args = new CmdArguments();
		args.add("cp");
		args.add(src);
		args.add(dest);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		execVoid(args);
	}

	/**
	 * <p>
	 * Remove files and directories from version control.</p>
	 *   
	 * @param target Local path or URL to remove.
	 * @param message Associated message when deleting from
	 *   URL.
	 */
	String delete(String[] target, String message, boolean force) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.REMOVE, true);
		CmdArguments args = new CmdArguments();
		args.add("rm");
		if (message != null) {
			args.addLogMessage(message);
		}
        if (force) {
        	args.add("--force");
        }
		for (int i = 0;i < target.length;i++) {
			args.add(target[i]);
		}
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		return execString(args,false);
	}

	/**
	 * <p>
	 * Display the differences between two paths.</p>
	 * 
	 */
	InputStream diff(String oldPath, String oldRev, String newPath, String newRev, boolean recurse, 
			boolean ignoreAncestry, boolean noDiffDeleted, boolean force) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.DIFF, false);
		CmdArguments args = new CmdArguments();
		args.add("diff");
		if (newRev != null) {
			args.add("-r");
			if (newRev.equals("WORKING")) { // "WORKING" is not a valid revision argument at least in 0,35,1
				args.add(oldRev);
			} else {
				args.add(oldRev+":"+newRev);			
			}
		}
        if (!recurse) {
            args.add("-N");
        }
        if (!ignoreAncestry) {
        	args.add("--notice-ancestry");
        }
        if (noDiffDeleted) {
        	args.add("--no-diff-deleted");
        }
        if (force) {
        	args.add("--force");
        }
		args.add("--old");
		args.add(oldPath);
		args.add("--new");
		args.add(newPath);
        args.addConfigInfo(this.configDir);		        
		return execInputStream(args);
	}

	/**
	 * <p>
	 * export files and directories from remote URL.</p>
	 * 
	 */
	void export(String url, String path, String revision, boolean force) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.EXPORT, true);        
		CmdArguments args = new CmdArguments();
		args.add("export");
		args.add("-r");
		args.add(validRev(revision));
		args.add(url);
		args.add(path);
		if (force)
			args.add("--force");
        args.addConfigInfo(this.configDir);			
		execVoid(args);
	}

	/**
	 * <p>
	 * Commit an unversioned file or directory into the repository.</p>
     *
     * @param path Local path to import from.
	 * @param url Remote URL to import to.
	 * @param message commit message
	 */
	String importFiles(String path, String url, String message, boolean recursive)
		throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.IMPORT, true);
		CmdArguments args = new CmdArguments();
		args.add("import");
		args.add(path);
        args.add(url);
        if (!recursive) {
        	args.add("-N");
        }
		args.addLogMessage(message);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		return execString(args,false);
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
	String info(String[] target, String revision, String peg) throws CmdLineException {
        if (target.length == 0) {
            // otherwise we would do a "svn info" without args
            return ""; 
        }
        
        setCommand(ISVNNotifyListener.Command.INFO, false);
		CmdArguments args = new CmdArguments();
		args.add("info");
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
        if (revision != null) {
        	args.add("-r");
        	args.add(revision);
        }
        for (int i = 0;i < target.length;i++) {
        	if (peg == null)
        		args.add(target[i]);
        	else
        		args.add(target[i] + "@" + peg);
        }

		return execString(args,false);
	}

	/**
	 * <p>
	 * List directory entries of a URL.</p>
	 * 
	 * @param url Remote URL.
	 * @param revision Revision to use. can be <tt>null</tt>
	 *   Defaults to <tt>HEAD</tt>.
	 * @param recursive Should this operation recurse into sub-directories
	 */
	byte[] list(String url, String revision, boolean recursive) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.LS, false);
		CmdArguments args = new CmdArguments();
		args.add("list");
		if (recursive) {
			args.add("-R");
		}
		args.add("--xml");
		args.add("-r");
		args.add(revision);
		args.add(url);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);		
		return execBytes(args,false);
	}

	/**
	 * <p>
	 * Show the log messages for a set of revision(s) and/or file(s).</p>
	 * 
	 * @param target Local path or URL.
	 * @param revision Optional revision range to get log
	 *   messages from.
	 */
	byte[] log(String target, String revision, boolean stopOnCopy, long limit ) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.LOG, false);		
		CmdArguments args = new CmdArguments();
		args.add("log");
		args.add("-r");
		args.add(validRev(revision));
		args.add(target);
		args.add("--xml");
		if (stopOnCopy)
		    args.add("--stop-on-copy");
		if (limit > 0) {
		    args.add("--limit");
		    args.add(Long.toString(limit));
		}
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
        return execBytes(args, true);
	}
	
	/**
	 * <p>
	 * Show the log messages for a set of revision(s) and/or file(s).</p>
	 * <p> The difference to the methode log is the parameter -v
	 * 
	 * @param target Local path or URL.
         * @param paths list of paths relative to target, may be null 
	 * @param revision Optional revision range to get log
	 *   messages from.
	 */
	byte[] logVerbose(String target, String [] paths, String revision, boolean stopOnCopy, long limit ) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.LOG, false);		
        CmdArguments args = new CmdArguments();
        args.add("log");
        args.add("-r");
        args.add(validRev(revision));
        args.add(target);
        if (paths != null) {
            for (int i = 0; i < paths.length; i++) {
                args.add(paths[i]);
            }
        }
        args.add("--xml");
        args.add("-v");
        if (stopOnCopy)
            args.add("--stop-on-copy");
        if (limit > 0) {
            args.add("--limit");
            args.add(Long.toString(limit));
        }
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
        return execBytes(args, true);
	}

	/**
	 * <p>
	 * Create a new directory under revision control.</p>
	 * 
	 * @param url URL to create. (contains existing url, 
	 *   followed by "/newDirectoryName").
	 * @param message Commit message to send.
	 */
	void mkdir(String url, String message) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.MKDIR, true);
		CmdArguments args = new CmdArguments();
		args.add("mkdir");
		args.addLogMessage(message);
		args.add(url);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		execVoid(args);
	}
    
	void mkdir(String localPath) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.MKDIR, true);
		CmdArguments args = new CmdArguments();
		args.add("mkdir");
		args.add(localPath);
        args.addConfigInfo(this.configDir);        
		execVoid(args);
	}

	/**
	 * <p>
	 * Move/rename something in working copy or repository.</p>
	 * 
	 * <p>
	 * <tt>source</tt> and <tt>dest</tt> can both be working copy (WC) paths or URLs.</p>
	 * <dl>
	 * <dt>WC -&gt; WC</dt>
	 * <dd>move and schedule for addition (with history)</dd>
	 * <dt>URL -&gt; URL</dt>
	 * <dd>complete server-side rename.</dd>
	 * 
	 * @param source Local path or URL to move from.
	 * @param dest Local path or URL to move to.
	 * @param message Optional message to send with URL.
	 * @param force Perform move even if there are modifications to working copy
	 */
	String move(String source, String dest, String message, String revision, boolean force)
		throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.MOVE, true);            
		CmdArguments args = new CmdArguments();
		args.add("mv");
		args.add("-r");
		args.add(validRev(revision));
		args.add(source);
		args.add(dest);
		if (message != null) {
			args.addLogMessage(message);
		}
		if (force) {
			args.add("--force");
		}
		args.addAuthInfo(this.user, this.pass);				
        args.addConfigInfo(this.configDir);
		return execString(args,false);
	}

	/**
	 * <p>
	 * Print value of <tt>propName</tt> on files, dirs, or revisions.</p>
	 *
	 * @param Local path of resource.
	 * @param propName Property name whose value we wish to find.
	 */
	InputStream propget(String path, String propName) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.PROPGET, false);
		CmdArguments args = new CmdArguments();
		args.add("propget");
		args.add("--strict");
		args.add(propName);
		args.add(path);
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		return execInputStream(args);
	}

	/**
	 * <p>
	 * Print value of <tt>propName</tt> on files, dirs, or revisions.</p>
	 *
	 * @param Local path of resource.
	 * @param propName Property name whose value we wish to find.
	 */
	InputStream propget(String path, String propName, String revision, String peg) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.PROPGET, false);
        CmdArguments args = new CmdArguments();
		args.add("propget");
		args.add("--strict");
		args.add("-r");
		args.add(revision);
		args.add(propName);
		args.add(path + "@" + peg);
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		return execInputStream(args);
	}

	/**
	 * <p>
	 * Set <tt>propName</tt> to <tt>propVal</tt> on files or dirs.</p>
	 * 
	 * @param propName name of the property.
	 * @param propValue New value to set <tt>propName</tt> to.
	 * @param target Local path to resource.
	 */
	void propset(String propName, String propValue, String target, boolean recurse)
		throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.PROPSET, false);
		CmdArguments args = new CmdArguments();
		args.add("propset");
		if (recurse)
			args.add("-R");
		args.add(propName);
		args.add(propValue);
		args.add(target);
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		execVoid(args);
	}
    /**
     * List the properties for the given file or dir
     * 
     * @param target
     * @return String with the resource properties
     * @throws CmdLineException
     */
    String proplist(String target, boolean recurse) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.PROPLIST, false);
		CmdArguments args = new CmdArguments();
		args.add("proplist");
		if (recurse)
			args.add("-R");
		args.add(target);
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
        
		return execString(args,false);
    }
    
    /**
     * Remove <tt>propName</tt> from files, dirs. 
     * 
     * @param propName
     * @param target
     * @param recurse
     * @throws CmdLineException
     */
    void propdel(String propName, String target, boolean recurse) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.PROPDEL, true);
		CmdArguments args = new CmdArguments();
		args.add("propdel");
		if (recurse)
			args.add("-R");
		args.add(propName);
		args.add(target);	
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
        
        execVoid(args);
    }
    
	/**
	 * <p>
	 * Sets a binary file as the value of a property.</p>
	 * 
	 * @param propName name of the property.
	 * @param propFile Local path to binary file.
	 * @param target Local path to resource.
	 */
	void propsetFile(String propName, String propFile, String target, boolean recurse)
		throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.PROPSET, false);
		CmdArguments args = new CmdArguments();
		args.add("propset");
		if (recurse)
			args.add("-R");
		args.add(propName);
		args.add("-F");
		args.add(propFile);
		args.add(target);	
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
        
		execVoid(args);
	}

	/**
	 * <p>
	 * Restore pristine working copy file (undo all local edits)</p>
	 * 
	 * @param paths Local paths to revert.
	 * @param recursive <tt>true</tt> if reverting subdirectories. 
	 */
	String revert(String[] paths, boolean recursive) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.REVERT, true);
		CmdArguments args = new CmdArguments();
		args.add("revert");
		if (recursive)
			args.add("-R");
		for (int i = 0; i < paths.length;i++) {
			args.add(paths[i]);
		}
        args.addConfigInfo(this.configDir);        
		
		return execString(args,false);
	}

	/**
	 * Remove 'conflicted' state on working copy files or directories.
	 *
	 * @param paths
	 * @param recursive
	 * @throws CmdLineException
	 */
	void resolved(String[] paths, boolean recursive) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.RESOLVED, true);
		CmdArguments args = new CmdArguments();
		args.add("resolved");
		if (recursive)
			args.add("-R");
		for (int i = 0; i < paths.length;i++) {
			args.add(paths[i]);
		}
        args.addConfigInfo(this.configDir);		
		execVoid(args);		
	}

	/**
	 * <p>
	 * Print the status of working copy files and directories.</p>
	 *   
	 * @param path Local path of resource to get status of.
	 * @param allEntries if false, only interesting entries will be get (local mods and/or out-of-date).
	 * @param checkUpdates Check for updates on server.
	 */
	byte[] status(String path[], boolean descend, boolean allEntries, boolean checkUpdates, boolean ignoreExternals) throws CmdLineException {
        if (path.length == 0) {
            // otherwise we would do a "svn status" without args
            return new byte[0]; 
        }
        setCommand(ISVNNotifyListener.Command.STATUS, false);
		CmdArguments args = new CmdArguments();
		args.add("status");
        args.add("--xml");
        if (allEntries) {
            args.add("-v");
        }
		if (!descend) 
            args.add("-N");
        if (checkUpdates) {
            args.add("-u");
        }
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
		return execBytes(args, false);
	}

	/**
	 * <p>
	 * Bring changes from the repository into the working copy.</p>
	 * 
	 * @param path Local path to possibly update.
	 * @param revision Optional revision to update to.
	 */
	String update(String path, String revision) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.UPDATE, true);
		CmdArguments args = new CmdArguments();
		args.add("up");
		args.add("-r");
		args.add(validRev(revision));
		args.add(path);
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		return execString(args,false);
	}

	/**
	 * <p>
	 * Bring changes from the repository into the working copy.</p>
	 * 
	 * @param paths Local paths to possibly update.
	 * @param revision Optional revision to update to.
	 */
	String update(String[] paths, String revision) throws CmdLineException {
		StringBuffer pathsArg = new StringBuffer();
		for (int i = 0; i < paths.length; i++) {
			pathsArg.append(paths[i]);
			pathsArg.append(" ");
		}
        setCommand(ISVNNotifyListener.Command.UPDATE, true);
		CmdArguments args = new CmdArguments();
		args.add("up");
		args.add("-r");
		args.add(validRev(revision));
		args.add(pathsArg.toString());
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
	 * @return byte[] array containing the XML representation of annotate data
	 * @throws CmdLineException
	 */
	byte[] annotate(String path,String revisionStart, String revisionEnd) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.ANNOTATE, false);
		CmdArguments args = new CmdArguments();
		args.add("annotate");
		args.add("--xml");
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
		return execBytes(args,false);
	}

    /**
     * Update the working copy to mirror a new URL within the repository.
     */
    String switchUrl(String path, String url, String revision, boolean recurse) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.SWITCH, true);
        CmdArguments args = new CmdArguments();
        args.add("sw");
        args.add(url);
        args.add(path);
        if (!recurse)
            args.add("-N");                
        args.add("-r");
        args.add(validRev(revision));
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
        return execString(args,false);
    }

    /**
     * Update the working copy to point to a new repository URL.
     */
    String relocate(String from, String to, String path, boolean recurse) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.RELOCATE, false);
        CmdArguments args = new CmdArguments();
        args.add("sw");
        args.add("--relocate");
        if (!recurse)
            args.add("-N");
        args.add(from);
        args.add(to);
        args.add(path);
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
        return execString(args,false);
    }
    
    /**
     * Update the working copy to mirror a new URL within the repository.
     */
    String merge(String path1, String revision1, String path2, String revision2, String localPath, boolean force, boolean recurse, boolean dryRun, boolean ignoreAncestry) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.MERGE, true);
        CmdArguments args = new CmdArguments();
        args.add("merge");
        if (!recurse)
        	args.add("-N");
        if (force)
        	args.add("--force");
        if (ignoreAncestry)
        	args.add("--ignore-ancestry");
        if (dryRun)
        	args.add("--dry-run");
        if (path1.equals(path2)) {
        	args.add("-r");
        	args.add(validRev(revision1) + ":" + validRev(revision2));
        	args.add(path1);
        } else {
        	args.add(path1 + "@" + validRev(revision1));
        	args.add(path2 + "@" + validRev(revision2));
        }
        args.add(localPath);
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
        return execString(args,false);
    }
    
	/**
	 * <p>
	 * Set <tt>propName</tt> to <tt>propVal</tt> on revision <tt>revision</tt>.</p>
	 * 
	 * @param propName name of the property.
	 * @param propValue New value to set <tt>propName</tt> to.
	 * @param target Local path or URL to resource.
	 * @param force If the propset should be forced.
	 */
	void revpropset(String propName, String propValue, String target, String revision, boolean force)
		throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.PROPSET, false);
		CmdArguments args = new CmdArguments();
		args.add("propset");
		args.add(propName);
		
		args.add("--revprop");
		
		args.add(propValue);
		args.add(target);
		
		args.add("-r");
		args.add(revision);

		if (force)
			args.add("--force");
        args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);        
		execVoid(args);
	}

    /**
     * @param paths Represents either WC paths, or repository URIs.
     * @param comment The comment to use for the lock operation.
     * @param force Whether to include <code>--force</code> in the
     * command-line.
     */
    String lock(Object[] paths, String comment, boolean force)
        throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.LOCK, true);
		CmdArguments args = new CmdArguments();
		args.add("lock");
		if (force)
		    args.add("--force");
		if (comment != null && !comment.equals("")) {
		    args.add("-m");
		    args.add(comment);
		}
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
		        
        for (int i = 0; i < paths.length; i++) {
        	args.add(paths[i]);
        }
        
		return execString(args, false);
    }

    /**
     * @param paths Represents either WC paths, or repository URIs.
     * @param comment The comment to use for the lock operation.
     * @param force Whether to include <code>--force</code> in the
     * command-line.
     */
    String unlock(Object[] paths, boolean force) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.UNLOCK, true);
		CmdArguments args = new CmdArguments();
		args.add("unlock");
		if (force)
		    args.add("--force");
		args.addAuthInfo(this.user, this.pass);
        args.addConfigInfo(this.configDir);
		        
        for (int i = 0; i < paths.length;i++) {
        	args.add(paths[i]);
        }
        
		return execString(args,false);
    }

    /*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.commandline.CommandLine#notifyFromSvnOutput(java.lang.String)
	 */
	protected void notifyFromSvnOutput(String svnOutput) {
		this.rev = SVNRevision.SVN_INVALID_REVNUM;
		// we call the super implementation : handles logMessage and logCompleted
		super.notifyFromSvnOutput(svnOutput);

		if (parseSvnOutput) {
			// we parse the svn output
			CmdLineNotify notify = new CmdLineNotify() {
		
				public void onNotify(
						String path,
				        int action,
				        int kind,
				        String mimeType,
				        int contentState,
				        int propState,
				        long revision) {
					// we only call notifyListenersOfChange and logRevision
					// logMessage and logCompleted have already been called
					if (path != null) {
						notificationHandler.notifyListenersOfChange(path);
					}
					if (revision != SVNRevision.SVN_INVALID_REVNUM) {
						SvnCommandLine.this.rev = revision;
						notificationHandler.logRevision(revision, path);
					}
				}
				
			};
			
		
			try {
				svnOutputParser.addListener(notify);
				svnOutputParser.parse(svnOutput);
			} finally {
				svnOutputParser.removeListener(notify);			
			}
		}
		
	}
	
	/**
	 * We call the super implementation : handles logMessage and logCompleted.
	 * This method main reason is to provide subclasses way to call super.super.notifyFromSvnOutput()
	 * @param svnOutput
	 */
	protected void notifyMessagesFromSvnOutput(String svnOutput) {
		this.rev = SVNRevision.SVN_INVALID_REVNUM;
		// we call the super implementation : handles logMessage and logCompleted
		super.notifyFromSvnOutput(svnOutput);
	}
	
	/**
	 * set the command used and tell if the svn ouput is notification
	 * that must be parsed using SvnOutputParser <br>
	 * The result of commands like list, cat must not be parsed here because
	 * in these cases, the output is not notification
	 * @param command
	 * @param ouputIsNotification
	 */
	protected void setCommand(int command, boolean ouputIsNotification) {
		this.parseSvnOutput = ouputIsNotification;
		notificationHandler.setCommand(command);
	}
	
	/**
	 * get the revision notified for latest command. If an error occured, the value
	 * of revision must be ignored
	 * @return Returns the revision.
	 */
	public long getRevision() {
		return rev;
	}
    
}	
