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
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.Info;
import org.tigris.subversion.javahl.PromptUserPassword;
import org.tigris.subversion.javahl.PropertyData;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.SVNAdmin;
import org.tigris.subversion.javahl.SVNClient;
import org.tigris.subversion.javahl.SVNClientInterface;
import org.tigris.subversion.javahl.SVNClientSynchronized;
import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.svnclientadapter.ISVNAnnotations;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNBaseDir;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNInfoUnversioned;
import org.tigris.subversion.svnclientadapter.SVNKeywords;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusUnversioned;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineAnnotations;

/**
 * An adapter for SVNClient. Easier and safer to use than SVNClient
 *
 * @author Cédric Chabanois (cchabanois at no-log.org)
 * @author Panagiotis Korros (pkorros at bigfoot.com) 
 *
 */
public class JhlClientAdapter implements ISVNClientAdapter {
    final private static int SVN_ERR_WC_NOT_DIRECTORY = 155007;

    private SVNClientSynchronized svnClient;
    private SVNAdmin svnAdmin;
    private JhlNotificationHandler notificationHandler;
    private PromptUserPassword promptUserPasswordHandler;
    
    private static boolean availabilityCached = false;
    private static boolean available;

    public JhlClientAdapter() {
        svnClient = new SVNClientSynchronized();
        svnAdmin = new SVNAdmin();
        notificationHandler = new JhlNotificationHandler();
        svnClient.notification(notificationHandler);        
        svnClient.setPrompt(new DefaultPromptUserPassword());
    }

    /**
     * tells if JhlClientAdapter is usable
     * @return
     */
    public static boolean isAvailable() {
    	if (!availabilityCached) {
	        try {
	            // if library is already loaded, it will not be reloaded
	
	        	//workaround to solve Subclipse ISSUE #83
	        	String os = System.getProperty("osgi.os");
				if( "win32".equals(os) ) {
					System.loadLibrary("libapr");
					System.loadLibrary("libapriconv");
					System.loadLibrary("libaprutil");
					System.loadLibrary("libeay32");
					System.loadLibrary("libdb42");
					System.loadLibrary("ssleay32");
				}
	        	//workaround to solve Subclipse ISSUE #83
	
	            /*
	             * first try to load the library by the new name.
	             * if that fails, try to load the library by the old name.
	             */
	            try
	            {
	                System.loadLibrary("svnjavahl-1");
	            }
	            catch(UnsatisfiedLinkError ex)
	            {
	                try
	                {
	                    System.loadLibrary("libsvnjavahl-1");
	                }
	                catch (UnsatisfiedLinkError e)
	                {
	                    System.loadLibrary("svnjavahl");
	                }
	            }        	
	        	
	            available = true;
	        } catch (Exception e) {
	        	available = false;
	        } catch (UnsatisfiedLinkError e) {
	        	available = false;
	        }
	        availabilityCached = true;
    	}
    	return available;
    }

	/**
	 * for users who want to directly use underlying javahl SVNClientInterface
	 * @return
	 */
	public SVNClientInterface getSVNClient() {
		return svnClient;
	}
 
    /**
     * the default prompter : never prompts the user
     */
    private class DefaultPromptUserPassword implements PromptUserPassword {

        public String askQuestion(String realm, String question, boolean showAnswer) {
            return "";
		}

        public boolean askYesNo(String realm, String question, boolean yesIsDefault) {
			return yesIsDefault;
		}

		public String getPassword() {
			return "";
		}

		public String getUsername() {
			return "";
		}

        public boolean prompt(String realm, String username) {
			return false;
		}
    }

    /**
     * Add a notification listener
     */
    public void addNotifyListener(ISVNNotifyListener listener) {
        notificationHandler.add(listener);
    }

    /**
     * Remove a notification listener 
     */
    public void removeNotifyListener(ISVNNotifyListener listener) {
        notificationHandler.remove(listener);
    }

    /**
     * Sets the username.
     */
    public void setUsername(String username) {
        svnClient.username(username);
    }

    /**
     * Sets the password.
     */
    public void setPassword(String password) {
        notificationHandler.setCommand(ISVNNotifyListener.Command.UNDEFINED);
        svnClient.password(password);
    }

    /**
     * Register callback interface to supply username and password on demand
     */
    public void setPromptUserPassword(PromptUserPassword prompt) {
        promptUserPasswordHandler = prompt;
        svnClient.setPrompt(prompt);        
    }


    private static String fileToSVNPath(File file, boolean canonical) {
    	// SVN need paths with '/' separators
    	if (canonical) {
            try {
	   	       return file.getCanonicalPath().replace('\\', '/');    		 
    	   } catch (IOException e)
    	   {
    	       return null;
    	   }
        } else
            return file.getPath().replace('\\', '/');
    }
    
    /**
     * Adds a file (or directory) to the repository.
     * @exception ClientException
     */
    public void addFile(File file) throws SVNClientException {
        try{
            notificationHandler.setCommand(ISVNNotifyListener.Command.ADD);
            notificationHandler.logCommandLine("add -N "+file.toString());
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(file));
            svnClient.add(fileToSVNPath(file, true), false);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }        
    }

    /**
     * Adds a directory to the repository.
     * @exception ClientException
     */
    public void addDirectory(File dir, boolean recurse)
        throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.ADD);            
            notificationHandler.logCommandLine(
                "add"+
                (recurse?"":"-N")+
                " "+dir.toString());
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(dir));
            svnClient.add(fileToSVNPath(dir, true), recurse);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /**
     * Executes a revision checkout.
     * @param moduleName name of the module to checkout.
     * @param destPath destination directory for checkout.
     * @param revision the revision number to checkout. If the number is -1
     *                 then it will checkout the latest revision.
     * @param recurse whether you want it to checkout files recursively.
     * @exception ClientException
     */
    public void checkout(
        SVNUrl moduleName,
        File destPath,
        SVNRevision revision,
        boolean recurse)
        throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.CHECKOUT);
            notificationHandler.logCommandLine(
                "checkout" +
                (recurse?"":" -N") + 
                " -r "+revision.toString()+
                " "+moduleName.toString());        
			notificationHandler.setBaseDir(new File("."));
            svnClient.checkout(
			    moduleName.toString(),
                fileToSVNPath(destPath, true),
                JhlConverter.convert(revision),
                recurse);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }

    /**
     * Commits changes to the repository. This usually requires
     * authentication, see Auth.
     * @return Returns a long representing the revision. It returns a
     *         -1 if the revision number is invalid.
     * @param path files to commit.
     * @param message log message.
     * @param recurse whether the operation should be done recursively.
     * @exception ClientException
     */
    public long commit(File[] paths, String message, boolean recurse)
        throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.COMMIT);
            String[] files = new String[paths.length];
            String commandLine = "commit -m \""+message+"\"";
            if (!recurse)
                commandLine+=" -N";

            for (int i = 0; i < paths.length; i++) {
                files[i] = fileToSVNPath((File) paths[i], true);
                commandLine+=" "+files[i].toString();
            }
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(paths));

            return svnClient.commit(files, message, recurse);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }

    }

	/**
	 * List directory entries of a URL
	 * @param url
	 * @param revision
	 * @param recurse
	 * @return
	 * @throws ClientException
	 */
	public ISVNDirEntry[] getList(SVNUrl url, SVNRevision revision, boolean recurse) 
            throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.LS);
            String commandLine = "list -r "+revision.toString()+(recurse?"-R":"")+" "+url.toString();
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir(new File("."));		
            return JhlConverter.convert(svnClient.list(url.toString(), JhlConverter.convert(revision), recurse));
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
	}

	/**
	 * List directory entries of a directory
	 * @param url
	 * @param revision
	 * @param recurse
	 * @return
	 * @throws ClientException
	 */
	public ISVNDirEntry[] getList(File path, SVNRevision revision, boolean recurse) 
            throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.LS);
            String target = fileToSVNPath(path, true);
            String commandLine = "list -r "+revision.toString()+(recurse?"-R":"")+" "+path;
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir(new File("."));		
            return JhlConverter.convert(svnClient.list(target, JhlConverter.convert(revision), recurse));
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getDirEntry(org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision)
	 */
	public ISVNDirEntry getDirEntry(SVNUrl url, SVNRevision revision)
		throws SVNClientException {
		
		// list give the DirEntrys of the elements of a directory or the DirEntry
		// of a file
		ISVNDirEntry[] entries = getList(url.getParent(), revision,false);
		String expectedPath = url.getSegment(url.getSegments().length-1);
		for (int i = 0; i < entries.length;i++) {
			if (entries[i].getPath().equals(expectedPath)) {
				return entries[i];
			}
		}
		return null; // not found
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getDirEntry(java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
	 */
	public ISVNDirEntry getDirEntry(File path, SVNRevision revision) 
		throws SVNClientException {

		// list give the DirEntrys of the elements of a directory or the DirEntry
		// of a file
		ISVNDirEntry[] entries = getList(path.getParentFile(), revision,false);
		String expectedPath = path.getName();
		for (int i = 0; i < entries.length;i++) {
			if (entries[i].getPath().equals(expectedPath)) {
				return entries[i];
			}
		}
		return null; // not found
	}
	
    /**
     * Returns the status of a single file in the path.
     *
     * @param path File to gather status.
     * @return a Status
     */
    public ISVNStatus getSingleStatus(File path) 
            throws SVNClientException {
        return getStatus(new File[] {path})[0];
    }
    
    /**
     * Returns the status of the given resources
     */
    public ISVNStatus[] getStatus(File[] path) 
            throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.STATUS);
        String filePathSVN[] = new String[path.length];
        String commandLine = "status -N --no-ignore"; 
        for (int i = 0; i < filePathSVN.length;i++) {
            filePathSVN[i] = fileToSVNPath(path[i], true);
            commandLine+=" "+filePathSVN[i]; 
        }
        notificationHandler.logCommandLine(commandLine);
		notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));

        ISVNStatus[] statuses = new ISVNStatus[path.length]; 
        for (int i = 0; i < filePathSVN.length;i++) {
            try {
                Status status = svnClient.singleStatus(filePathSVN[i], false);
                if (status == null) {
                	statuses[i] = new SVNStatusUnversioned(path[i]);
                } else {
                	statuses[i] = new JhlStatus(status);
                }
            } catch (ClientException e) {
                if (e.getAprError() == SVN_ERR_WC_NOT_DIRECTORY) {
                    // when there is no .svn dir, an exception is thrown ...
                    statuses[i] = new SVNStatusUnversioned(path[i]);
                } else
                {
                    notificationHandler.logException(e);
                    throw new SVNClientException(e);
                }
            }
        }
        return statuses;
    }

    /**
     * Returns the status of files and directory recursively
     *
     * @param path File to gather status.
     * @return a Status
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll)
		throws SVNClientException {
		return getStatus(path, descend,getAll,false);
	}
	

    /**
     * Returns the status of files and directory recursively
     *
     * @param path File to gather status.
     * @param descend get recursive status information
     * @param getAll get status information for all files
     * @param contactServer contact server to get remote changes
     *  
     * @return a Status
     */
    public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll, boolean contactServer) throws SVNClientException {
		notificationHandler.setCommand(ISVNNotifyListener.Command.STATUS);
		String filePathSVN = fileToSVNPath(path, true);
		notificationHandler.logCommandLine("status " + (contactServer?"-u ":"")+ filePathSVN);
		notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
		try {
			return JhlConverter.convert(
                svnClient.status(
                    filePathSVN,  
                    descend,     // If descend is true, recurse fully, else do only immediate children.
                    contactServer,       // If update is set, contact the repository and augment the status structures with information about out-of-dateness     
					getAll));    // retrieve all entries; otherwise, retrieve only "interesting" entries (local mods and/or
                                 // out-of-date).
		} catch (ClientException e) {
			if (e.getAprError() == SVN_ERR_WC_NOT_DIRECTORY) {
				// when there is no .svn dir, an exception is thrown ...
				return new ISVNStatus[] {new SVNStatusUnversioned(path)};
			} else {
				notificationHandler.logException(e);
				throw new SVNClientException(e);
			}
		}
    }

    /**
     * copy and schedule for addition (with history)
     * @param srcPath
     * @param destPath
     * @throws ClientException
     */ 
	public void copy(File srcPath, File destPath) throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);

			String src = fileToSVNPath(srcPath, true);
			String dest = fileToSVNPath(destPath, true);
			notificationHandler.logCommandLine("copy " + src + " " + dest);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(new File[] {srcPath,destPath }));
			svnClient.copy(src, dest, "", Revision.HEAD);
			// last two parameters are not used
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}

	/**
	 * immediately commit a copy of WC to URL
	 * @param srcPath
	 * @param destUrl
	 * @throws ClientException
	 */
	public void copy(File srcPath, SVNUrl destUrl, String message)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
			String src = fileToSVNPath(srcPath, true);
			String dest = destUrl.toString();
			notificationHandler.logCommandLine("copy " + src + " " + dest);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(srcPath));
			svnClient.copy(src, dest, message, Revision.HEAD);
			// last parameter is not used
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}

	/**
	 * check out URL into WC, schedule for addition
	 * @param srcUrl
	 * @param destPath
	 * @throws ClientException
	 */
	public void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
			String src = srcUrl.toString();
			String dest = fileToSVNPath(destPath, true);
			notificationHandler.logCommandLine("copy " + src + " " + dest);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(destPath));
			svnClient.copy(src, dest, "", JhlConverter.convert(revision));
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}
	
	/**
	 * complete server-side copy;  used to branch & tag
	 * @param srcUrl
	 * @param destUrl
	 * @throws ClientException
	 */
	public void copy(
		SVNUrl srcUrl,
		SVNUrl destUrl,
		String message,
		SVNRevision revision)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
			String src = srcUrl.toString();
			String dest = destUrl.toString();
			notificationHandler.logCommandLine("copy " + src + " " + dest);
			notificationHandler.setBaseDir();
			svnClient.copy(src, dest, message, JhlConverter.convert(revision));
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}

	/**
	 * item is deleted from the repository via an immediate commit.
	 * @param url
	 * @param message
	 * @throws ClientException
	 */
	public void remove(SVNUrl url[], String message) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.REMOVE);

            String commandLine = "delete -m \""+message+"\"";
            
            String targets[] = new String[url.length];
            for (int i = 0; i < url.length;i++) {
                targets[i] = url[i].toString(); 
                commandLine += " "+targets[i];
            }
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir();
		    svnClient.remove(targets,message,false);
            
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }           
	}

	/**
	 * the item is scheduled for deletion upon the next commit.  
	 * Files, and directories that have not been committed, are immediately 
	 * removed from the working copy.  The command will not remove TARGETs 
	 * that are, or contain, unversioned or modified items; 
	 * use the force option to override this behaviour.
	 * @param file
	 * @param force
	 * @throws ClientException
	 */
	public void remove(File file[], boolean force) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.REMOVE);
            
            String commandLine = "delete"+(force?" --force":"");
            String targets[] = new String[file.length];
            
            for (int i = 0; i < file.length;i++) {
                targets[i] = fileToSVNPath(file[i], true);
                commandLine += " "+targets[i];
            }
            
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(file));
   
            svnClient.remove(targets,"",force);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }           
	}

	/**
	 * Exports a clean directory tree from the repository specified by
	 * srcUrl, at revision revision 
	 * @param srcUrl
	 * @param destPath
	 * @param revision
	 * @throws ClientException
	 */
	public void doExport(
		SVNUrl srcUrl,
		File destPath,
		SVNRevision revision,
		boolean force)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.EXPORT);
			String src = srcUrl.toString();
			String dest = fileToSVNPath(destPath, true);
			notificationHandler.logCommandLine(
				"export -r " + revision.toString() + ' ' + src + ' ' + dest);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(destPath));
			svnClient.doExport(src, dest, JhlConverter.convert(revision), force);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}
	
	/**
	 * Exports a clean directory tree from the working copy specified by
	 * PATH1 into PATH2.  all local changes will be preserved, but files
	 * not under revision control will not be copied.
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */
	public void doExport(File srcPath, File destPath, boolean force)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.EXPORT);
			String src = fileToSVNPath(srcPath, true);
			String dest = fileToSVNPath(destPath, true);
			notificationHandler.logCommandLine("export " + src + ' ' + dest);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(new File[]{srcPath,destPath }));
			// in this case, revision is not used but must be valid
			svnClient.doExport(src, dest, Revision.HEAD, force);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}
	
	/**
	 * Import file or directory PATH into repository directory URL at head
	 * @param path
	 * @param url
	 * @param newEntry new directory in which the contents of <i>path</i> are imported.
	 * 		  if null, copy top-level contents of PATH into URL directly
	 * @param message
	 * @param recurse
	 * @throws ClientException
	 */
	public void doImport(
		File path,
		SVNUrl url,
		String message,
		boolean recurse)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.IMPORT);
			String src = fileToSVNPath(path, true);
			String dest = url.toString();
			notificationHandler.logCommandLine(
				"import -m \""
					+ message
					+ "\" "
					+ (recurse ? "" : "-N ")
					+ src
					+ ' '
					+ dest);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			svnClient.doImport(src, dest, message, recurse);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}

	/**
	 * Creates a directory directly in a repository
	 * @param url
	 * @param message
	 * @throws ClientException
	 */
	public void mkdir(SVNUrl url, String message) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.MKDIR);
		    String target = url.toString();
            notificationHandler.logCommandLine(
                "mkdir -m \""+message+"\" "+target);
			notificationHandler.setBaseDir();
            svnClient.mkdir(new String[] { target },message);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }                   	
	}
	
	/**
	 * creates a directory on disk and schedules it for addition.
	 * @param file
	 * @throws ClientException
	 */
	public void mkdir(File file) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.MKDIR);
            String target = fileToSVNPath(file, true);
            notificationHandler.logCommandLine(
                "mkdir "+target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(file));
            svnClient.mkdir(new String[] { target },"");
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }           	
	}

	/**
	 * Moves or renames a file.
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */	
	public void move(File srcPath, File destPath, boolean force) throws SVNClientException {
        // use force when you want to move file even if there are local modifications
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.MOVE);
		    String src = fileToSVNPath(srcPath, true);
            String dest = fileToSVNPath(destPath, true);
            notificationHandler.logCommandLine(
                    "move "+src+' '+dest);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(new File[] {srcPath, destPath}));        
            svnClient.move(src,dest,"",Revision.HEAD,force);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }                   	
	}

	/**
	 * Moves or renames a file.
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */	
	public void move(
		SVNUrl srcUrl,
		SVNUrl destUrl,
		String message,
		SVNRevision revision)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.MOVE);
			String src = srcUrl.toString();
			String dest = destUrl.toString();
			notificationHandler.logCommandLine(
				"move -m \""
					+ message
					+ "\" -r "
					+ revision.toString()
					+ ' '
					+ src
					+ ' '
					+ dest);
			notificationHandler.setBaseDir();
			svnClient.move(src, dest, message, JhlConverter.convert(revision), false);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}	

	/**
	 * Update a file or a directory
	 * @param path
	 * @param revision
	 * @param recurse
	 * @throws ClientException
	 */
	public void update(File path, SVNRevision revision, boolean recurse)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.UPDATE);
			String target = fileToSVNPath(path, true);
			notificationHandler.logCommandLine(
				"update -r "
					+ revision.toString()
					+ ' '
					+ (recurse ? "" : "-N ")
					+ target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			svnClient.update(target, JhlConverter.convert(revision), recurse);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}

    /**
     * Restore pristine working copy file (undo all local edits)
     * @param path
     * @param recurse
     * @throws ClientException
     */
    public void revert(File path, boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.REVERT);
            String target = fileToSVNPath(path, true);
            notificationHandler.logCommandLine(
                "revert "+
                (recurse?"":"-N ")+
                target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path)); 
            svnClient.revert(target,recurse);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }         
    }

    /**
     * Get the log messages for a set of revision(s) 
     * @param url
     * @param revisionStart
     * @param revisionEnd
     * @return
     */
	public ISVNLogMessage[] getLogMessages(
		SVNUrl url,
		SVNRevision revisionStart,
		SVNRevision revisionEnd)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.LOG);
			String target = url.toString();
			notificationHandler.logCommandLine(
				"log -r "
					+ revisionStart.toString()
					+ ":"
					+ revisionEnd.toString()
					+ " "
					+ target);
			notificationHandler.setBaseDir();
			return JhlConverter.convert(
                    svnClient.logMessages(
                            target, 
                            JhlConverter.convert(revisionStart), 
                            JhlConverter.convert(revisionEnd),
                            false,  // don't stop on copy
                            true)); // discover paths
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	} 
    
    /**
     * Get the log messages for a set of revision(s)
     * @param path
     * @param revisionStart
     * @param revisionEnd
     * @return
     */
	public ISVNLogMessage[] getLogMessages(
		File path,
		SVNRevision revisionStart,
		SVNRevision revisionEnd)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(
				ISVNNotifyListener.Command.LOG);
			String target = fileToSVNPath(path, true);
			notificationHandler.logCommandLine(
				"log -r "
					+ revisionStart.toString()
					+ ":"
					+ revisionEnd.toString()
					+ " "
					+ target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			return JhlConverter.convert(
                    svnClient.logMessages(
                            target, 
                            JhlConverter.convert(revisionStart), 
                            JhlConverter.convert(revisionEnd),
                            false,   // don't stop on copy
                            true));  // discover paths
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}    


	/**
	 * enables logging
	 * @param logLevel
	 * @param filePath
	 */	
	public static void enableLogging(int logLevel,File filePath) {
		SVNClient.enableLogging(logLevel,fileToSVNPath(filePath, true));	
	}

    /**
     * get the content of a file
     * @param url
     * @param revision
     */
	public InputStream getContent(SVNUrl url, SVNRevision revision)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(
				ISVNNotifyListener.Command.CAT);
            notificationHandler.logCommandLine(
                            "cat -r "
                                + revision.toString()
                                + " "
                                + url.toString());
			notificationHandler.setBaseDir();                
			
			byte[] contents = svnClient.fileContent(url.toString(), JhlConverter.convert(revision));
			InputStream input = new ByteArrayInputStream(contents);
			return input;
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}


	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getContent(java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
	 */
	public InputStream getContent(File path, SVNRevision revision)
		throws SVNClientException {
		try {
			String target = fileToSVNPath(path, true);
			notificationHandler.setCommand(
				ISVNNotifyListener.Command.CAT);
			notificationHandler.logCommandLine(
							"cat -r "
								+ revision.toString()
								+ " "
								+ target);
			notificationHandler.setBaseDir();                
			
            /*
               there's  abit of a gotcha here: the text-base is stored without
               eol-style conversion or keywords! this will result in spurious due
               to line endings and keyword expansion,making external diffing
               of the wc and the text-base useless in the case of client/server
               line terminator mismatch. --mcclain 7/14/2004


			if (revision.equals(SVNRevision.BASE)) {
				// svn should not contact the repository when we want to get base
				// file but it does.
				// Until this is corrected, we get the file directly if we can
				File file = new File(path.getParentFile(),".svn/text-base/"+path.getName()+".svn-base");
				try {
					FileInputStream in = new FileInputStream(file);
					return in;
				} catch (FileNotFoundException e) {
					// we do nothing, we will use svnClient.fileContent instead				
				}
			}
            */
			
			byte[] contents = svnClient.fileContent(target, JhlConverter.convert(revision));
			InputStream input = new ByteArrayInputStream(contents);
			return input;
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}


	/**
	 * returns the svn properties for the given file or directory
	 * @param path
	 * @return
	 * @throws SVNClientException
	 */
	public ISVNProperty[] getProperties(File path) throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.PROPLIST);
			String target = fileToSVNPath(path, true);
			notificationHandler.logCommandLine(
					"proplist "+ target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			PropertyData[] propertiesData = svnClient.properties(target);
			if (propertiesData == null) {
				// no properties
				return new JhlPropertyData[0];
			}
			JhlPropertyData[] svnProperties = new JhlPropertyData[propertiesData.length];
			for (int i = 0; i < propertiesData.length;i++) {
				svnProperties[i] = new JhlPropertyData(propertiesData[i]);  
			}
			return svnProperties;
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}		
	}

    /**
     * set a property
     * @param path
     * @param propertyName
     * @param propertyValue
     * @param recurse
     * @throws ClientException
     */
	public void propertySet(
		File path,
		String propertyName,
		String propertyValue,
		boolean recurse)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.PROPSET);

			String target = fileToSVNPath(path, true);
			notificationHandler.logCommandLine(
				"propset "
					+ propertyName
					+ " \""
					+ propertyValue
					+ "\" "
					+ target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			svnClient.propertySet(target, propertyName, propertyValue, recurse);
			
			// there is no notification (Notify.notify is not called) when we set a property
			// so we will do notification ourselves
			ISVNStatus[] statuses = getStatus(path,recurse,false);
			for (int i = 0; i < statuses.length;i++) {
				notificationHandler.notifyListenersOfChange(statuses[i].getFile().getAbsolutePath());	
			}
			
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}
    
    /**
     * set a property using the content of a file 
     */
	public void propertySet(
		File path,
		String propertyName,
		File propertyFile,
		boolean recurse)
		throws SVNClientException, IOException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.PROPSET);

			String target = fileToSVNPath(path, true);
			notificationHandler.logCommandLine(
				"propset "
					+ propertyName
					+ "-F \""
					+ propertyFile.toString()
					+ "\" "
					+ target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			byte[] propertyBytes;

			FileInputStream is = new FileInputStream(propertyFile);
			propertyBytes = new byte[(int) propertyFile.length()];
			is.read(propertyBytes);

			svnClient.propertySet(target, propertyName, propertyBytes, recurse);

			// there is no notification (Notify.notify is not called) when we set a property
			// so we will do notification ourselves
			ISVNStatus[] statuses = getStatus(path,recurse,false);
			for (int i = 0; i < statuses.length;i++) {
				notificationHandler.notifyListenersOfChange(statuses[i].getFile().getAbsolutePath());	
			}
			
			
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}
    
    /**
     * get a property
     * @param path
     * @param propertyName
     * @param propertyValue
     * @return the property or null if property was not found
     * @throws ClientException
     */
	public ISVNProperty propertyGet(File path, String propertyName)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.PROPGET);

			String target = fileToSVNPath(path, true);
			notificationHandler.logCommandLine(
				"propget " + propertyName + " " + target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			PropertyData propData = svnClient.propertyGet(target, propertyName);
            if (propData == null)
                return null;
            else
			    return new JhlPropertyData(propData);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}

	}

    /**
     * delete a property
     * @param path
     * @param propertyName
     * @param recurse
     * @throws ClientException
     */
    public void propertyDel(File path, String propertyName,boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.PROPDEL);
            
            String target = fileToSVNPath(path, true);
            notificationHandler.logCommandLine("propdel "+propertyName+" "+target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
            
			// propertyRemove is on repository, this will be present on next version of javahl			
			// svnClient.propertyRemove(target, propertyName,recurse);
			// @TODO : change this method when svnjavahl will be upgraded
			// for now we use this workaround 		
            PropertyData propData = svnClient.propertyGet(target,propertyName);
            propData.remove(recurse);
            
		   // there is no notification (Notify.notify is not called) when we set a property
   		   // so we will do notification ourselves
   		   ISVNStatus[] statuses = getStatus(path,recurse,false);
		   for (int i = 0; i < statuses.length;i++) {
			   notificationHandler.notifyListenersOfChange(statuses[i].getFile().getAbsolutePath());	
		   }


        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);            
        }        
    }
    
    /**
     * get the ignored patterns for the given directory
     * if path is not a directory, returns null 
     */
    public List getIgnoredPatterns(File path) throws SVNClientException {
        if (!path.isDirectory())
            return null;
        List list = new ArrayList();
        ISVNProperty pd = propertyGet(path, ISVNProperty.IGNORE);
        if (pd == null)
            return list;
        String patterns = pd.getValue();
        StringTokenizer st = new StringTokenizer(patterns,"\n");
        while (st.hasMoreTokens()) {
            String entry = st.nextToken();
            if (!entry.equals(""))
                list.add(entry);
        }
        return list;
    }
    
    /**
     * add a pattern to svn:ignore property
     * @param must be a directory 
     * @throws SVNClientException
     */
    public void addToIgnoredPatterns(File path, String pattern)  throws SVNClientException {
        List patterns = getIgnoredPatterns(path);
        if (patterns == null) // not a directory
            return;
 
        // verify that the pattern has not already been added
        for (Iterator it = patterns.iterator(); it.hasNext();) {
            if (((String)it.next()).equals(pattern))
                return; // already added
        }
            
        patterns.add(pattern);
        setIgnoredPatterns(path,patterns);
    }

    /**
     * returns the keywords used for substitution for the given resource
     * @param path
     * @return
     * @throws SVNClientException
     */ 
    public SVNKeywords getKeywords(File path) throws SVNClientException {
        ISVNProperty prop = propertyGet(path, ISVNProperty.KEYWORDS);
        if (prop == null)
            return new SVNKeywords(); 

        // value is a space-delimited list of the keywords names
        String value = prop.getValue();
        
        return new SVNKeywords(value);
    }

    /**
     * set the keywords substitution for the given resource
     * @param path
     * @param keywords
     * @param recurse
     * @throws SVNClientException
     */
    public void setKeywords(File path, SVNKeywords keywords, boolean recurse) throws SVNClientException {
        propertySet(path, ISVNProperty.KEYWORDS, keywords.toString(), recurse);
    }

    /**
     * add some keyword to the keywords substitution list
     * @param path
     * @param keywords
     * @return
     * @throws SVNClientException
     */
    public SVNKeywords addKeywords(File path, SVNKeywords keywords) throws SVNClientException {
        SVNKeywords currentKeywords = getKeywords(path);
        if (keywords.isHeadUrl())
            currentKeywords.setHeadUrl(true);
        if (keywords.isId())
            currentKeywords.setId(true);
        if (keywords.isLastChangedBy())
            currentKeywords.setLastChangedBy(true);
        if (keywords.isLastChangedDate())
            currentKeywords.setLastChangedBy(true);
        if (keywords.isLastChangedRevision())
            currentKeywords.setLastChangedRevision(true);
        setKeywords(path,currentKeywords,false);
        
        return currentKeywords;                
    }

    /**
     * remove some keywords to the keywords substitution list
     * @param path
     * @param keywords
     * @return
     * @throws SVNClientException
     */
    public SVNKeywords removeKeywords(File path, SVNKeywords keywords) throws SVNClientException {
        SVNKeywords currentKeywords = getKeywords(path);
        if (keywords.isHeadUrl())
            currentKeywords.setHeadUrl(false);
        if (keywords.isId())
            currentKeywords.setId(false);
        if (keywords.isLastChangedBy())
            currentKeywords.setLastChangedBy(false);
        if (keywords.isLastChangedDate())
            currentKeywords.setLastChangedBy(false);
        if (keywords.isLastChangedRevision())
            currentKeywords.setLastChangedRevision(false);
        setKeywords(path,currentKeywords,false);
        
        return currentKeywords;                
    }


    
    /**
     * set the ignored patterns for the given directory 
     */
    public void setIgnoredPatterns(File path, List patterns) throws SVNClientException {
        if (!path.isDirectory())
            return;
        String value ="";
        for (Iterator it = patterns.iterator(); it.hasNext();) {
            String pattern = (String)it.next();
            value = value + '\n' + pattern;    
        }
        propertySet(path, ISVNProperty.IGNORE, value, false);       
    }

    /**
     * display the differences between two paths. 
     */
    public void diff(File oldPath, SVNRevision oldPathRevision,
                     File newPath, SVNRevision newPathRevision,
                     File outFile, boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.DIFF);
                
            if (oldPath == null)
                oldPath = new File(".");
            if (newPath == null)
                newPath = oldPath;
            if (oldPathRevision == null)
                oldPathRevision = SVNRevision.BASE;
            if (newPathRevision == null)
                newPathRevision = SVNRevision.WORKING;
            
            // we don't want canonical file path (otherwise the complete file name
            // would be in the patch). This way the user can choose to use a relative
            // path
            String oldTarget = fileToSVNPath(oldPath, false);
            String newTarget = fileToSVNPath(newPath, false);
            String svnOutFile = fileToSVNPath(outFile, true);
            
            String commandLine = "diff ";
            if ( (oldPathRevision.getKind() != Revision.Kind.base) ||
                 (newPathRevision.getKind() != Revision.Kind.working) )
            {
                commandLine += "-r "+oldPathRevision.toString();
                if (newPathRevision.getKind() != Revision.Kind.working)
                    commandLine+= ":"+newPathRevision.toString();
                commandLine += " ";         
            }
            if (!oldPath.equals(new File(".")))
                commandLine += "--old "+oldTarget+" ";
            if (!newPath.equals(oldPath))
                commandLine += "--new "+newTarget+" ";
            
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(new File[]{oldPath,newPath}));
            svnClient.diff(oldTarget,JhlConverter.convert(oldPathRevision),newTarget,JhlConverter.convert(newPathRevision), svnOutFile, recurse);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);            
        }
    }

    /**
     * diff between path and head revision
     */
    public void diff(File path, File outFile, boolean recurse) throws SVNClientException {
        diff(path, null,null,null,outFile,recurse);
    }

    /**
     * display the differences between two urls. 
     */
    public void diff(SVNUrl oldUrl, SVNRevision oldUrlRevision,
                     SVNUrl newUrl, SVNRevision newUrlRevision,
                     File outFile, boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.DIFF);
                
            if (newUrl == null)
                newUrl = oldUrl;
            if (oldUrlRevision == null)
                oldUrlRevision = SVNRevision.HEAD;
            if (newUrlRevision == null)
                newUrlRevision = SVNRevision.HEAD;
            
            String svnOutFile = fileToSVNPath(outFile, true);
            
            String commandLine = "diff ";
            if ( (oldUrlRevision.getKind() != Revision.Kind.head) ||
                 (newUrlRevision.getKind() != Revision.Kind.head) )
            {
                commandLine += "-r "+oldUrlRevision.toString();
                if (newUrlRevision.getKind() != Revision.Kind.head)
                    commandLine+= ":"+newUrlRevision.toString();
                commandLine += " ";         
            }
            commandLine += oldUrl+" ";
            if (!newUrl.equals(oldUrl))
                commandLine += newUrl+" ";
            
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir();
            svnClient.diff(oldUrl.toString(),JhlConverter.convert(oldUrlRevision),newUrl.toString(),JhlConverter.convert(newUrlRevision), svnOutFile, recurse);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);            
        }
    }

    public void diff(SVNUrl url, SVNRevision oldUrlRevision, SVNRevision newUrlRevision,
                     File outFile, boolean recurse) throws SVNClientException {
        diff(url,oldUrlRevision,url,newUrlRevision,outFile,recurse);                     
    }

    private ISVNAnnotations annotate(String target, SVNRevision revisionStart, SVNRevision revisionEnd)
    	throws SVNClientException
	{
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.ANNOTATE);
            if(revisionStart == null)
                revisionStart = new SVNRevision.Number(1);
            if(revisionEnd == null)
                revisionEnd = SVNRevision.HEAD;
            String commandLine = "blame ";
            if(revisionEnd != SVNRevision.HEAD || !revisionStart.equals(new SVNRevision.Number(1)))
                commandLine = commandLine + "-r " + revisionStart.toString() + ":" + revisionEnd.toString() + " ";
            commandLine = commandLine + target.toString();
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir();
			
			// this blame method does not seem to work ... 
//			JhlAnnotations annotations = new JhlAnnotations();
//            svnClient.blame(target, JhlConverter.convert(revisionStart), JhlConverter.convert(revisionEnd), annotations);
			
			byte[] annotationBytes = svnClient.blame(target, JhlConverter.convert(revisionStart), JhlConverter.convert(revisionEnd));
            return new CmdLineAnnotations(annotationBytes,"\n");
        } catch (ClientException e) { 
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }

	}
    
    /**
     * Output the content of specified url with revision and 
     * author information in-line. 
     */
    public ISVNAnnotations annotate(SVNUrl url, SVNRevision revisionStart, SVNRevision revisionEnd)
        throws SVNClientException
    {
    	return annotate(url.toString(), revisionStart, revisionEnd);
    }

    /**
     * Output the content of specified file with revision and 
     * author information in-line. 
     */
    public ISVNAnnotations annotate(File file, SVNRevision revisionStart, SVNRevision revisionEnd)
        throws SVNClientException
    {
    	return annotate(fileToSVNPath(file, true), revisionStart, revisionEnd);
    }    
    
    
    /**
     * Remove 'conflicted' state on working copy files or directories
     * @param path
     * @throws SVNClientException
     */    
    public void resolved(File path) 
    	throws SVNClientException
    {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.RESOLVED);
            
			String target = fileToSVNPath(path, true);
			notificationHandler.logCommandLine("resolved "+target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			svnClient.resolved(target,false);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);            
		}        
   	
    }

    
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#createRepository(java.io.File)
	 */
	public void createRepository(File path, String repositoryType) throws SVNClientException {
		try {		
			if (repositoryType == null) {
				repositoryType = REPOSITORY_BDB;
			}
		    notificationHandler.setCommand(ISVNNotifyListener.Command.CREATE_REPOSITORY);
		     
		    String target = fileToSVNPath(path,true);
		    notificationHandler.logCommandLine(
		    		MessageFormat.format(
		    				"create --fstype {0} {1}", 
							new String[] { repositoryType, target }));
		    svnAdmin.create(target, false, false, null, repositoryType);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);            
		}        
	    
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#cancelOperation()
	 */
	public void cancelOperation() throws SVNClientException {
		try {
			svnClient.cancelOperation();
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getInfo(java.io.File)
	 */
	public ISVNInfo getInfo(File path) throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.INFO);
            
			String target = fileToSVNPath(path, true);
			notificationHandler.logCommandLine("info "+target);
			File baseDir = SVNBaseDir.getBaseDir(path);
			notificationHandler.setBaseDir(baseDir);
			
            Info info = svnClient.info(target);
            if (info == null) {
            	return new SVNInfoUnversioned(path);
            } else {
                return new JhlInfo(path,info);    
            }
            
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);            
		}        
	}
    
    public SVNUrl getRepositoryRoot(SVNUrl url) {
        return null;
        
        // the following only works for file:/// urls
/*    	String target = url.toString()+"/FILEWHICHDOESNOTEXIST";
    	try {
            // revision is not important because the repository root
            // is always the same 
            svnClient.logMessages(
                            target, 
                            JhlConverter.convert(SVNRevision.HEAD), 
                            JhlConverter.convert(SVNRevision.HEAD),
                            false,  // don't stop on copy
                            true); // discover paths
            return null;
        } catch (ClientException e) {
            // Filesystem has no item
            // svn: File not found: revision 1, path '/entryTest/FILEWHICHDOESNOTEXIST'
        	e.getMessage();
            RE re = new RE("path '(.+)'");
            if (re.match(e.getMessage())) {
            	String path = re.getParen(1);
            	try {
					return new SVNUrl(target.substring(0,target.length()-path.length()));
				} catch (MalformedURLException e1) {
					return null;
				}
            } else {
            	return null;
            }
        }*/        
    }
    
}
