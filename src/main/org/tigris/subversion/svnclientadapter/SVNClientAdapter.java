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
package org.tigris.subversion.svnclientadapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.DirEntry;
import org.tigris.subversion.javahl.LogMessage;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.PromptUserPassword;
import org.tigris.subversion.javahl.PropertyData;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.SVNClient;
import org.tigris.subversion.javahl.Status;

/**
 * An adapter for SVNClient. Easier and safer to use than SVNClient
 *
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 *
 */
public class SVNClientAdapter {
    final private static int SVN_ERR_WC_NOT_DIRECTORY = 155007;

    private SVNClient svnClient;
    private ISVNNotifyListener notificationHandler;
    private PromptUserPassword promptUserPasswordHandler;
    private List notifylisteners = new ArrayList();
    

    public SVNClientAdapter() {
        svnClient = new SVNClient();
        notificationHandler = new NotificationHandler();
        svnClient.notification(notificationHandler);        
        svnClient.setPrompt(new DefaultPromptUserPassword());
    }

    /**
     * The notification handler : broadcast to all listeners 
     */
    private class NotificationHandler implements ISVNNotifyListener {

        public void onNotify(
                String path, int action, int kind, String mimeType,
                int contentState, int propState, long revision) {
            for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
                ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
                listener.onNotify(path,action,kind,mimeType,contentState,propState,revision);
            }
        }
        
        public void setCommand(int command) {
            for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
                ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
                listener.setCommand(command);
            }            
        }
    
        public void setCommandLine(String commandLine) {
            for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
                ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
                listener.setCommandLine(commandLine);
            }                        
        }
        
        public void setException(ClientException e) {
            for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
                ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
                listener.setException(e);
            }                                    
        }
        
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
        notifylisteners.add(listener);
    }

    /**
     * Remove a notification listener 
     */
    public void removeNotifyListener(ISVNNotifyListener listener) {
        notifylisteners.remove(listener);
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
        notificationHandler.setCommand(ISVNNotifyListener.COMMAND_UNDEFINED);
        svnClient.password(password);
    }

    /**
     * Register callback interface to supply username and password on demand
     */
    public void setPromptUserPassword(PromptUserPassword prompt) {
        promptUserPasswordHandler = prompt;
        svnClient.setPrompt(prompt);        
    }


    private static String fileToSVNPath(File file) {
    	// SVN need paths with '/' separators
    	try {
			return file.getCanonicalPath().replace('\\', '/');    		 
    	} catch (IOException e)
    	{
    		return null;
    	}
    }
    
    private static String urlToSVNUrl(URL url) {
		// SVN need urls like http://... or file:///, not file:/ 
    	String urlStr = url.toExternalForm();
    	if ( (url.getProtocol().equals("file")) && (url.getHost().equals("")) )
    			urlStr = urlStr.replaceFirst("file:/","file:///");
    	if (urlStr.endsWith("/"))
    		urlStr = urlStr.substring(0,urlStr.length()-1);
    	return urlStr;
    }
    

    /**
     * Adds a file (or directory) to the repository.
     * @exception ClientException
     */
    public void addFile(File file) throws ClientException {
        try{
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_ADD);
            notificationHandler.setCommandLine("add -N "+file.toString());
            svnClient.add(fileToSVNPath(file), false);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }        
    }

    /**
     * Adds a directory to the repository.
     * @exception ClientException
     */
    public void addDirectory(File dir, boolean recurse)
        throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_ADD);            
            notificationHandler.setCommandLine("add -N "+dir.toString());
            svnClient.add(fileToSVNPath(dir), recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
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
        URL moduleName,
        File destPath,
        Revision revision,
        boolean recurse)
        throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_CHECKOUT);
            notificationHandler.setCommandLine(
                "checkout" +
                (recurse?"":" -N") + 
                " -r "+revision.toString()+
                " "+moduleName.toString());        
            svnClient.checkout(
			    urlToSVNUrl(moduleName),
                fileToSVNPath(destPath),
                revision,
                recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
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
        throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_COMMIT);
            String[] files = new String[paths.length];
            String commandLine = "commit -m \""+message+"\"";
            if (!recurse)
                commandLine+=" -N";

            for (int i = 0; i < paths.length; i++) {
                files[i] = fileToSVNPath((File) paths[i]);
                commandLine+=" "+files[i].toString();
            }
            notificationHandler.setCommandLine(commandLine);

            return svnClient.commit(files, message, recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
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
	public DirEntry[] getList(URL url, Revision revision, boolean recurse) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_LS);
            String commandLine = "list -r "+revision.toString()+(recurse?"-R":"")+" "+url.toString();
            notificationHandler.setCommandLine(commandLine);		
            return svnClient.list(urlToSVNUrl(url), revision, recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }
	}


    /**
     * Returns the status of a single file in the path.
     *
     * @param path File to gather status.
     * @return a Status
     */
    public Status getSingleStatus(File path) throws ClientException {
        notificationHandler.setCommand(ISVNNotifyListener.COMMAND_STATUS);
        String filePathSVN = fileToSVNPath(path);
        notificationHandler.setCommandLine("status -N "+filePathSVN);
        try {
            return svnClient.singleStatus(filePathSVN, false);
        } catch (ClientException e) {
            if (e.getAprError() == SVN_ERR_WC_NOT_DIRECTORY) {
                // when there is no .svn dir, an exception is thrown ...
                return new Status(
                    filePathSVN,
                    null,
                    path.isDirectory()?NodeKind.dir:NodeKind.file,
                    -1, // revision
                	-1, // lastchangedrevision
                	0, // lastchangedDate
                	null, // lastcommitauthor
					path.exists() ? Status.Kind.unversioned : Status.Kind.none, // textType
					path.exists() ? Status.Kind.unversioned : Status.Kind.none, // propType
                	Status.Kind.none, // repositoryTextStatus
					Status.Kind.none, // repositoryPropStatus
                	false, // locked
                	false, // copied
                	"", // conflictOld
                	"", // conflictNew
                	""  // conflictWorking
                );
            } else
            {
                notificationHandler.setException(e);
                throw e;
            }
        }
    }

    /**
     * Returns the status of files and directory recursively
     *
     * @param path File to gather status.
     * @return a Status
     */
    public Status[] getStatusRecursively(File path) throws ClientException {
        notificationHandler.setCommand(ISVNNotifyListener.COMMAND_STATUS);
        String filePathSVN = fileToSVNPath(path);
        notificationHandler.setCommandLine("status "+filePathSVN);
        try {
            return svnClient.status(filePathSVN,true, false);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }
    }

    /**
     * copy and schedule for addition (with history)
     * @param srcPath
     * @param destPath
     * @throws ClientException
     */ 
    public void copy(File srcPath, File destPath) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_COPY);
        
            String src = fileToSVNPath(srcPath);
            String dest = fileToSVNPath(destPath);
            notificationHandler.setCommandLine(
                "copy "+src+" "+dest);
            svnClient.copy(src,dest,"",Revision.HEAD); // last two parameters are not used
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }
    }

	/**
	 * immediately commit a copy of WC to URL
	 * @param srcPath
	 * @param destUrl
	 * @throws ClientException
	 */
	public void copy(File srcPath, URL destUrl, String message) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_COPY);
            String src = fileToSVNPath(srcPath);
            String dest = urlToSVNUrl(destUrl);
            notificationHandler.setCommandLine(
                    "copy "+src+" "+dest);
		    svnClient.copy(src,dest,message,Revision.HEAD); // last parameter is not used
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }
	}

	/**
	 * check out URL into WC, schedule for addition
	 * @param srcUrl
	 * @param destPath
	 * @throws ClientException
	 */
	public void copy(URL srcUrl, File destPath, Revision revision) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_COPY);
            String src = urlToSVNUrl(srcUrl);
            String dest = fileToSVNPath(destPath);
            notificationHandler.setCommandLine(
                    "copy "+src+" "+dest);
            svnClient.copy(src,dest,"",revision);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }
	}
	
	/**
	 * complete server-side copy;  used to branch & tag
	 * @param srcUrl
	 * @param destUrl
	 * @throws ClientException
	 */
	public void copy(URL srcUrl, URL destUrl, String message, Revision revision) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_COPY);
            String src = urlToSVNUrl(srcUrl);
            String dest = urlToSVNUrl(destUrl);
            notificationHandler.setCommandLine(
                    "copy "+src+" "+dest);

		    svnClient.copy(src,dest,message,revision);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }
	}

	/**
	 * item is deleted from the repository via an immediate commit.
	 * @param url
	 * @param message
	 * @throws ClientException
	 */
	public void remove(URL url[], String message) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_REMOVE);

            String commandLine = "delete -m \""+message+"\"";
            
            String targets[] = new String[url.length];
            for (int i = 0; i < url.length;i++) {
                targets[i] = urlToSVNUrl(url[i]); 
                commandLine += " "+targets[i];
            }
            notificationHandler.setCommandLine(commandLine);
		    svnClient.remove(targets,message,false);
            
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
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
	public void remove(File file[], boolean force) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_REMOVE);
            
            String commandLine = "delete"+(force?" --force":"");
            String targets[] = new String[file.length];
            
            for (int i = 0; i < file.length;i++) {
                targets[i] = fileToSVNPath(file[i]);
                commandLine += " "+targets[i];
            }
            
            notificationHandler.setCommandLine(commandLine);
   
            svnClient.remove(targets,"",force);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
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
	public void doExport(URL srcUrl, File destPath, Revision revision, boolean force) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_EXPORT);
            String src = urlToSVNUrl(srcUrl);
            String dest = fileToSVNPath(destPath);
            notificationHandler.setCommandLine(
                "export -r "+revision.toString()+ ' '+src+' '+dest);
            
            svnClient.doExport(src,dest,revision,force);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
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
	public void doExport(File srcPath, File destPath, boolean force) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_EXPORT);
            String src = fileToSVNPath(srcPath);
            String dest = fileToSVNPath(destPath);
            notificationHandler.setCommandLine(
                 "export "+src+' '+dest);
            // in this case, revision is not used but must be valid
		    svnClient.doExport(src,dest,Revision.HEAD, force);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
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
	public void doImport(File path, URL url, String message, boolean recurse) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_IMPORT);
            String src = fileToSVNPath(path);
            String dest = urlToSVNUrl(url);
            notificationHandler.setCommandLine(
                         "import -m \""+message+"\" "+
                         (recurse?"":"-N ")+
                         src+' '+dest);
            svnClient.doImport(src, dest,message, recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }			
	}

	/**
	 * Creates a directory directly in a repository
	 * @param url
	 * @param message
	 * @throws ClientException
	 */
	public void mkdir(URL url, String message) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_MKDIR);
		    String target = urlToSVNUrl(url);
            notificationHandler.setCommandLine(
                "mkdir -m \""+message+"\" "+target);
            svnClient.mkdir(new String[] { target },message);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }                   	
	}
	
	/**
	 * creates a directory on disk and schedules it for addition.
	 * @param file
	 * @throws ClientException
	 */
	public void mkdir(File file) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_MKDIR);
            String target = fileToSVNPath(file);
            notificationHandler.setCommandLine(
                "mkdir "+target);
            svnClient.mkdir(new String[] { target },"");
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }           	
	}

	/**
	 * Moves or renames a file.
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */	
	public void move(File srcPath, File destPath, boolean force) throws ClientException {
        // use force when you want to move file even if there are local modifications
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_MOVE);
		    String src = fileToSVNPath(srcPath);
            String dest = fileToSVNPath(destPath);
            notificationHandler.setCommandLine(
                    "move "+src+' '+dest);        
            svnClient.move(src,dest,"",Revision.HEAD,force);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }                   	
	}

	/**
	 * Moves or renames a file.
	 * @param srcPath
	 * @param destPath
	 * @throws ClientException
	 */	
	public void move(URL srcUrl, URL destUrl, String message, Revision revision) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_MOVE);
            String src = urlToSVNUrl(srcUrl);
            String dest = urlToSVNUrl(destUrl);
            notificationHandler.setCommandLine(
                "move -m \""+message+"\" -r "+revision.toString()+' '+src+' '+dest); 
            svnClient.move(src,dest,message,revision,false);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }           	
	}	

	/**
	 * Update a file or a directory
	 * @param path
	 * @param revision
	 * @param recurse
	 * @throws ClientException
	 */
	public void update(File path, Revision revision, boolean recurse) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_UPDATE);
		    String target = fileToSVNPath(path);
            notificationHandler.setCommandLine(
                "update -r "+revision.toString()+' '+
                (recurse?"":"-N ")+
                target); 
            svnClient.update(target, revision, recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }           	 
	}

    /**
     * Restore pristine working copy file (undo all local edits)
     * @param path
     * @param recurse
     * @throws ClientException
     */
    public void revert(File path, boolean recurse) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_REVERT);
            String target = fileToSVNPath(path);
            notificationHandler.setCommandLine(
                "revert "+
                (recurse?"":"-N ")+
                target); 
            svnClient.revert(target,recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }         
    }

    /**
     * Get the log messages for a set of revision(s) 
     * @param url
     * @param revisionStart
     * @param revisionEnd
     * @return
     */
    public LogMessage[] getLogMessages(URL url, Revision revisionStart, Revision revisionEnd) throws ClientException 
    {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_LOG);
            String target = urlToSVNUrl(url);
            notificationHandler.setCommandLine("log -r "+revisionStart.toString()+":"+revisionEnd.toString()+
                " "+target);
        
            return svnClient.logMessages(target, revisionStart, revisionEnd);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }    
    } 
    
    /**
     * Get the log messages for a set of revision(s)
     * @param path
     * @param revisionStart
     * @param revisionEnd
     * @return
     */
    public LogMessage[] getLogMessages(File path, Revision revisionStart, Revision revisionEnd) throws ClientException 
    {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_UNDEFINED);
            String target = fileToSVNPath(path);
            notificationHandler.setCommandLine("log -r "+revisionStart.toString()+":"+revisionEnd.toString()+
                    " "+target);
            return svnClient.logMessages(target, revisionStart, revisionEnd);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
        }    
    }     


	/**
	 * enables logging
	 * @param logLevel
	 * @param filePath
	 */	
	public static void enableLogging(int logLevel,File filePath) {
		SVNClient.enableLogging(logLevel,fileToSVNPath(filePath));	
	}

    /**
     * get the content of a file
     * @param url
     * @param revision
     */
    public InputStream getContent(URL url, Revision revision) throws ClientException
    {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_UNDEFINED);
            byte[] contents = svnClient.fileContent(urlToSVNUrl(url),revision);
            InputStream input = new ByteArrayInputStream(contents);
            return input;
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;
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
    public void propertySet(File path, String propertyName, String propertyValue, boolean recurse) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_PROPSET);
            
            String target = fileToSVNPath(path);
            notificationHandler.setCommandLine("propset "+propertyName+ " \""+propertyValue+"\" "+target);
                    
            svnClient.propertySet(target, propertyName, propertyValue, recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;            
        }
    }
    
    /**
     * set a property using the content of a file 
     */
    public void propertySet(File path, String propertyName, File propertyFile, boolean recurse) throws ClientException, IOException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_PROPSET);
            
            String target = fileToSVNPath(path);
            notificationHandler.setCommandLine("propset "+propertyName+ "-F \""+propertyFile.toString()+"\" "+target);

            byte[] propertyBytes;
         
            FileInputStream is = new FileInputStream(propertyFile);
            propertyBytes = new byte[(int)propertyFile.length()]; 
            is.read(propertyBytes);
                    
            svnClient.propertySet(target, propertyName, propertyBytes, recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;            
        }
    }
    
    /**
     * get a property
     * @param path
     * @param propertyName
     * @param propertyValue
     * @return
     * @throws ClientException
     */
    public PropertyData propertyGet(File path, String propertyName) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_PROPSET);
                
            String target = fileToSVNPath(path);
            notificationHandler.setCommandLine("propget "+propertyName+" "+target);
                        
            PropertyData propData = svnClient.propertyGet(target, propertyName);
            return propData;
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;            
        }
    
    }

    /**
     * delete a property
     * @param path
     * @param propertyName
     * @param recurse
     * @throws ClientException
     */
    public void propertyDel(File path, String propertyName,boolean recurse) throws ClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.COMMAND_PROPDEL);
            
            String target = fileToSVNPath(path);
            notificationHandler.setCommandLine("propdel "+propertyName+" "+target);
                    
            svnClient.propertySet(target, propertyName, (String)null, recurse);
        } catch (ClientException e) {
            notificationHandler.setException(e);
            throw e;            
        }        
    }
    
    /**
     * get the ignored patterns for the given directory
     * if path is not a directory, returns null 
     */
    public List getIgnoredPatterns(File path) throws ClientException {
        if (!path.isDirectory())
            return null;
        List list = new ArrayList();
        PropertyData pd = propertyGet(path, "svn:ignore");
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
     */
    public void addToIgnoredPatterns(File path, String pattern)  throws ClientException {
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
     * set the ignored patterns for the given directory 
     */
    public void setIgnoredPatterns(File path, List patterns) throws ClientException {
        if (!path.isDirectory())
            return;
        String value ="";
        for (Iterator it = patterns.iterator(); it.hasNext();) {
            String pattern = (String)it.next();
            value = value + '\n' + pattern;    
        }
        propertySet(path, "svn:ignore", value, false);       
    }
}