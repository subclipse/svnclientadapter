/*
 *  Copyright(c) 2003-2004 by the authors indicated in the @author tags.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.Info;
import org.tigris.subversion.javahl.Info2;
import org.tigris.subversion.javahl.PromptUserPassword;
import org.tigris.subversion.javahl.PropertyData;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.RevisionKind;
import org.tigris.subversion.javahl.SVNClient;
import org.tigris.subversion.javahl.SVNClientInterface;
import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.svnclientadapter.AbstractClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNAnnotations;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.Policy;
import org.tigris.subversion.svnclientadapter.SVNBaseDir;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNInfoUnversioned;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNStatusUnversioned;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * This is a base class for the JavaHL Adapter.  It allows the JavaHL
 * Adapter and the JavaSVN Adapter to share most of their implementation.
 * 
 * The JavaSVN Adapter works by providing an implementation of the JavaHL
 * SVNClientInterface. 
 *
 */
public abstract class AbstractJhlClientAdapter extends AbstractClientAdapter {
    final protected static int SVN_ERR_WC_NOT_DIRECTORY = 155007;

    protected SVNClientInterface svnClient;
    protected JhlNotificationHandler notificationHandler;

    public AbstractJhlClientAdapter() {

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
    public static class DefaultPromptUserPassword implements PromptUserPassword {

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
        svnClient.setPrompt(prompt);        
    }


    protected static String fileToSVNPath(File file, boolean canonical) {
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
            svnClient.add(fileToSVNPath(file, false), false);
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
            svnClient.add(fileToSVNPath(dir, false), recurse);
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
                fileToSVNPath(destPath, false),
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
        return commit(paths, message, recurse, false);
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
    public long commit(File[] paths, String message, boolean recurse, boolean keepLocks)
        throws SVNClientException {
        try {
        	if (message == null)
        		message = "";
            notificationHandler.setCommand(ISVNNotifyListener.Command.COMMIT);
            String[] files = new String[paths.length];
            String commandLine = "commit -m \""+message+"\"";
            if (!recurse)
                commandLine+=" -N";
            if (keepLocks)
                commandLine+=" --no-unlock";

            for (int i = 0; i < paths.length; i++) {
                files[i] = fileToSVNPath(paths[i], false);
                commandLine+=" "+ files[i];
            }
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(paths));

            long newRev = svnClient.commit(files, message, recurse, keepLocks);
            if (newRev > 0)
            	notificationHandler.logCompleted("Committed revision " + newRev + ".");
            return newRev;
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
            String target = fileToSVNPath(path, false);
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
		String expectedPath = url.getLastPathSegment();
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
            filePathSVN[i] = fileToSVNPath(path[i], false);
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
		String filePathSVN = fileToSVNPath(path, false);
		notificationHandler.logCommandLine("status " + (contactServer?"-u ":"")+ filePathSVN);
		notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
		try {
			return JhlConverter.convert(
                svnClient.status(
                    filePathSVN,  
                    descend,            // If descend is true, recurse fully, else do only immediate children.
                    contactServer,      // If update is set, contact the repository and augment the status structures with information about out-of-dateness     
					getAll,getAll));    // retrieve all entries; otherwise, retrieve only "interesting" entries (local mods and/or out-of-date).
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

			String src = fileToSVNPath(srcPath, false);
			String dest = fileToSVNPath(destPath, false);
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
        	if (message == null)
        		message = "";
			notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
			String src = fileToSVNPath(srcPath, false);
			String dest = destUrl.toString();
			notificationHandler.logCommandLine("copy " + src + " " + dest);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(srcPath));
			svnClient.copy(src, dest, message, Revision.WORKING);
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
			String dest = fileToSVNPath(destPath, false);
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
        	if (message == null)
        		message = "";
			notificationHandler.setCommand(ISVNNotifyListener.Command.COPY);
			String src = srcUrl.toString();
			String dest = destUrl.toString();
			notificationHandler.logCommandLine("copy -r" + revision.toString() + " " + src + " " + dest);
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
        	if (message == null)
        		message = "";
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
                targets[i] = fileToSVNPath(file[i], false);
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
			String dest = fileToSVNPath(destPath, false);
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
			String src = fileToSVNPath(srcPath, false);
			String dest = fileToSVNPath(destPath, false);
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
        	if (message == null)
        		message = "";
			notificationHandler.setCommand(ISVNNotifyListener.Command.IMPORT);
			String src = fileToSVNPath(path, false);
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
			notificationHandler.logCompleted(Policy.bind("notify.import.complete"));
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
        	if (message == null)
        		message = "";
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
            String target = fileToSVNPath(file, false);
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
		    String src = fileToSVNPath(srcPath, false);
            String dest = fileToSVNPath(destPath, false);
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
        	if (message == null)
        		message = "";
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
	public long update(File path, SVNRevision revision, boolean recurse)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.UPDATE);
			String target = fileToSVNPath(path, false);
			notificationHandler.logCommandLine(
				"update -r "
					+ revision.toString()
					+ ' '
					+ (recurse ? "" : "-N ")
					+ target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			return svnClient.update(target, JhlConverter.convert(revision), recurse);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
	}

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#update(java.io.File[], org.tigris.subversion.svnclientadapter.SVNRevision, boolean, boolean)
     */
    public long[] update(File[] path, SVNRevision revision, boolean recurse, boolean ignoreExternals) 
        throws SVNClientException
	{
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.UPDATE);
			String[] targets = new String[path.length];
			StringBuffer targetsString = new StringBuffer();
			for (int i = 0; i < targets.length; i++) {
				targets[i] = fileToSVNPath(path[i], false);
				targetsString.append(targets[i]);
				targetsString.append(" ");
			}
			notificationHandler.logCommandLine(
				"update -r "
					+ revision.toString()
					+ ' '
					+ (recurse ? "" : "-N ")
					+ (ignoreExternals ? "--ignore-externals " : "")
					+ targetsString.toString());
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			notificationHandler.holdStats();
			long[] rtnCode =  svnClient.update(targets, JhlConverter.convert(revision), recurse, ignoreExternals);
			notificationHandler.releaseStats();
			return rtnCode;
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
            String target = fileToSVNPath(path, false);
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
	 * @param fetchChangePath
     * @return
     */
	public ISVNLogMessage[] getLogMessages(
		SVNUrl url,
		SVNRevision revisionStart,
		SVNRevision revisionEnd,
		boolean fetchChangePath)
		throws SVNClientException {
		String target = url.toString();;
		notificationHandler.setBaseDir();
        return this.getLogMessages(target, revisionStart, revisionEnd, false, fetchChangePath, 0);
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
		SVNRevision revisionEnd,
		boolean fetchChangePath)
		throws SVNClientException {
		String target = fileToSVNPath(path, false);
		notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
	    return this.getLogMessages(target, revisionStart, revisionEnd, false, fetchChangePath, 0);
	}    


	/**
	 * enables logging
	 * @param logLevel
	 * @param filePath
	 */	
	public static void enableLogging(int logLevel,File filePath) {
		SVNClient.enableLogging(logLevel,fileToSVNPath(filePath, false));	
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
			
			byte[] contents = svnClient.fileContent(url.toString(), JhlConverter.convert(revision), Revision.HEAD);
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
			String target = fileToSVNPath(path, false);
			notificationHandler.setCommand(
				ISVNNotifyListener.Command.CAT);
			notificationHandler.logCommandLine(
							"cat -r "
								+ revision.toString()
								+ " "
								+ target);
			notificationHandler.setBaseDir();                
			
			if (revision.equals(SVNRevision.BASE)) {
			    // This is to work-around a JavaHL problem when trying to
			    // retrieve the base revision of a newly added file.
			    ISVNStatus status = getSingleStatus(path);
			    if (status.getTextStatus().equals(SVNStatusKind.ADDED))
			        return new ByteArrayInputStream(new byte[0]);
			}
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
			String target = fileToSVNPath(path, false);
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
	 * returns the svn properties for the given url
	 * @param url
	 * @return
	 * @throws SVNClientException
	 */
	public ISVNProperty[] getProperties(SVNUrl url) throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.PROPLIST);
			String target = url.toString();
			notificationHandler.logCommandLine(
					"proplist "+ target);
			notificationHandler.setBaseDir();
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

			String target = fileToSVNPath(path, false);
			notificationHandler.logCommandLine(
				"propset "
					+ (recurse?"-R ":"")
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
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.PROPSET);

			String target = fileToSVNPath(path, false);
			notificationHandler.logCommandLine(
				"propset "
					+ (recurse?"-R ":"")
					+ propertyName
					+ "-F \""
					+ propertyFile.toString()
					+ "\" "
					+ target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			byte[] propertyBytes = new byte[(int) propertyFile.length()];

			FileInputStream is = null;
			try {
				is = new FileInputStream(propertyFile);
				is.read(propertyBytes);
			}
			catch (IOException ioe) {
				throw new SVNClientException(ioe);
			}
			finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}

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

			String target = fileToSVNPath(path, false);
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
     * get a property
     * @param url
     * @param propertyName
     * @param propertyValue
     * @return the property or null if property was not found
     * @throws ClientException
     */
	public ISVNProperty propertyGet(SVNUrl url, String propertyName)
		throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.PROPGET);

			String target = url.toString();
			notificationHandler.logCommandLine(
				"propget " + propertyName + " " + target);
			notificationHandler.setBaseDir();
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
            
            String target = fileToSVNPath(path, false);
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
            String svnOutFile = fileToSVNPath(outFile, false);
            
            String commandLine = "diff ";
            if ( (oldPathRevision.getKind() != RevisionKind.base) ||
                 (newPathRevision.getKind() != RevisionKind.working) )
            {
                commandLine += "-r "+oldPathRevision.toString();
                if (newPathRevision.getKind() != RevisionKind.working)
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
            
            String svnOutFile = fileToSVNPath(outFile, false);
            
            String commandLine = "diff ";
            if ( (oldUrlRevision.getKind() != RevisionKind.head) ||
                 (newUrlRevision.getKind() != RevisionKind.head) )
            {
                commandLine += "-r "+oldUrlRevision.toString();
                if (newUrlRevision.getKind() != RevisionKind.head)
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
            commandLine = commandLine + "-r " + revisionEnd.toString() + " ";
            commandLine = commandLine + target + "@HEAD";
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir();
			
			JhlAnnotations annotations = new JhlAnnotations();
            svnClient.blame(target, Revision.HEAD, JhlConverter.convert(revisionStart), JhlConverter.convert(revisionEnd), annotations);
            return annotations;
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
    	return annotate(fileToSVNPath(file, false), revisionStart, revisionEnd);
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
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getInfoFromWorkingCopy(java.io.File)
	 */
	public ISVNInfo getInfoFromWorkingCopy(File path) throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.INFO);
            
			String target = fileToSVNPath(path, false);
			notificationHandler.logCommandLine("info "+target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			
			Info info = svnClient.info(target);
            if (info == null) {
            	return new SVNInfoUnversioned(path);
            } 
            return new JhlInfo(path, info);
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
            
			String target = fileToSVNPath(path, false);
			notificationHandler.logCommandLine("info "+target);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
			
			//Call the simple info() first to check whether the resource actually exists in repositiory.
			//If yes, the call info2() later to get more data from the repository.
			Info info = svnClient.info(target);
            if (info == null) {
            	return new SVNInfoUnversioned(path);
            } else if (info.getUuid() == null)
			{
				//Item is not in repository (yet or anymore ?)
                return new JhlInfo(path, info);
			}
			
            Info2[] info2 = svnClient.info2(target, Revision.HEAD, Revision.HEAD, false);
            if (info2 == null || info2.length == 0) {
            	return new SVNInfoUnversioned(path);
            } else {
                return new JhlInfo2(path,info2[0]);
            }
            
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);            
		}        
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getInfo(org.tigris.subversion.svnclientadapter.SVNUrl)
	 */
	public ISVNInfo getInfo(SVNUrl url) throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.INFO);
            
			String target = url.toString();
			notificationHandler.logCommandLine("info "+target);
//			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(url));
			
            Info2[] info = svnClient.info2(target, Revision.HEAD, Revision.HEAD, false);
            if (info == null || info.length == 0) {
            	return new SVNInfoUnversioned(null);
            } else {
                return new JhlInfo2(null, info[0]);    
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

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#switchUrl(org.tigris.subversion.svnclientadapter.SVNUrl, java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
    public void switchToUrl(File path, SVNUrl url, SVNRevision revision, boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.SWITCH);
            
            String target = fileToSVNPath(path, false);
            String commandLine = "switch "+url+" "+target+" "+"-r"+revision.toString();
            if (!recurse) {
            	commandLine += " -N";
            }
            notificationHandler.logCommandLine(commandLine);
            File baseDir = SVNBaseDir.getBaseDir(path);
            notificationHandler.setBaseDir(baseDir);

            svnClient.doSwitch(target, url.toString(),JhlConverter.convert(revision),recurse);
           
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);            
        }        
    	
    }

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setConfigDirectory(java.io.File)
	 */
	public void setConfigDirectory(File dir) throws SVNClientException {
        try {
        	svnClient.setConfigDirectory(fileToSVNPath(dir,false));
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);            
        }
	}

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#cleanup(java.io.File)
     */
    public void cleanup(File path) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.CLEANUP);
            String target = fileToSVNPath(path, false);
            String commandLine = "cleanup " + target;
            notificationHandler.logCommandLine(commandLine);
            svnClient.cleanup(target);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
	/* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#merge(java.lang.String, org.tigris.subversion.svnclientadapter.SVNRevision, java.lang.String, org.tigris.subversion.svnclientadapter.SVNRevision, java.lang.String, boolean, boolean)
     */
    public void merge(SVNUrl path1, SVNRevision revision1, SVNUrl path2,
    		SVNRevision revision2, File localPath, boolean force,
    		boolean recurse) throws SVNClientException {
        merge(path1, revision1, path2, revision2, localPath, force, recurse, false);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#merge(org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File, boolean, boolean, boolean)
     */
    public void merge(SVNUrl path1, SVNRevision revision1, SVNUrl path2,
            SVNRevision revision2, File localPath, boolean force,
            boolean recurse, boolean dryRun) throws SVNClientException {
    	try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.MERGE);
            
            String target = fileToSVNPath(localPath, false);
            String commandLine = "merge";
            if (!recurse) {
            	commandLine += " -N";
            }
            if (dryRun) {
            	commandLine += " --dry-run";
            }
            if (force) {
            	commandLine += " --force";
            }
            if (path1.toString().equals(path2.toString())) {
            	commandLine += " -r" + revision1.toString() + ":" + revision2.toString() + " " + path1;
            } else {
            	commandLine += " " + path1 + "@" + revision1.toString() + " " + path2 + "@" + revision2.toString();
            }
            commandLine += " " + target;
            notificationHandler.logCommandLine(commandLine);
            File baseDir = SVNBaseDir.getBaseDir(localPath);
            notificationHandler.setBaseDir(baseDir);
    
            svnClient.merge(path1.toString(), JhlConverter.convert(revision1), path2.toString(), JhlConverter.convert(revision2), target, force, recurse, false, dryRun );
            if (dryRun)
                notificationHandler.logCompleted("Dry-run merge complete.");
            else
                notificationHandler.logCompleted("Merge complete.");
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);            
        }        
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addPasswordCallback(org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword)
     */
    public void addPasswordCallback(ISVNPromptUserPassword callback) {
        if (callback != null) {
	        JhlPromptUserPassword prompt = new JhlPromptUserPassword(callback);
	        this.setPromptUserPassword(prompt);
        }
    }
    /**
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#lock(SVNUrl[], java.lang.String, boolean)
     */
    public void lock(SVNUrl[] uris, String comment, boolean force)
            throws SVNClientException {
        notImplementedYet();
    }

    /**
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#unlock(SVNUrl[], boolean)
     */
    public void unlock(SVNUrl[] uris, boolean force)
        throws SVNClientException {
        notImplementedYet();
    
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#lock(java.lang.String[], java.lang.String, boolean)
     */
    public void lock(File[] paths, String comment, boolean force)
            throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.LOCK);
            String[] files = new String[paths.length];
            String commandLine = "lock -m \""+comment+"\"";
            if (force)
                commandLine+=" --force";

            for (int i = 0; i < paths.length; i++) {
                files[i] = fileToSVNPath(paths[i], false);
                commandLine+=" "+files[i];
            }
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(paths));

            svnClient.lock(files, comment, force);
            for (int i = 0; i < files.length; i++) {
                notificationHandler.notifyListenersOfChange(files[i]);
            }
        } catch (ClientException e) {
            notificationHandler.logException(e);
//            throw new SVNClientException(e);
        }

    }
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#unlock(java.lang.String[], boolean)
     */
    public void unlock(File[] paths, boolean force) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.LOCK);
            String[] files = new String[paths.length];
            String commandLine = "unlock ";
            if (force)
                commandLine+=" --force";
    
            for (int i = 0; i < paths.length; i++) {
                files[i] = fileToSVNPath(paths[i], false);
                commandLine+=" "+files[i];
            }
            notificationHandler.logCommandLine(commandLine);
    		notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(paths));
    
            svnClient.unlock(files, force);
            for (int i = 0; i < files.length; i++) {
                notificationHandler.notifyListenersOfChange(files[i]);
            }
        } catch (ClientException e) {
            notificationHandler.logException(e);
 //           throw new SVNClientException(e);
        }
    
    }

	public void setRevProperty(SVNUrl url, SVNRevision.Number revisionNo, String propName, String propertyData, boolean force) throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.PROPSET);

			notificationHandler.logCommandLine(
				"propset --revprop -r " + revisionNo.toString()
					+ (force ? "--force " : "")
					+ " \""
					+ propName
					+ "\"  \""
					+ propertyData
					+ "\" "
					+ url.toString());
			notificationHandler.setBaseDir();
			svnClient.setRevProperty(url.toString(), propName, Revision.getInstance(revisionNo.getNumber()), propertyData, true);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}		
	}

	public String getAdminDirectoryName() {
		return svnClient.getAdminDirectoryName();
	}

	public boolean isAdminDirectory(String name) {
		return svnClient.isAdminDirectory(name);
	}
    private ISVNLogMessage[] getLogMessages(String target,
            SVNRevision revisionStart, SVNRevision revisionEnd,
            boolean stopOnCopy, boolean fetchChangePath, long limit)
            throws SVNClientException {
		try {
			notificationHandler.setCommand(
				ISVNNotifyListener.Command.LOG);
			String logExtras = "";
			if (stopOnCopy)
			    logExtras = logExtras + " --stop-on-copy";
			if (limit > 0 )
			    logExtras = logExtras + " --limit " + limit;
			notificationHandler.logCommandLine(
				"log -r "
					+ revisionStart.toString()
					+ ":"
					+ revisionEnd.toString()
					+ " "
					+ target
					+ logExtras);
			return JhlConverter.convert(
                    svnClient.logMessages(
                            target, 
                            JhlConverter.convert(revisionStart), 
                            JhlConverter.convert(revisionEnd),
                            stopOnCopy, 
                            fetchChangePath, 
                            limit));  
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);
		}
    }
    
    public ISVNLogMessage[] getLogMessages(File path,
            SVNRevision revisionStart, SVNRevision revisionEnd,
            boolean stopOnCopy, boolean fetchChangePath, long limit)
            throws SVNClientException {
			String target = fileToSVNPath(path, false);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
	        return this.getLogMessages(target, revisionStart, revisionEnd, stopOnCopy, fetchChangePath, limit);
    }
    
    public ISVNLogMessage[] getLogMessages(SVNUrl url, SVNRevision pegRevision,
            SVNRevision revisionStart, SVNRevision revisionEnd,
            boolean stopOnCopy, boolean fetchChangePath, long limit)
            throws SVNClientException {
			String target = url.toString();;
			notificationHandler.setBaseDir();
	        return this.getLogMessages(target, revisionStart, revisionEnd, stopOnCopy, fetchChangePath, limit);
    }
    
    public ISVNLogMessage[] getLogMessages(File path,
            SVNRevision revisionStart, SVNRevision revisionEnd,
            boolean stopOnCopy, boolean fetchChangePath)
            throws SVNClientException {
		String target = fileToSVNPath(path, false);
		notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
        return this.getLogMessages(target, revisionStart, revisionEnd, stopOnCopy, fetchChangePath, 0);
    }
    
    public void relocate(String from, String to, String path, boolean recurse)
            throws SVNClientException {
		try {
			notificationHandler.setCommand(ISVNNotifyListener.Command.RELOCATE);
			if (recurse)
			    notificationHandler.logCommandLine("switch --relocate "+ from + " " + to + " " + path);
			else
			    notificationHandler.logCommandLine("switch --relocate -N"+ from + " " + to + " " + path);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(new File(path)));
			svnClient.relocate(from, to, path, recurse);
		} catch (ClientException e) {
			notificationHandler.logException(e);
			throw new SVNClientException(e);            
		}        
    }
    
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(java.io.File, org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File, boolean)
	 */
	public void diff(File path, SVNUrl url, SVNRevision urlRevision,
			File outFile, boolean recurse) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.DIFF);
                
            // we don't want canonical file path (otherwise the complete file name
            // would be in the patch). This way the user can choose to use a relative
            // path
            String wcPath = fileToSVNPath(path, false);
            String svnOutFile = fileToSVNPath(outFile, false);
            
            String commandLine = "diff --old " + wcPath + " ";
           	commandLine += "--new " + url.toString();
            if (!urlRevision.equals(SVNRevision.HEAD))
            	commandLine += "@"+ urlRevision.toString();
            
            notificationHandler.logCommandLine(commandLine);
			notificationHandler.setBaseDir(SVNBaseDir.getBaseDir(path));
            svnClient.diff(wcPath,Revision.WORKING,url.toString(),JhlConverter.convert(urlRevision), svnOutFile, recurse);
        } catch (ClientException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);            
        }
	}
}
