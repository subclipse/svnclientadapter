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

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;

/**
 * <p>
 * Performs the gruntwork of calling "svn".
 * Is a bare-bones interface to using the Subversion commandline client.</p>
 *  
 * @author Philip Schatz (schatz at tigris)
 */
class CommandLine {
	/*
	 * These are the commands used to execute the methods listed
	 * below.
	 */
	private static String CMD_VERSION = "--version";
	private static String CMD_ADD = "add {0} {1}";
	private static String CMD_CAT = "cat -r {0} {1}";
	private static String CMD_CLEANUP = "cleanup {0}";
	private static String CMD_COMMIT = "ci {0} -m \"{1}\"";
	private static String CMD_COPY = "cp -r {0} -m \"{1}\" {2} {3}";
	private static String CMD_COPY_LOCAL = "cp {0} {1}";
	private static String CMD_CHECKOUT = "co -r {0} {1} {2}";
	private static String CMD_DELETE = "rm {0} {1} --force";
	private static String CMD_EXPORT = "export -r {0} {1} {2} {3}";
	private static String CMD_IMPORT = "import {0} {1} {2} -m \"{3}\"";
	private static String CMD_INFO = "info {0}";
	private static String CMD_LIST = "list -v -r {0} {1}";
	private static String CMD_LOG = "log -r {0} {1}";
	private static String CMD_MKDIR = "mkdir -m \"{0}\" {1}";
	private static String CMD_MKDIR_LOCAL = "mkdir {0}";
	private static String CMD_MOVE = "mv -r {0} {1} {2} {3} --force";
	private static String CMD_PROPGET = "propget {0} {1}";
	private static String CMD_PROPSET = "propset {0} \"{1}\" {2}";
	private static String CMD_PROPSET_FILE = "propset {0} -F \"{1}\" {2}";
	private static String CMD_REVERT = "revert {0} {1}";
	private static String CMD_STATUS = "status -v -N {0} {1}";
	private static String CMD_RECURSIVE_STATUS = "status -v {0}";

	private static String CMD_UPDATE = "up -r {0} {1}";
	private static String AUTH_INFO = " --username \"{0}\" --password \"{1}\" --non-interactive";

	private String CMD;
	private static String user;
	private static String pass;
	List listeners = new LinkedList();

	//Constructors
	CommandLine(String svnPath) {
		CMD = svnPath + ' ';
	}

	//Methods
	String version() throws CmdLineException {
		return execString(CMD_VERSION);
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
		String flags = (recursive) ? "" : "-N";
		return execString(MessageFormat.format(CMD_ADD, new String[] { flags, path }));
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
		Process proc =
			execProcess(
				MessageFormat.format(CMD_CAT, new String[] { validRev(revision), url })
					+ getAuthInfo());

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
	String checkin(String path, String message) throws CmdLineException {
		return execString(
			MessageFormat.format(CMD_COMMIT, new String[] { path, message }) + getAuthInfo());
	}

	/**
	 * <p>
	 * Recursively clean up the working copy, 
	 * removing locks, resuming unfinished operations.</p>
	 * 
	 * @param path The local path to clean up.
	 */
	void cleanup(String path) throws CmdLineException {
		execVoid(MessageFormat.format(CMD_CLEANUP, new String[] { path }));
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
		String flags = (recursive) ? "" : "-N";
		return execString(
			MessageFormat.format(
				CMD_CHECKOUT,
				new String[] { validRev(revision), url, destination, flags })
				+ getAuthInfo());
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
		execVoid(
			MessageFormat.format(CMD_COPY, new String[] { validRev(revision), message, src, dest })
				+ getAuthInfo());
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
		execVoid(MessageFormat.format(CMD_COPY_LOCAL, new String[] { src, dest }) + getAuthInfo());
	}

	/**
	 * <p>
	 * Remove files and directories from version control.</p>
	 *   
	 * @param target Local path or URL to remove.
	 * @param message Associated message when deleting from
	 *   URL.
	 */
	String delete(String target, String message) throws CmdLineException {
		String msg = (message == null) ? "" : "-m \"" + message + "\"";
		return execString(
			MessageFormat.format(CMD_DELETE, new String[] { msg, target }) + getAuthInfo());
	}

	/**
	 * <p>
	 * Display the differences between two paths.</p>
	 * 
	 */
	InputStream diff(String oldPath, String oldRev, String newPath, String newRev, boolean recurse)
		throws CmdLineException {
		String commandLine = " diff ";
		if (!"BASE".equals(oldRev) || !"WORKING".equals(newPath)) {
			commandLine += "-r " + oldRev;
			if (!"WORKING".equals(newRev))
				commandLine += ":" + newRev + " ";
		}
		commandLine += " --old " + oldPath;
		commandLine += " --new " + newPath;

		Process proc = execProcess(commandLine);
		InputStream content = proc.getInputStream();
		return content;
	}

	/**
	 * <p>
	 * export files and directories from remote URL.</p>
	 * 
	 */
	void export(String url, String path, String revision, boolean force) throws CmdLineException {
		execVoid(
			MessageFormat.format(
				CMD_EXPORT,
				new String[] { url, path, validRev(revision), (force) ? "--force" : "" }));
	}

	/**
	 * <p>
	 * Commit an unversioned file or directory into the repository.</p>
	 * 
	 * @param url Remote URL to import to.
	 * @param path Local path to import from.
	 * @param module Remote module name.
	 * @param message optional. can be null
	 */
	String importFiles(String url, String path, String module, String message)
		throws CmdLineException {
		return execString(
			MessageFormat.format(CMD_IMPORT, new String[] { url, path, module, message })
				+ getAuthInfo());
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
	String info(String path) throws CmdLineException {
		return execString(MessageFormat.format(CMD_INFO, new String[] { path }));
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
		return execString(
			MessageFormat.format(CMD_LIST, new String[] { revision, url }) + getAuthInfo());
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
		return execString(
			MessageFormat.format(CMD_LOG, new String[] { validRev(revision), target })
				+ getAuthInfo());
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
		execVoid(MessageFormat.format(CMD_MKDIR, new String[] { message, url }) + getAuthInfo());
	}
	void mkdir(String localPath) throws CmdLineException {
		execVoid(MessageFormat.format(CMD_MKDIR_LOCAL, new String[] { localPath }));
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
	 */
	String move(String source, String dest, String message, String revision)
		throws CmdLineException {
		String messageStr = (message == null) ? "" : "-m \"" + message + "\"";
		return execString(
			MessageFormat.format(
				CMD_MOVE,
				new String[] { validRev(revision), source, dest, messageStr })
				+ getAuthInfo());
	}

	/**
	 * <p>
	 * Print value of <tt>propName</tt> on files, dirs, or revisions.</p>
	 *
	 * @param Local path of resource.
	 * @param propName Property name whose value we wish to find.
	 */
	InputStream propget(String path, String propName) throws CmdLineException {
		Process proc =
			execProcess(MessageFormat.format(CMD_PROPGET, new String[] { propName, path }));
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
		execVoid(MessageFormat.format(CMD_PROPSET, new String[] { propName, propValue, target }));
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
		execVoid(
			MessageFormat.format(CMD_PROPSET_FILE, new String[] { propName, propFile, target }));
	}

	/**
	 * <p>
	 * Restore pristine working copy file (undo all local edits)</p>
	 * 
	 * @param paths Local paths to revert.
	 * @param recursive <tt>true</tt> if reverting subdirectories. 
	 */
	String revert(String paths, boolean recursive) throws CmdLineException {
		String recursiveFlag = (recursive) ? "-R" : "";
		return execString(MessageFormat.format(CMD_REVERT, new String[] { recursiveFlag, paths }));
	}

	/**
	 * <p>
	 * Print the status of working copy files and directories.</p>
	 *   
	 * @param path Local path of resource to get status of.
	 * @param checkUpdates Check for updates on server.
	 */
	String status(String path, boolean checkUpdates) throws CmdLineException {
		String flags = (checkUpdates ? "-u" : "");
		return execString(
			MessageFormat.format(CMD_STATUS, new String[] { flags, path }) + getAuthInfo());
	}

	/**
	 * <p>
	 * Obtain the status of a directory and its children
	 * recursively.</p>
	 * 
	 * @param path Local path of directory to use.
	 * @throws CmdLineException
	 */
	String recursiveStatus(String path) throws CmdLineException {
		return execString(
			MessageFormat.format(CMD_RECURSIVE_STATUS, new String[] { path }) + getAuthInfo());
	}

	/**
	 * <p>
	 * Bring changes from the repository into the working copy.</p>
	 * 
	 * @param path Local path to possibly update.
	 * @param revision Optional revision to update to.
	 */
	String update(String path, String revision) throws CmdLineException {
		return execString(
			MessageFormat.format(CMD_UPDATE, new String[] { validRev(revision), path })
				+ getAuthInfo());
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

	private Process execProcess(String svnCommand) throws CmdLineException {
		Runtime rt = Runtime.getRuntime();

		if (!listeners.isEmpty())
			logCommand(svnCommand);

		/* run the process */
		Process proc = null;
		try {
			proc = rt.exec(CMD + svnCommand);
		} catch (IOException e) {
			throw new CmdLineException(e);
		}

		return proc;
	}

	/**
	 * runs the process and returns the results.
	 * @param cmd
	 * @return String
	 */
	private String execString(String svnCommand) throws CmdLineException {
		String line;
		StringBuffer sb = new StringBuffer();
		StringBuffer sbErr = new StringBuffer();

		Process proc = execProcess(svnCommand);

		try {
			String result = Helper.getStringOrFail(proc);
			if (!listeners.isEmpty())
				logMessageAndCompleted(result);
			return result;
		} catch (CmdLineException e) {
			if (!listeners.isEmpty())
				logException(e);
			throw e;
		}

	}

	private void execVoid(String svnCommand) throws CmdLineException {
		execString(svnCommand);
	}

	private void logMessageAndCompleted(String messages) {
		StringTokenizer st = new StringTokenizer(messages, Helper.NEWLINE);
		int size = st.countTokens();
		//do everything but the last line
		for (int i = 1; i < size; i++) {
			logMessage(st.nextToken());
		}

		//log the last line as the completed message.
		if (size > 0)
			logCompleted(st.nextToken());
	}

	private void logCompleted(String message) {
		for (Iterator it = listeners.iterator(); it.hasNext();) {
			ISVNNotifyListener listener = (ISVNNotifyListener) it.next();
			listener.logCompleted(message);
		}
	}

	private void logMessage(String message) {
		for (Iterator it = listeners.iterator(); it.hasNext();) {
			ISVNNotifyListener listener = (ISVNNotifyListener) it.next();
			listener.logMessage(message);
		}
	}

	private void logCommand(String line) {
		for (Iterator it = listeners.iterator(); it.hasNext();) {
			ISVNNotifyListener listener = (ISVNNotifyListener) it.next();
			listener.logCommandLine(line);
		}
	}

	private void logException(CmdLineException e) {
		StringTokenizer st = new StringTokenizer(e.getMessage(), Helper.NEWLINE);
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				ISVNNotifyListener listener = (ISVNNotifyListener) it.next();
				listener.logError(line);
			}
		}
	}

	private String getAuthInfo() {
		if (user == null || pass == null || user.length() == 0)
			return "";
		return MessageFormat.format(AUTH_INFO, new String[] { user, pass });
	}

	private static String validRev(String revision) {
		return (revision == null || "".equals(revision)) ? "HEAD" : revision;
	}

}
