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
    private static String CMD_VERSION = " --version";
	private static String CMD_ADD = " add {0} {1}";
	private static String CMD_CAT = " cat -r {0} {1}";
	private static String CMD_CLEANUP = " cleanup {0}";
	private static String CMD_COMMIT = " ci {0} -m \"{1}\"";
	private static String CMD_COPY = " cp -r {0} -m \"{1}\" {2} {3}";
	private static String CMD_COPY_LOCAL = " cp {0} {1}";
	private static String CMD_CHECKOUT = " co -r {0} {1} {2}";
	private static String CMD_DELETE = " rm {0} {1} --force";
	private static String CMD_DIFF = " diff {0} {1}@{2} {3}@{4}";
	private static String CMD_EXPORT = " export -r {0} {1} {2} {3}";
	private static String CMD_IMPORT = " import {0} {1} {2} -m \"{3}\"";
	private static String CMD_INFO = " info {0}";
	private static String CMD_LIST = " list -v -r {0} {1}";
	private static String CMD_LOG = " log -r {0} {1}";
	private static String CMD_MKDIR = " mkdir -m \"{0}\" {1}";
	private static String CMD_MKDIR_LOCAL = " mkdir {0}";
	private static String CMD_MOVE =
		" mv -r {0} {1} {2} {3} --force --non-interactive";
	private static String CMD_PROPGET = " propget {0} {1}";
	private static String CMD_PROPSET = " propset {0} \"{1}\" {2}";
	private static String CMD_PROPSET_FILE = " propset {0} -F \"{1}\" {2}";
	private static String CMD_REVERT = " revert {0} {1}";
	private static String CMD_STATUS =
		" status -v -N {0} {1} --non-interactive";
	private static String CMD_RECURSIVE_STATUS =
		" status -v {0} --non-interactive";

	private static String CMD_UPDATE = " up -r {0} {1} --non-interactive";
	private static String AUTH_INFO = " --username \"{0}\" --password \"{1}\"";

	private String CMD;

	private static String user;
	private static String pass;

	//Constructors
	public CommandLine(String svnPath) {
		CMD = svnPath;
	}

    public String version() throws CmdLineException {
        return exec(CMD+CMD_VERSION);
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
	public String add(String path, boolean recursive) throws CmdLineException {
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
	public InputStream cat(String url, String revision) throws CmdLineException {
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
	 * <pre>
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
	 * </pre>
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
	 * <pre>
	 * cleanup: Recursively clean up the working copy, removing locks, resuming
	 * unfinished operations, etc.
	 * usage: cleanup [PATH...]
	 * 
	 * Valid options:
	 *   --diff3-cmd arg          : use ARG as merge command
	 *   --config-dir arg         : read user configuration files from directory ARG
	 * </pre>
	 * 
	 * @param path
	 */
	public void cleanup(String path) throws CmdLineException {
		execInternal(
			CMD + MessageFormat.format(CMD_CLEANUP, new String[] { path }));
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
		String revision) throws CmdLineException {
		return execInternal(
			CMD
				+ MessageFormat.format(
					CMD_COPY,
					new String[] { validRev(revision), message, src, dest })
				+ getAuthInfo());

	}
	public Process copy(String src, String dest) throws CmdLineException {
		return execInternal(
			CMD
				+ MessageFormat.format(CMD_COPY_LOCAL, new String[] { src, dest })
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
	 * <pre>
	 * diff (di): display the differences between two paths.
	 * usage: 1. diff [-r N[:M]] [--old OLD-TGT] [--new NEW-TGT] [PATH...]
	 *        2. diff -r N:M URL
	 *        3. diff [-r N[:M]] URL1[@N] URL2[@M]
	 * 
	 *   1. Display the differences between OLD-TGT and NEW-TGT.  PATHs, if
	 *      given, are relative to OLD-TGT and NEW-TGT and restrict the output
	 *      to differences for those paths.  OLD-TGT and NEW-TGT may be working
	 *      copy paths or URL[@REV].
	 * 
	 *      OLD-TGT defaults to the path '.' and NEW-TGT defaults to OLD-TGT.
	 *      N defaults to "BASE" or, if OLD-TGT is an URL, to "HEAD".
	 *      M defaults to the current working version or, if NEW-TGT is an URL,
	 *      to "HEAD".
	 * 
	 *      '-r N' sets the revision of OLD-TGT to N, '-r N:M' also sets the
	 *      revision of NEW-TGT to M.
	 * 
	 *   2. Shorthand for 'svn diff -r N:M --old=URL --new=URL'.
	 * 
	 *   3. Shorthand for 'svn diff [-r N[:M]] --old=URL1 --new=URL2'
	 * 
	 *   Use just 'svn diff' to display local modifications in a working copy
	 * 
	 * Valid options:
	 *   -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 *                              {DATE}      date instead of revision number
	 *                              "HEAD"      latest in repository
	 *                              "BASE"      base revision of item's working copy
	 *                              "COMMITTED" revision of item's last commit
	 *                              "PREV"      revision before item's last commit
	 *   --old arg                : use ARG as the older target
	 *   --new arg                : use ARG as the newer target
	 *   -x [--extensions] arg    : pass ARG as bundled options to GNU diff
	 *   -N [--non-recursive]     : operate on single directory only
	 *   --diff-cmd arg           : use ARG as diff command
	 *   --no-diff-deleted        : do not print differences for deleted files
	 *   --notice-ancestry        : notice ancestry when calculating differences
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   --config-dir arg         : read user configuration files from directory ARG
	 */
	public InputStream diff(
		String oldPath,
		String oldRev,
		String newPath,
		String newRev,
		boolean recurse) throws CmdLineException {
		/*
		Process proc =
			execInternal(
				CMD
					+ MessageFormat.format(
						CMD_DIFF,
						new String[] {
							recurse ? "" : "-N",
							oldPath,
							oldRev,
							newPath,
							newRev })
					+ getAuthInfo());
		
		InputStream content = proc.getInputStream();
		return content;
		*/
		String commandLine = " diff ";
		if (!"BASE".equals(oldRev) || !"WORKING".equals(newPath)) {
			commandLine += "-r " + oldRev;
			if (!"WORKING".equals(newRev))
				commandLine += ":" + newRev + " ";
		}
		commandLine += " --old " + oldPath;
		commandLine += " --new " + newPath;

		Process proc = execInternal(CMD + commandLine);
		InputStream content = proc.getInputStream();
		return content;
	}

	/**
	 * <pre>
	 * export: export stuff.
	 * usage: 1. export [-r REV] URL [PATH]
	 *        2. export PATH1 PATH2
	 * 
	 *   1. Exports a clean directory tree from the repository specified by
	 *      URL, at revision REV if it is given, otherwise at HEAD, into
	 *      PATH. If PATH is omitted, the last component of the URL is used
	 *      for the local directory name.
	 * 
	 *   2. Exports a clean directory tree from the working copy specified by
	 *      PATH1 into PATH2.  all local changes will be preserved, but files
	 *      not under revision control will not be copied.
	 * 
	 * Valid options:
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
	 *   --config-dir arg         : read user configuration files from directory ARG
	 * </pre>
	 * 
	 */
	public void export(String url, String path, String revision, boolean force)
		throws CmdLineException {
		execInternal(
			CMD
				+ MessageFormat.format(
					CMD_EXPORT,
					new String[] {
						url,
						path,
						validRev(revision),
						(force) ? "--force" : "" }));
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
	public String log(String target, String revision) throws CmdLineException {
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
	public Process mkdir(String path, String message) throws CmdLineException {
		return execInternal(
			CMD
				+ MessageFormat.format(CMD_MKDIR, new String[] { message, path })
				+ getAuthInfo());
	}
	public Process mkdir(String localPath) throws CmdLineException{
		return execInternal(
			CMD
				+ MessageFormat.format(
					CMD_MKDIR_LOCAL,
					new String[] { localPath }));
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
	 * <pre>
	 * propget (pget, pg): Print value of PROPNAME on files, dirs, or revisions.
	 * usage: 1. propget PROPNAME [PATH...]
	 *        2. propget PROPNAME --revprop -r REV [URL]
	 * 
	 *   1. Prints versioned prop in working copy.
	 *   2. Prints unversioned remote prop on repos revision.
	 * 
	 *   By default, this subcommand will add an extra newline to the end
	 *   of the property values so that the output looks pretty.  Also,
	 *   whenever there are multiple paths involved, each property value
	 *   is prefixed with the path with which it is associated.  Use
	 *   the --strict option to disable these beautifications (useful,
	 *   for example, when redirecting binary property values to a file).
	 * 
	 * Valid options:
	 *   -R [--recursive]         : descend recursively
	 *   -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 *                              {DATE}      date instead of revision number
	 *                              "HEAD"      latest in repository
	 *                              "BASE"      base revision of item's working copy
	 *                              "COMMITTED" revision of item's last commit
	 *                              "PREV"      revision before item's last commit
	 *   --revprop                : operate on a revision property (use with -r)
	 *   --strict                 : use strict semantics
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   --config-dir arg         : read user configuration files from directory ARG
	 * </pre>
	 */
	public InputStream propget(String path, String name)
		throws CmdLineException {
		Process proc =
			execInternal(
				CMD
					+ MessageFormat.format(
						CMD_PROPGET,
						new String[] { name, path }));
		return proc.getInputStream();
	}

	/**
	 * <pre>
	 * propset (pset, ps): Set PROPNAME to PROPVAL on files, dirs, or revisions.
	 * 
	 * usage: 1. propset PROPNAME [PROPVAL | -F VALFILE] PATH...
	 *        2. propset PROPNAME --revprop -r REV [PROPVAL | -F VALFILE] [URL]
	 * 
	 *   1. Creates a versioned, local propchange in working copy.
	 *   2. Creates an unversioned, remote propchange on repos revision.
	 * 
	 *   Note: svn recognizes the following special versioned properties
	 *   but will store any arbitrary properties set:
	 *     svn:ignore     - A newline separated list of file patterns to ignore.
	 *     svn:keywords   - Keywords to be expanded.  Valid keywords are:
	 *       URL, HeadURL             - The URL for the head version of the object.
	 *       Author, LastChangedBy    - The last person to modify the file.
	 *       Date, LastChangedDate    - The date/time the object was last modified.
	 *       Rev, LastChangedRevision - The last revision the object changed.
	 *       Id                       - A compressed summary of the previous
	 *                                    4 keywords.
	 *     svn:executable - If present, make the file executable. This
	 *       property cannot be set on a directory.  A non-recursive attempt
	 *       will fail, and a recursive attempt will set the property only
	 *       on the file children of the directory.
	 *     svn:eol-style  - One of 'native', 'LF', 'CR', 'CRLF'.
	 *     svn:mime-type  - The mimetype of the file.  Used to determine
	 *       whether to merge the file, and how to serve it from Apache.
	 *       A mimetype beginning with 'text/' (or an absent mimetype) is
	 *       treated as text.  Anything else is treated as binary.
	 *     svn:externals  - A newline separated list of module specifiers,
	 *       each of which consists of a relative directory path, optional
	 *       revision flags, and an URL.  For example
	 *         foo             http://example.com/repos/zig
	 *         foo/bar -r 1234 http://example.com/repos/zag
	 * 
	 * Valid options:
	 *   -F [--file] arg          : read data from file ARG
	 *   -q [--quiet]             : print as little as possible
	 *   -r [--revision] arg      : revision X or X:Y range.  X or Y can be one of:
	 *                              {DATE}      date instead of revision number
	 *                              "HEAD"      latest in repository
	 *                              "BASE"      base revision of item's working copy
	 *                              "COMMITTED" revision of item's last commit
	 *                              "PREV"      revision before item's last commit
	 *   --targets arg            : pass contents of file ARG as additional args
	 *   -R [--recursive]         : descend recursively
	 *   --revprop                : operate on a revision property (use with -r)
	 *   --username arg           : specify a username ARG
	 *   --password arg           : specify a password ARG
	 *   --no-auth-cache          : do not cache authentication tokens
	 *   --non-interactive        : do no interactive prompting
	 *   --encoding arg           : treat value as being in charset encoding ARG
	 *   --force                  : force operation to run
	 *   --config-dir arg         : read user configuration files from directory ARG
	 * </pre>
	 */
	public void propset(
		String propName,
		String propValue,
		String target,
		boolean recurse)
		throws CmdLineException {
		exec(
			CMD
				+ MessageFormat.format(
					CMD_PROPSET,
					new String[] { propName, propValue, target }));
	}
	public void propsetFile(
		String propName,
		String propFile,
		String target,
		boolean recurse)
		throws CmdLineException {
		exec(
			CMD
				+ MessageFormat.format(
					CMD_PROPSET_FILE,
					new String[] { propName, propFile, target }));
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
	public String revert(String paths, boolean recursive) throws CmdLineException {
		String recursiveFlag = (recursive) ? "-R" : "";
		return exec(
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
	
	public String recursiveStatus(String path)
		throws CmdLineException {
		return exec(
			CMD
				+ MessageFormat.format(CMD_RECURSIVE_STATUS, new String[] { path })
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

	private Process execInternal(String cmd) throws CmdLineException {
		Runtime rt = Runtime.getRuntime();

		/* run the process */
		Process proc = null;
		try {
			proc = rt.exec(cmd);
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
