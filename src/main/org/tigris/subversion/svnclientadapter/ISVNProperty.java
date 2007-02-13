/*******************************************************************************
 * Copyright (c) 2003, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter;

import java.io.File;

/**
 * An interface describing a subversion property (e.g. as return by svn propget)
 *
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 */
public interface ISVNProperty {
	
    /**
     * mime type of the entry, used to flag binary files
     */
    public static final String MIME_TYPE = "svn:mime-type";
    /**
     * list of filenames with wildcards which should be ignored by add and
     * status
     */
    public static final String IGNORE = "svn:ignore";
    /**
     * how the end of line code should be treated during retrieval
     */
    public static final String EOL_STYLE = "svn:eol-style";
    /**
     * list of keywords to be expanded during retrieval
     */
    public static final String KEYWORDS = "svn:keywords";
    /**
     * flag if the file should be made excutable during retrieval
     */
    public static final String EXECUTABLE = "svn:executable";
    /**
     * value for svn:executable
     */
    public static final String EXECUTABLE_VALUE = "*";
    /**
     * list of directory managed outside of this working copy
     */
    public static final String EXTERNALS = "svn:externals";
    /**
     * the author of the revision
     */
    public static final String REV_AUTHOR = "svn:author";
    /**
     * the log message of the revision
     */
    public static final String REV_LOG = "svn:log";
    /**
     * the date of the revision
     */
    public static final String REV_DATE = "svn:date";
    /**
     * the original date of the revision
     */
    public static final String REV_ORIGINAL_DATE = "svn:original-date";

	/**
	 * @return the name of the property
	 */
	String getName();
	
    /**
     * Returns the string value of the property.
     * There is no protocol if a property is a string or a binary value
     * @return the string value
     */
	String getValue();
	
	/**
	 * @return the file this property belongs to (or null if on remote resource)
	 */
	File getFile();
	
    /**
     * @return the url this property belongs to
     */
	SVNUrl getUrl();

    /**
     * Returns the byte array value of the property
     * There is no protocol if a property is a string or a binary value
     * @return the byte array value
     */
	byte[] getData();
}