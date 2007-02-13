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
package org.tigris.subversion.svnclientadapter;


/**
 * An interface defining a change path for a log message
 *
 */
public interface ISVNLogMessageChangePath {
	
	/**
	 * Retrieve the path to the commited item
	 * @return  the path to the commited item
	 */
	public abstract String getPath();

	/**
	 * Retrieve the copy source revision if any or null otherwise 
	 * @return  the copy source revision (if any)
	 */
	public abstract SVNRevision.Number getCopySrcRevision();

	/**
	 * Retrieve the copy source path (if any) or null otherwise
	 * @return  the copy source path (if any)
	 */
	public abstract String getCopySrcPath();

	/**
	 * Retrieve action performed.
	 * I.e. 'A'dd, 'D'elete, 'R'eplace, 'M'odify
	 * 
	 * @return  action performed
	 */
	public abstract char getAction();
}