/*
 *  Copyright(c) 2003-2004 by the authors indicated in the @author tags.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tigris.subversion.svnclientadapter;

import java.io.File;

/**
 * describes a property (see svn command propget)
 *
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 */
public interface ISVNProperty {
	public static final String MIME_TYPE = "svn:mime-type";

	public static final String IGNORE = "svn:ignore";

	public static final String EOL_STYLE = "svn:eol-style";

	public static final String KEYWORDS = "svn:keywords";

	public static final String EXECUTABLE = "svn:executable";

	public static final String EXECUTABLE_VALUE = "*";

	public static final String EXTERNALS = "svn:externals";

	public static final String REV_AUTHOR = "svn:author";

	public static final String REV_LOG = "svn:log";

	public static final String REV_DATE = "svn:date";

	public static final String REV_ORIGINAL_DATE = "svn:original-date";

	/**
	 * get the name of the property
	 */
	public abstract String getName();
	
	/**
	 * get the value of the property as a string
	 * note that if value is a binary, this string will be invalid
	 */
	public abstract String getValue();
	
	/**
	 * get the file this property belongs to 
	 */
	public abstract File getFile();
	
	/**
	 * get the value of the property as an array of bytes 
	 */
	public abstract byte[] getData();
}