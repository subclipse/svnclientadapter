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

/**
 * Provides a rudimentary interface to using the Subversion commandline client. 
 * @author philip schatz
 */
public class CommandLine {

	/*
	 * These are the commands used to execute the methods listed
	 * below.
	 */
	private static String CMD_ADD = " add {0} {1}";
	private static String CMD_CAT = " cat -r {0} {1}";
	private static String CMD_COMMIT = " ci {0} -m \"{1}\"";
	private static String CMD_COPY = " cp -r {0} -m \"{1}\" {2} {3}";
	private static String CMD_CHECKOUT = " co -r {0} {1} {2}";
	private static String CMD_DELETE = " rm {0} {1} --force";
	private static String CMD_IMPORT = " import {0} {1} {2} -m \"{3}\"";
	private static String CMD_INFO = " info {0}";
	private static String CMD_LIST = " list -v -r {0} {1}";
	private static String CMD_LOG = " log -r {0} {1}";
	private static String CMD_MKDIR = " mkdir -m \"{0}\" {1}";
	private static String CMD_MOVE = " mv -r {0} {1} {2} {3} --force --non-interactive";
	private static String CMD_REVERT = " revert {0} {1}";
	private static String CMD_STATUS =
		" status -v -N {0} {1} --non-interactive";
	private static String CMD_UPDATE = " up -r {0} {1} --non-interactive";
	private static String AUTH_INFO =
		" --username \"{0}\" --password \"{1}\"";

	private String CMD;

	private static String user;
	private static String pass;

	//Constructors
	public CommandLine(String svnPath) {
		CMD = svnPath;
	}

	/**
	 * add: Put files and directories under revision control, scheduling
	 * them for addition to repository.  They will be added in next commit.
	 * usage: add PATH [PATH [PATH ... ]]
	 * 
	 * Valid options:
	 *   --targets arg            : pass contents of file ARG as additional args
	 *   -N [--non-recursive]     : operate on single directory only
	 *   -q [--quiet]             : print as little as possible
	 * 
	 * @param resource
	 * @param recursive
	 */
	public String add(String path, boolean recursive)
		throws CmdLineException {
		String flags = (recursive) ? "" : "-N";
		return exec(
			CMD + MessageFormat.format(CMD_ADD, new String[] { flags, path }));
	}

	/**
	 * 	cat: Output the content of specified files or URLs.
	 * 	usage: cat TARGET [TARGET [TARGET ... ]]
	 *
	 * 	Valid options:
	 * 	  -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 * 								 {DATE}      date instead of revision number
	 * 								 "HEAD"      latest in repository
	 * 								 "BASE"      base revision of item's working copy
	 * 								 "COMMITTED" revision of item's last commit
	 * 								 "PREV"      revision before item's last commit
	 * 	  --username arg           : specify a username ARG
	 * 	  --password arg           : specify a password ARG
	 * 	  --no-auth-cache          : do not cache authentication tokens
	 * 	  --non-interactive        : do no interactive prompting
	 */
	public InputStream cat(String url, String revision) {
		Process proc =
			execInternal(
				CMD
					+ MessageFormat.format(
						CMD_CAT,
						new String[] { revision, url })
					+ getAuthInfo());

		InputStream content = proc.getInputStream();
		return content;
	}

	/**
	 * commit (ci): Send changes from your working copy to the repository.
	 * usage: commit [PATH [PATH ... ]]
	 * 
	 *   Be sure to use one of -m or -F to send a log message.
	 * 
	 * Valid options:
	 *   -m [--message] arg       : specify commit message "ARG"
	 *   -F [--file] arg          : read data from file ARG
	 *   -q [--quiet]             : print as little as possible
	 *   -N [--non-recursive]     : operate on single directory only
	 *   --targets arg            : pass contents of file "ARG" as additional args
	 *   --force                  : force operation to run
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   --encoding arg           : treat value as being in charset encoding ARG
	 *   
	 * @param path
	 * @param message
	 * @throws AuthenticationException
	 */
	public String checkin(String path, String message)
		throws CmdLineException {
		return exec(
			CMD
				+ MessageFormat.format(
					CMD_COMMIT,
					new String[] { path, message })
				+ getAuthInfo());
	}

	/**
	 * checkout (co): Check out a working copy from a repository.
	 * usage: checkout URL [URL [URL ... ]] [PATH]
	 *   Note: If PATH is omitted, the basename of the URL will be used as
	 *   the destination. If multiple URLs are given each will be checked
	 *   out into a sub-directory of PATH, with the name of the sub-directory
	 *   being the basename of the URL.
	 * 
	 * Valid options:
	 *   -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 *                              {DATE}      date instead of revision number
	 *                              "HEAD"      latest in repository
	 *                              "BASE"      base revision of item's working copy
	 *                              "COMMITTED" revision of item's last commit
	 *                              "PREV"      revision before item's last commit
	 *   -q [--quiet]             : print as little as possible
	 *   -N [--non-recursive]     : operate on single directory only
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *
	 * @param url
	 * @param destination
	 * @param revision
	 * @param recursive
	 * @throws AuthenticationException
	 */
	public String checkout(
		String url,
		String destination,
		String revision,
		boolean recursive)
		throws CmdLineException {
		String flags = (recursive) ? "" : "-N";
		return exec(
			CMD
				+ MessageFormat.format(
					CMD_CHECKOUT,
					new String[] {
						validRev(revision),
						url,
						destination,
						flags })
				+ getAuthInfo());
	}

	/**
	 * copy (cp): Duplicate something in working copy or repos, remembering history.
	 * usage: copy SRC DST
	 * 
	 *   SRC and DST can each be either a working copy (WC) path or URL:
	 *     WC  -> WC:   copy and schedule for addition (with history)
	 *     WC  -> URL:  immediately commit a copy of WC to URL
	 *     URL -> WC:   check out URL into WC, schedule for addition
	 *     URL -> URL:  complete server-side copy;  used to branch & tag
	 * 
	 * Valid options:
	 *   -m [--message] arg       : specify commit message ARG
	 *   -F [--file] arg          : read data from file ARG
	 *   -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 *                              {DATE}      date instead of revision number
	 *                              "HEAD"      latest in repository
	 *                              "BASE"      base revision of item's working copy
	 *                              "COMMITTED" revision of item's last commit
	 *                              "PREV"      revision before item's last commit
	 *   -q [--quiet]             : print as little as possible
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   --encoding arg           : treat value as being in charset encoding ARG
	 */
	public Process copy(
		String src,
		String dest,
		String message,
		String revision) {
		return execInternal(
			CMD
				+ MessageFormat.format(
					CMD_COPY,
					new String[] { validRev(revision), message, src, dest })
				+ getAuthInfo());
	}

	/**
	 * delete (del, remove, rm): Remove files and directories from version control.
	 * usage: delete [TARGET [TARGET ... ]]
	 * 
	 *   If run on a working copy TARGET, the item is scheduled for deletion
	 *   upon the next commit.  Files, and directories that have not been
	 *   committed, are immediately removed from the working copy.  The
	 *   command will not remove TARGETs that are, or contain, unversioned
	 *   or modified items; use the --force option to override this
	 *   behaviour.
	 * 
	 *   If run on an URL, the item is deleted from the repository via an
	 *   immediate commit.
	 * 
	 * Valid options:
	 *   --force                  : force operation to run
	 *   -m [--message] arg       : specify commit message ARG
	 *   -F [--file] arg          : read data from file ARG
	 *   -q [--quiet]             : print as little as possible
	 *   --targets arg            : pass contents of file ARG as additional args
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   --encoding arg           : treat value as being in charset encoding ARG
	 *   
	 * @param target
	 */
	public String delete(String target, String message)
		throws CmdLineException {
		String msg = (message == null) ? "" : "-m \"" + message + "\"";
		return exec(
			CMD
				+ MessageFormat.format(CMD_DELETE, new String[] { msg, target })
				+ getAuthInfo());
	}

	/**
	 * import: Commit an unversioned file or tree into the repository.
	 * usage: import URL [PATH [NEW_ENTRY_IN_REPOS]]
	 *
	 *   Recursively commit a copy of PATH to URL.
	 *   If no third arg, copy top-level contents of PATH into URL
	 *   directly.  Otherwise, create NEW_ENTRY underneath URL and
	 *   begin copy there.
	 * 
	 * Valid options:
	 *   -m [--message] arg       : specify commit message "ARG"
	 *   -F [--file] arg          : read data from file ARG
	 *   -q [--quiet]             : print as little as possible
	 *   -N [--non-recursive]     : operate on single directory only
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   --encoding arg           : treat value as being in charset encoding ARG
	 * 
	 * @param url
	 * @param path
	 * @param module
	 * @param message optional. can be null
	 */
	public String importFiles(
		String url,
		String path,
		String module,
		String message)
		throws CmdLineException {
		return exec(
			CMD
				+ MessageFormat.format(
					CMD_IMPORT,
					new String[] { url, path, module, message })
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
	public String info(String path) throws CmdLineException {
		return exec(
			CMD + MessageFormat.format(CMD_INFO, new String[] { path }));
	}

	/**
	 *  list (ls): List directory entries of a URL.
	 * 	usage: list URL [URL ... ]
	 *
	 * 	  If URL is a file, just file entry will be displayed.
	 * 
	 * 	Valid options:
	 * 	  -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 * 								 {DATE}      date instead of revision number
	 * 								 "HEAD"      latest in repository
	 * 								 "BASE"      base revision of item's working copy
	 * 								 "COMMITTED" revision of item's last commit
	 * 								 "PREV"      revision before item's last commit
	 * 	  -v [--verbose]           : print extra information
	 * 	  -R [--recursive]         : descend recursively
	 * 	  --username arg           : specify a username ARG
	 * 	  --password arg           : specify a password ARG
	 * 	  --no-auth-cache          : do not cache authentication tokens
	 * 	  --non-interactive        : do no interactive prompting
	 */
	public String list(String url, String revision) throws CmdLineException {
		return exec(
			CMD
				+ MessageFormat.format(CMD_LIST, new String[] { revision, url })
				+ getAuthInfo());
	}

	/**
	 *  log: Show the log messages for a set of revision(s) and/or file(s).
	 * 	usage: log [URL] [PATH [PATH ... ]]
	 * 	  Print the log messages for local PATHs, or for PATHs under
	 * 	  URL, if URL is given.  If URL is given by itself, then print log
	 * 	  messages for everything under it.  With -v, also print all affected
	 * 	  paths with each log message.  With -q, don't print the log message
	 * 	  body itself (note that this is compatible with -v).
	 * 
	 *   Each log message is printed just once, even if more than one of the
	 * 	  affected paths for that revision were explicitly requested.  Logs
	 * 	  cross copy history by default; use --strict to disable this.
	 * 	  For example:
	 *
	 * 		svn log
	 * 		svn log foo.c
	 * 		svn log http://www.example.com/repo/project/foo.c
	 * 		svn log http://www.example.com/repo/project foo.c bar.c
	 * 
	 * 	Valid options:
	 * 	  -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 * 								 {DATE}      date instead of revision number
	 * 								 "HEAD"      latest in repository
	 * 								 "BASE"      base revision of item's working copy
	 * 								 "COMMITTED" revision of item's last commit
	 * 								 "PREV"      revision before item's last commit
	 * 	  -q [--quiet]             : print as little as possible
	 * 	  -v [--verbose]           : print extra information
	 * 	  --targets arg            : pass contents of file ARG as additional args
	 * 	  --strict                 : use strict semantics
	 * 	  --incremental            : give output suitable for concatenation
	 * 	  --xml                    : output in xml
	 * 	  --username arg           : specify a username ARG
	 * 	  --password arg           : specify a password ARG
	 * 	  --no-auth-cache          : do not cache authentication tokens
	 *	  --non-interactive        : do no interactive prompting
	 */
	public String log(String target, String revision)
		throws CmdLineException {
		return exec(
			CMD
				+ MessageFormat.format(
					CMD_LOG,
					new String[] { validRev(revision), target })
				+ getAuthInfo());
	}

	/**
	 * 	mkdir: Create a new directory under revision control.
	 * 	usage: mkdir TARGET [TARGET [TARGET ... ]]
	 *
	 * 	  Create a directory with a name given by the final component of
	 * 	  TARGET.  If TARGET is a working copy path the directory is scheduled
	 * 	  for addition in the working copy.  If TARGET is an URL the directory
	 * 	  is created in the repository via an immediate commit.  In both cases
	 * 	  all the intermediate directories must already exist.
	 * 
	 * 	Valid options:
	 * 	  -m [--message] arg       : specify commit message "ARG"
	 * 	  -F [--file] arg          : read data from file ARG
	 * 	  -q [--quiet]             : print as little as possible
	 * 	  --username arg           : specify a username ARG
	 * 	  --password arg           : specify a password ARG
	 * 	  --no-auth-cache          : do not cache authentication tokens
	 * 	  --non-interactive        : do no interactive prompting
	 * 	  --encoding arg           : treat value as being in charset encoding ARG
	 */
	public Process mkdir(String path, String message) {
		return execInternal(
			CMD
				+ MessageFormat.format(CMD_MKDIR, new String[] { message, path })
				+ getAuthInfo());
	}

	/**
	 * move (mv, rename, ren): Move/rename something in working copy or repository.
	 * usage: move SRC DST
	 * 
	 *   NOTE:  this command is equivalent to a 'copy' and 'delete'.
	 * 
	 *   SRC and DST can both be working copy (WC) paths or URLs:
	 *     WC  -> WC:   move and schedule for addition (with history)
	 *     URL -> URL:  complete server-side rename.
	 * 
	 * Valid options:
	 *   -m [--message] arg       : specify commit message ARG
	 *   -F [--file] arg          : read data from file ARG
	 *   -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 *                              {DATE}      date instead of revision number
	 *                              "HEAD"      latest in repository
	 *                              "BASE"      base revision of item's working copy
	 *                              "COMMITTED" revision of item's last commit
	 *                              "PREV"      revision before item's last commit
	 *   -q [--quiet]             : print as little as possible
	 *   --force                  : force operation to run
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   --encoding arg           : treat value as being in charset encoding ARG
	 *   
	 * @param source
	 * @param dest
	 */
	public String move(String source, String dest, String message, String revision) throws CmdLineException {
		String messageStr = (message == null) ? "" : "-m \"" + message + "\"";
		return exec(
			CMD
				+ MessageFormat.format(CMD_MOVE, new String[] { validRev(revision), source, dest, messageStr })
				+ getAuthInfo());
	}

	/**
	 * revert: Restore pristine working copy file (undo all local edits)
	 * usage: revert PATH [PATH [PATH ... ]]
	 * 
	 *   Note:  this routine does not require network access, and
	 *   resolves any conflicted states.
	 * 
	 * Valid options:
	 *   --targets arg            : pass contents of file ARG as additional args
	 *   -R [--recursive]         : descend recursively
	 *   -q [--quiet]             : print as little as possible
	 */
	public Process revert(String paths, boolean recursive) {
		String recursiveFlag = (recursive) ? "-R" : "";
		return execInternal(
			CMD
				+ MessageFormat.format(
					CMD_REVERT,
					new String[] { recursiveFlag, paths }));
	}

	/**
	 * status (stat, st): Print the status of working copy files and directories.
	 * usage: status [PATH [PATH ... ]]
	 * 
	 *   With no args, print only locally modified items (no network access).
	 *   With -u, add working revision and server out-of-date information.
	 *   With -v, print full revision information on every item.
	 * 
	 * The first five columns in the output are each one character wide:
	 *     First column: Says if item was added, deleted, or otherwise changed
	 *       ' ' no modifications
	 *       'A' Added
	 *       'D' Deleted
	 *       'M' Modified
	 *       'C' Conflicted
	 *       '?' item is not under revision control
	 *       '!' item is missing and was removed via a non-svn command
	 *       '~' versioned item obstructed by some item of a different kind
	 *     Second column: Modifications of a file's or directory's properties
	 *       ' ' no modifications
	 *       'M' Modified
	 *       'C' Conflicted
	 *     Third column: Whether the working copy directory is locked
	 *       ' ' not locked
	 *       'L' locked
	 *     Fourth column: Scheduled commit will contain addition-with-history
	 *       ' ' no history scheduled with commit
	 *       '+' history scheduled with commit
	 *     Fifth column: Whether the item is switched relative to its parent
	 *       ' ' normal
	 *       'S' switched
	 *     The out-of-date information appears in the eighth column
	 *       '*' a newer revision exists on the server
	 *       ' ' the working copy is up to date
	 * 
	 * Remaining fields are variable width and delimited by spaces:
	 * 
	 * The working revision is the next field if -u or -v is given, followed
	 * by both the last committed revision and last committed author if -v is
	 * given.  The working copy path is always the final field, so it can
	 * include spaces.
	 * 
	 *   Example output:
	 *     svn status wc
	 *      M     wc/bar.c
	 *     A  +   wc/qax.c
	 * 
	 *     svn status -u wc
	 *      M           965    wc/bar.c
	 *            *     965    wc/foo.c
	 *     A  +         965    wc/qax.c
	 *     Head revision:   981
	 * 
	 *     svn status --show-updates --verbose wc
	 *      M           965       938     kfogel   wc/bar.c
	 *            *     965       922    sussman   wc/foo.c
	 *     A  +         965       687        joe   wc/qax.c
	 *                  965       687        joe   wc/zig.c
	 *     Head revision:   981
	 * 
	 * Valid options:
	 *   -u [--show-updates]      : display update information
	 *   -v [--verbose]           : print extra information
	 *   -N [--non-recursive]     : operate on single directory only
	 *   -q [--quiet]             : print as little as possible
	 *   --no-ignore              : disregard default and svn:ignore property ignores
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   
	 * @param path
	 */
	public String status(String path, boolean checkUpdates)
		throws CmdLineException {
		String flags = (checkUpdates ? "-u" : "");
		return exec(
			CMD
				+ MessageFormat.format(CMD_STATUS, new String[] { flags, path })
				+ getAuthInfo());
	}

	/**
	 * update (up): Bring changes from the repository into the working copy.
	 * usage: update [PATH [PATH ... ]]
	 * 
	 *   If no revision given, bring working copy up-to-date with HEAD rev.
	 *   Else synchronize working copy to revision given by -r.
	 * 
	 *   For each updated item a line will start with a character reporting the
	 *   action taken.  These characters have the following meaning:
	 * 
	 *     A  Added
	 *     D  Deleted
	 *     U  Updated
	 *     C  Conflict
	 *     G  Merged
	 * 
	 *   A character in the first column signifies an update to the actual file,
	 *   while updates to the file's props are shown in the second column.
	 * 
	 * Valid options:
	 *   -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 *                              {DATE}      date instead of revision number
	 *                              "HEAD"      latest in repository
	 *                              "BASE"      base revision of item's working copy
	 *                              "COMMITTED" revision of item's last commit
	 *                              "PREV"      revision before item's last commit
	 *   -N [--non-recursive]     : operate on single directory only
	 *   -q [--quiet]             : print as little as possible
	 *   --diff3-cmd arg          : Use ARG as merge command
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 */
	public String update(String path, String revision)
		throws CmdLineException {
		return exec(
			CMD
				+ MessageFormat.format(
					CMD_UPDATE,
					new String[] { validRev(revision), path })
				+ getAuthInfo());
	}

	public void setUsername(String username) {
		user = username;
	}

	public void setPassword(String password) {
		pass = password;
	}

	private Process execInternal(String cmd) {
		Runtime rt = Runtime.getRuntime();
		System.out.println("CommandLine:starting [" + cmd + "]");

		/* run the process */
		Process proc = null;
		try {
			proc = rt.exec(cmd);
		} catch (IOException e) {
		}

		return proc;
	}

	/**
	 * runs the process and returns the results.
	 * @param cmd
	 * @return String
	 */
	private String exec(String cmd) throws CmdLineException {
		String line;
		StringBuffer sb = new StringBuffer();
		StringBuffer sbErr = new StringBuffer();

		Process proc = execInternal(cmd);

		return Helper.getStringOrFail(proc);
	}

	private String getAuthInfo() {
		if (user == null || pass == null || user.length() == 0)
			return "";
		return MessageFormat.format(AUTH_INFO, new String[] { user, pass });
	}

	private String validRev(String revision) {
		return (revision == null || "".equals(revision)) ? "HEAD" : revision;
	}

}
