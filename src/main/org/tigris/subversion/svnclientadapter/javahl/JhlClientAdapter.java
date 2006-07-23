/*******************************************************************************
 * Copyright (c) 2003, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.File;
import java.text.MessageFormat;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.SVNAdmin;
import org.tigris.subversion.javahl.SVNClient;
import org.tigris.subversion.javahl.SVNClientInterface;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * A JavaHL base implementation of {@link ISVNClientAdapter}.
 *
 * @author C�dric Chabanois (cchabanois at no-log.org)
 * @author Panagiotis Korros (pkorros at bigfoot.com) 
 *
 */
public class JhlClientAdapter extends AbstractJhlClientAdapter {

    private SVNAdmin svnAdmin;
    
    private static boolean availabilityCached = false;
    private static boolean available;
	private static StringBuffer javaHLErrors = new StringBuffer("Failed to load JavaHL Library.\nThese are the errors that were encountered:\n");

	/**
	 * Default constructor
	 */
    public JhlClientAdapter() {
        svnClient = new SVNClient();
        svnAdmin = new SVNAdmin();
        notificationHandler = new JhlNotificationHandler();
        svnClient.notification2(notificationHandler);
        svnClient.setPrompt(new DefaultPromptUserPassword());
    }

    /**
     * tells if JhlClientAdapter is usable
     * @return true if Jhl client adapter is available
     */
    public static boolean isAvailable() {
    	if (!availabilityCached) {
	            // if library is already loaded, it will not be reloaded
	
	        	//workaround to solve Subclipse ISSUE #83
    		    // we will ignore these exceptions to handle scenarios where
    		    // javaHL was built diffently.  Ultimately, if javaHL fails to load
    		    // because of a problem in one of these libraries the proper behavior
    		    // will still occur -- meaning JavaHL adapter is disabled.
				if(isOsWindows()) {
					try {
						System.loadLibrary("libapr");
			        } catch (Exception e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        } catch (UnsatisfiedLinkError e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        }
					try {
						System.loadLibrary("libapriconv");
			        } catch (Exception e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        } catch (UnsatisfiedLinkError e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        }
					try {
						System.loadLibrary("libeay32");
			        } catch (Exception e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        } catch (UnsatisfiedLinkError e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        }
					try {
						System.loadLibrary("libdb43");
			        } catch (Exception e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        } catch (UnsatisfiedLinkError e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        }
					try {
						System.loadLibrary("ssleay32");
			        } catch (Exception e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        } catch (UnsatisfiedLinkError e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        }
					try {
						System.loadLibrary("libaprutil");
			        } catch (Exception e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        } catch (UnsatisfiedLinkError e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        }
					try {
						System.loadLibrary("intl3_svn");
			        } catch (Exception e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        } catch (UnsatisfiedLinkError e) {
		               	javaHLErrors.append(e.getMessage()).append("\n");
			        }
				}
	        	//workaround to solve Subclipse ISSUE #83
			available = false;
	        try {
		        /*
		         * see if the user has specified the fully qualified path to the native
		         * library
		         */
		       try
		       {
		            String specifiedLibraryName =
		                    System.getProperty("subversion.native.library");
		            if(specifiedLibraryName != null) {
		                System.load(specifiedLibraryName);
		            	   available = true;
		            }
		        }
		        catch(UnsatisfiedLinkError ex)
		        {
		        		javaHLErrors.append(ex.getMessage()).append("\n");
		        }
		        if (!available) {
		            /*
		             * first try to load the library by the new name.
		             * if that fails, try to load the library by the old name.
		             */
		            try
		            {
		                System.loadLibrary("libsvnjavahl-1");
		            }
		            catch(UnsatisfiedLinkError ex)
		            {
		            	  javaHLErrors.append(ex.getMessage() + "\n");
		               try
		               {
		                    System.loadLibrary("svnjavahl-1");
		               }
		               catch (UnsatisfiedLinkError e)
		               {
		               	javaHLErrors.append(e.getMessage()).append("\n");
	                     System.loadLibrary("svnjavahl");
		               }
		            }
	        	
	            available = true;
		        }
	        } catch (Exception e) {
	        	available = false;
	        	javaHLErrors.append(e.getMessage()).append("\n");
	        } catch (UnsatisfiedLinkError e) {
	        	available = false;
	        	javaHLErrors.append(e.getMessage()).append("\n");
	        } finally {
	        	availabilityCached = true;
	        }
	    	if (!available) {
	    		String libraryPath = System.getProperty("java.library.path");
	    		if (libraryPath != null)
	    			javaHLErrors.append("java.library.path = " + libraryPath);
	    		// System.out.println(javaHLErrors.toString());
	    	} else {
	    	    // At this point, the library appears to be available, but
	    	    // it could be a 1.2.x version of JavaHL.  We have to try
	    	    // to execute a 1.3.x method to be sure.
	            try {
	                SVNClientInterface svnClient = new SVNClient();
	                String dirname = svnClient.getAdminDirectoryName();
               // to remove compiler warning about dirname not being read
	                if (dirname != null)  
	                	available = true;
	            } catch (UnsatisfiedLinkError e) {
	                available = false;
	                javaHLErrors.append("Incompatible JavaHL library loaded.  1.3.x or later required.");
	            }
	    	}
    	}
    		
    	return available;
    }
    
    /**
     * @return an error string describing problems during loading platform native libraries (if any)
     */
    public static String getLibraryLoadErrors() {
        if (isAvailable())
            return "";
        else
            return javaHLErrors.toString();
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
		     
		    String target = fileToSVNPath(path,false);
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

	public boolean statusReturnsRemoteInfo() {
		return true;
	}
}
