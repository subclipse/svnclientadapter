/*******************************************************************************
 * Copyright (c) 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.svnclientadapter.SVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNRevision;

/**
 * JavaHL specific implementation of the {@link org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath}. 
 * Actually just an adapter from {@link org.tigris.subversion.javahl.ChangePath}
 * 
 */
public class JhlLogMessageChangePath extends SVNLogMessageChangePath {
	
	/**
	 * Constructor
	 * @param changePath
	 */
	public JhlLogMessageChangePath(ChangePath changePath) {
		super(
				changePath.getPath(),
				(changePath.getCopySrcRevision() != -1) ? new SVNRevision.Number(
						changePath.getCopySrcRevision()) : null, 
				changePath.getCopySrcPath(), 
				changePath.getAction());
	}

}
