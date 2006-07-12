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
package org.tigris.subversion.svnclientadapter.javahl;

import java.util.Date;

import org.tigris.subversion.javahl.Lock;
import org.tigris.subversion.svnclientadapter.ISVNLock;

/**
 * A JavaHL based implementation of {@link ISVNLock}.
 * Actually just an adapter from {@link org.tigris.subversion.javahl.Lock}
 *  
 * @author Mark Phippard
 */
public class JhlLock implements ISVNLock {
    
    private Lock _l;

    /**
     * Constructor
     * @param lock
     */
	public JhlLock(Lock lock) {
        super();
		_l = lock;
	}

	/* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLock#getOwner()
     */
    public String getOwner() {
        return _l.getOwner();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLock#getPath()
     */
    public String getPath() {
        return _l.getPath();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLock#getToken()
     */
    public String getToken() {
        return _l.getToken();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLock#getComment()
     */
    public String getComment() {
        return _l.getComment();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLock#getCreationDate()
     */
    public Date getCreationDate() {
        return _l.getCreationDate();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLock#getExpirationDate()
     */
    public Date getExpirationDate() {
        return _l.getExpirationDate();
    }

}
