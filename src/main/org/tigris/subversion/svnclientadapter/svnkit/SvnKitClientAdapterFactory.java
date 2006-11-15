/*******************************************************************************
 * Copyright (c) 2005, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.svnkit;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * Concrete implementation of SVNClientAdapterFactory for SVNKit interface.
 * To register this factory, just call {@link SvnKitClientAdapterFactory#setup()} 
 */
public class SvnKitClientAdapterFactory extends SVNClientAdapterFactory {
	
	/** Client adapter implementation identifier */
    public static final String SVNKIT_CLIENT = "svnkit";
    
	/**
	 * Private constructor.
	 * Clients are expected the use {@link #createSVNClientImpl()}, res.
	 * ask the {@link SVNClientAdapterFactory}
	 */
    private SvnKitClientAdapterFactory() {
    	super();
    }

    /* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#createSVNClientImpl()
	 */
	protected ISVNClientAdapter createSVNClientImpl() {
		return new SvnKitClientAdapter();
	}

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#getClientType()
     */
    protected String getClientType() {
        return SVNKIT_CLIENT;
    }
    
    public static boolean isAvailable() {
        try {
            Class c = Class.forName("org.tmatesoft.svn.core.javahl.SVNClientImpl");
            if (c != null)
            	return true;
            else
            	return false;
        } catch (Throwable t) {
            return false;
        }
    }
    
    /**
     * Setup the client adapter implementation and register it in the adapters factory
     * @throws SVNClientException
     */
    public static void setup() throws SVNClientException {
        if (!isAvailable()) {
            throw new SVNClientException("SVNKit client adapter is not available");
        }
        SVNClientAdapterFactory.registerAdapterFactory(new SvnKitClientAdapterFactory());
    }
  
}
