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
package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * Concrete implementation of SVNClientAdapterFactory for javahl interface.
 * To register this factory, just call {@link JhlClientAdapterFactory#setup()} 
 */
public class JhlClientAdapterFactory extends SVNClientAdapterFactory {
	
    public static final String JAVAHL_CLIENT = "javahl";
    
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#createSVNClientImpl()
	 */
	protected ISVNClientAdapter createSVNClientImpl() {
		return new JhlClientAdapter();
	}

    private JhlClientAdapterFactory() {
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#getClientType()
     */
    protected String getClientType() {
        return JAVAHL_CLIENT;
    }
    
    public static void setup() throws SVNClientException {
        if (!JhlClientAdapter.isAvailable()) {
        	throw new SVNClientException("Javahl client adapter is not available");
        }
        
    	SVNClientAdapterFactory.registerAdapterFactory(new JhlClientAdapterFactory());
    }
  
}
