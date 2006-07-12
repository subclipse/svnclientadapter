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
package org.tigris.subversion.svnclientadapter;

import java.util.Date;

/**
 * An interface describing a lock as return by the lock operation.
 * 
 */
public interface ISVNLock {

    /**
     * @return the owner of the lock
     */
    public String getOwner();

    /**
     * @return the path of the locked item
     */
    public String getPath();

    /**
     * @return the token provided during the lock operation
     */
    public String getToken();

    /**
     * @return the comment provided during the lock operation
     */
    public String getComment();
 
    /**
     * @return the date the lock was created
     */
    public Date getCreationDate();
 
    /**
     * @return the date when the lock will expire
     */
    public Date getExpirationDate();
 
}
