/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.InputStream;
import java.util.ArrayList;

import org.tigris.subversion.javahl.Notify;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.commandline.parser.SvnOutputParser;

/**
 * <p>
 * Performs the gruntwork of calling "svn".
 * Is a bare-bones interface to using the Subversion commandline client.</p>
 *  
 * @author Philip Schatz (schatz at tigris)
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
public class SvnCommandLine extends CommandLine {
	private static String user;
	private static String pass;	
    private SvnOutputParser svnOutputParser = new SvnOutputParser();
    private long revision = Revision.SVN_INVALID_REVNUM;
    private boolean parseSvnOutput = false;
    
    
	//Constructors
	SvnCommandLine(String svnPath,CmdLineNotificationHandler notificationHandler) {
		super(svnPath,notificationHandler);
	}	
	
    /**
     * 
     * @param revision
     * @return "HEAD" if revision is a null or empty string, return revision otherwise
     */
	private static String validRev(String revision) {
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

	
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.commandline.CommandLine#version()
	 */
	String version() throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.UNDEFINED, false);
		String result = super.version();
		return result;
	}
	/**
	 * <p>
	 * Adds an unversioned file into the repository.</p>
	 * 
	 * @param resource Local path of resource to add.
	 * @param recursive true if this is a directory
	 *   and its children should be traversed
	 *   recursively.
	 */
	String add(String path, boolean recursive) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.ADD, true);
		ArrayList args = new ArrayList();
		args.add("add");
		if (!recursive)
			args.add("-N");
		args.add(path);
		return execString(args,false);
	}

	private ArrayList addAuthInfo(ArrayList arguments) {
		if (user != null && user.length() > 0) {
			arguments.add("--username");
			arguments.add(user);
        }

        if (pass != null && pass.length() > 0) {
			arguments.add("--password");
			arguments.add(pass);
		}

		arguments.add("--non-interactive");

		return arguments;
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
		ArrayList args = new ArrayList();
		args.add("cat");
		args.add("-r");
		args.add(validRev(revision));
		args.add(url);
		addAuthInfo(args);
		
		Process proc =
			execProcess(args);

		InputStream content = proc.getInputStream();
		return content;
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
	String checkin(String[] path, String message) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.COMMIT, true);
		ArrayList args = new ArrayList();
		args.add("ci");
		args.add("-m");
		args.add(message);
		addAuthInfo(args);
		        
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
		ArrayList args = new ArrayList();
		args.add("cleanup");
		args.add(path);
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
		ArrayList args = new ArrayList();
		args.add("co");
		args.add("-r");
		args.add(validRev(revision));
		args.add(url);
		args.add(destination);
		
		if (!recursive)
			args.add("-N");
		addAuthInfo(args);

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
	 */
	void copy(String src, String dest, String message, String revision) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.COPY, true);        
		ArrayList args = new ArrayList();
		args.add("cp");
		args.add("-r");
		args.add(validRev(revision));
		args.add("-m");
		args.add(message);
		args.add(src);
		args.add(dest);
		addAuthInfo(args);
		
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
		ArrayList args = new ArrayList();
		args.add("cp");
		args.add(src);
		args.add(dest);
		addAuthInfo(args);
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
		ArrayList args = new ArrayList();
		args.add("rm");
		if (message != null) {
			args.add("-m");
			args.add(message);
		}
        if (force) {
        	args.add("--force");
        }
		for (int i = 0;i < target.length;i++) {
			args.add(target[i]);
		}
        addAuthInfo(args);
        
		return execString(args,false);
	}

	/**
	 * <p>
	 * Display the differences between two paths.</p>
	 * 
	 */
	InputStream diff(String oldPath, String oldRev, String newPath, String newRev, boolean recurse)
		throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.DIFF, false);
		ArrayList args = new ArrayList();
		args.add("diff");
		args.add("-r");
		if (newRev.equals("WORKING")) { // "WORKING" is not a valid revision argument at least in 0,35,1
			args.add(oldRev);
		} else {
			args.add(oldRev+":"+newRev);			
		}
		args.add("--old");
		args.add(oldPath);
		args.add("--new");
		args.add(newPath);
		        
		Process proc = execProcess(args);
  
  		InputStream content = proc.getInputStream();
		return content;
	}

	/**
	 * <p>
	 * export files and directories from remote URL.</p>
	 * 
	 */
	void export(String url, String path, String revision, boolean force) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.EXPORT, true);        
		ArrayList args = new ArrayList();
		args.add("export");
		args.add("-r");
		args.add(validRev(revision));
		args.add(url);
		args.add(path);
		if (force)
			args.add("--force");
			
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
		ArrayList args = new ArrayList();
		args.add("import");
		args.add(path);
        args.add(url);
        if (!recursive) {
        	args.add("-N");
        }
		args.add("-m");
		args.add(message);
		addAuthInfo(args);
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
	 * @return
	 */
	String info(String[] target) throws CmdLineException {
        if (target.length == 0) {
            // otherwise we would do a "svn info" without args
            return ""; 
        }
        
        setCommand(ISVNNotifyListener.Command.INFO, false);
		ArrayList args = new ArrayList();
		args.add("info");

        for (int i = 0;i < target.length;i++) {
            args.add(target[i]);
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
	 */
	String list(String url, String revision) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.LS, false);
		ArrayList args = new ArrayList();
		args.add("list");
		args.add("-v");
		args.add("-r");
		args.add(revision);
		args.add(url);
		addAuthInfo(args);
		
		return execString(args,false);
	}

	/**
	 * <p>
	 * Show the log messages for a set of revision(s) and/or file(s).</p>
	 * 
	 * @param target Local path or URL.
	 * @param revision Optional revision range to get log
	 *   messages from.
	 */
	String log(String target, String revision) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.LOG, false);		
		ArrayList args = new ArrayList();
		args.add("log");
		args.add("-r");
		args.add(validRev(revision));
		args.add(target);
		args.add("--xml");
        args.add("-v");
		addAuthInfo(args);

        return execString(args,true);
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
		ArrayList args = new ArrayList();
		args.add("mkdir");
		args.add("-m");
		args.add(message);
		args.add(url);
		addAuthInfo(args);
		execVoid(args);
	}
    
	void mkdir(String localPath) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.MKDIR, true);
		ArrayList args = new ArrayList();
		args.add("mkdir");
		args.add(localPath);
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
		ArrayList args = new ArrayList();
		args.add("mv");
		args.add("-r");
		args.add(validRev(revision));
		args.add(source);
		args.add(dest);
		if (message != null) {
			args.add("-m");
			args.add(message);
		}
		if (force) {
			args.add("--force");
		}
		addAuthInfo(args);				
	
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
		ArrayList args = new ArrayList();
		args.add("propget");
		args.add(propName);
		args.add(path);
        Process proc =
			execProcess(args);
		return proc.getInputStream();
	}



	/**
	 * <p>
	 * Set <tt>propName</tt> to <tt>propVal</tt> on files, dirs, or revisions.</p>
	 * 
	 * @param propName name of the property.
	 * @param propValue New value to set <tt>propName</tt> to.
	 * @param target Local path to resource.
	 */
	void propset(String propName, String propValue, String target, boolean recurse)
		throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.PROPSET, false);
		ArrayList args = new ArrayList();
		args.add("propset");
		if (recurse)
			args.add("-R");
		args.add(propName);
		args.add(propValue);
		args.add(target);        
		execVoid(args);
	}
    
    /**
     * List the properties for the given file or dir
     * 
     * @param target
     * @return
     * @throws CmdLineException
     */
    String proplist(String target, boolean recurse) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.PROPLIST, false);
		ArrayList args = new ArrayList();
		args.add("proplist");
		if (recurse)
			args.add("-R");
		args.add(target);
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
		ArrayList args = new ArrayList();
		args.add("propdel");
		if (recurse)
			args.add("-R");
		args.add(propName);
		args.add(target);	
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
		ArrayList args = new ArrayList();
		args.add("propset");
		if (recurse)
			args.add("-R");
		args.add(propName);
		args.add("-F");
		args.add(propFile);
		args.add(target);	
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
		ArrayList args = new ArrayList();
		args.add("revert");
		if (recursive)
			args.add("-R");
		for (int i = 0; i < paths.length;i++) {
			args.add(paths[i]);
		}
		
		return execString(args,false);
	}

	/**
	 * Remove 'conflicted' state on working copy files or directories.
	 *
	 * @param paths
	 * @param recursive
	 * @return
	 * @throws CmdLineException
	 */
	String resolved(String[] paths, boolean recursive) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.RESOLVED, true);
		ArrayList args = new ArrayList();
		args.add("resolved");
		if (recursive)
			args.add("-R");
		for (int i = 0; i < paths.length;i++) {
			args.add(paths[i]);
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
	String status(String path[], boolean descend, boolean allEntries, boolean checkUpdates) throws CmdLineException {
        if (path.length == 0) {
            // otherwise we would do a "svn status" without args
            return ""; 
        }
        setCommand(ISVNNotifyListener.Command.STATUS, false);
		ArrayList args = new ArrayList();
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
		
        for (int i = 0; i < path.length;i++) { 
            args.add(path[i]);
        }
		
        addAuthInfo(args);      
		return execString(args,false);
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
		ArrayList args = new ArrayList();
		args.add("up");
		args.add("-r");
		args.add(validRev(revision));
		args.add(path);
		addAuthInfo(args);
		return execString(args,false);
	}

	/**
	 * Output the content of specified files or URLs with revision and 
	 * author information in-line.
	 * @param path
	 * @param revisionStart
	 * @param revisionEnd
	 * @return
	 * @throws CmdLineException
	 */
	String annotate(String path,String revisionStart, String revisionEnd) throws CmdLineException {
		setCommand(ISVNNotifyListener.Command.ANNOTATE, false);
		ArrayList args = new ArrayList();
		args.add("annotate");
		args.add(path);
		args.add("-r");
		args.add(validRev(revisionStart)+":"+validRev(revisionEnd));
		addAuthInfo(args);
		return execString(args,false);
	}

    /**
     * Update the working copy to mirror a new URL within the repository.
     */
    String switchUrl(String path, String url, String revision) throws CmdLineException {
        setCommand(ISVNNotifyListener.Command.SWITCH, true);
        ArrayList args = new ArrayList();
        args.add("sw");
        args.add(url);
        args.add(path);
        args.add("-r");
        args.add(validRev(revision));
        addAuthInfo(args);
        return execString(args,false);
    }
    
	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.commandline.CommandLine#notifyFromSvnOutput(java.lang.String)
	 */
	protected void notifyFromSvnOutput(String svnOutput) {
		this.revision = Revision.SVN_INVALID_REVNUM;
		// we call the super implementation : handles logMessage and logCompleted
		super.notifyFromSvnOutput(svnOutput);

		if (parseSvnOutput) {
			// we parse the svn output
			Notify notify = new Notify() {
		
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
					if (revision != Revision.SVN_INVALID_REVNUM) {
						SvnCommandLine.this.revision = revision;
						notificationHandler.logRevision(revision);
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
	 * set the command used and tell if the svn ouput is notification
	 * that must be parsed using SvnOutputParser <br>
	 * The result of commands like list, cat must not be parsed here because
	 * in these cases, the output is not notification
	 * @param command
	 * @param ouputIsNotification
	 */
	private void setCommand(int command, boolean ouputIsNotification) {
		this.parseSvnOutput = ouputIsNotification;
		notificationHandler.setCommand(command);
	}
	
	/**
	 * get the revision notified for latest command. If an error occured, the value
	 * of revision must be ignored
	 * @return Returns the revision.
	 */
	public long getRevision() {
		return revision;
	}
    
}	
