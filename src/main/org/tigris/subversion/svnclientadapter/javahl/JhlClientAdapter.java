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

import java.io.File;
import java.text.MessageFormat;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.SVNAdmin;
import org.tigris.subversion.javahl.SVNClientSynchronized;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * An adapter for SVNClient. Easier and safer to use than SVNClient
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

    public JhlClientAdapter() {
        svnClient = new SVNClientSynchronized();
        svnAdmin = new SVNAdmin();
        notificationHandler = new JhlNotificationHandler();
        svnClient.notification2(notificationHandler);
        svnClient.setPrompt(new DefaultPromptUserPassword());
    }

    /**
     * tells if JhlClientAdapter is usable
     * @return
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
	    	    // it could be a 1.1.x version of JavaHL.  We have to try
	    	    // to create an object to be sure.
	            try {
	                SVNClientSynchronized svnClient = new SVNClientSynchronized();
	                JhlNotificationHandler notificationHandler = new JhlNotificationHandler();
	                svnClient.notification2(notificationHandler);
	            } catch (UnsatisfiedLinkError e) {
	                available = false;
	                javaHLErrors.append("Incompatible JavaHL library loaded.  1.2.x or later required.");
	            }
	    	}
    	}
    		
    	return available;
    }
    
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

	/**
	 * Answer whether running on Windows OS.
	 * (Actual code extracted from org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS)
	 * (For such one simple method it does make sense to introduce dependency on whole commons-lang.jar)
	 * @return
	 */
	public static boolean isOsWindows()
	{
        try {
            return System.getProperty("os.name").startsWith("Windows");
        } catch (SecurityException ex) {
            // we are not allowed to look at this property
            return false;
        }
	}

	public boolean statusReturnsRemoteInfo() {
		return true;
	}
}
