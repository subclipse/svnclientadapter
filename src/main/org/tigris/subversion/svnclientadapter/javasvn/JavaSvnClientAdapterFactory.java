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
package org.tigris.subversion.svnclientadapter.javasvn;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * Concrete implementation of SVNClientAdapterFactory for JavaSVN interface.
 * To register this factory, just call {@link JavaSvnClientAdapterFactory#setup()} 
 */
public class JavaSvnClientAdapterFactory extends SVNClientAdapterFactory {
	
    public static final String JAVASVN_CLIENT = "javasvn";
    
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#createSVNClientImpl()
	 */
	protected ISVNClientAdapter createSVNClientImpl() {
		return new JavaSvnClientAdapter();
	}

    private JavaSvnClientAdapterFactory() {
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#getClientType()
     */
    protected String getClientType() {
        return JAVASVN_CLIENT;
    }
    
    public static void setup() throws SVNClientException {
    	SVNClientAdapterFactory.registerAdapterFactory(new JavaSvnClientAdapterFactory());
    }
  
}
