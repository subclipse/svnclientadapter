/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.commandline;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * Concrete implementation of SVNClientAdapterFactory for command line interface.
 * To register this factory, just call {@link CmdLineClientAdapterFactory#setup()} 
 */
public class CmdLineClientAdapterFactory extends SVNClientAdapterFactory {
	
	/** Client adapter implementation identifier */
    public static final String COMMANDLINE_CLIENT = "commandline";
    
    private static boolean is13ClientAvailable = false;
    
	/**
	 * Private constructor.
	 * Clients are expected the use {@link #createSVNClientImpl()}, res.
	 * ask the {@link SVNClientAdapterFactory}
	 */
    private CmdLineClientAdapterFactory() {
    	super();
    }

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#createSVNClientImpl()
	 */
	protected ISVNClientAdapter createSVNClientImpl() {
		if (is13ClientAvailable) {
			return new CmdLineClientAdapter(new CmdLineNotificationHandler());
		} else {
			return new CmdLineClientAdapter12(new CmdLineNotificationHandler());
		}
	}

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#getClientType()
     */
    protected String getClientType() {
        return COMMANDLINE_CLIENT;
    }    
    
    /**
     * Setup the client adapter implementation and register it in the adapters factory
     * @throws SVNClientException
     */
    public static void setup() throws SVNClientException {
        if (!CmdLineClientAdapter12.isAvailable()) {
            throw new SVNClientException("Command line client adapter is not available");
        }
        
        is13ClientAvailable = CmdLineClientAdapter.isAvailable();
        
        SVNClientAdapterFactory.registerAdapterFactory(new CmdLineClientAdapterFactory());
    }

}
