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

import java.io.InputStream;
import java.util.Date;

/**
 * An interface defining the result of a call to svn blame/annotate. For each
 * line in the file, last modification data are returned.
 * 
 */
public interface ISVNAnnotations {

	/**
	 * Get the date of the last change for the given <code>lineNumber</code>
	 * 
	 * @param lineNumber
	 * @return date of last change
	 */
	public abstract Date getChanged(int lineNumber);

	/**
	 * Get the revision of the last change for the given <code>lineNumber</code>
	 * 
	 * @param lineNumber
	 * @return the revision of last change
	 */
	public abstract long getRevision(int lineNumber);

	/**
	 * Get the author of the last change for the given <code>lineNumber</code>
	 * 
	 * @param lineNumber
	 * @return the author of last change or null
	 */
	public abstract String getAuthor(int lineNumber);

	/**
	 * Get the content (line itself) of the given <code>lineNumber</code>
	 * 
	 * @param lineNumber
	 * @return the line content
	 */
	public abstract String getLine(int lineNumber);

	/**
	 * Get an input stream providing the content of the file being annotated.
	 * 
	 * @return an inputstream of the content of the file
	 */
	public abstract InputStream getInputStream();

	/**
	 * Get the number of annotated lines
	 * 
	 * @return number of lines of file being annotated
	 */
	public abstract int numberOfLines();
}