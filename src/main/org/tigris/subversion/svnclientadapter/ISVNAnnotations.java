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

import java.io.InputStream;

public interface ISVNAnnotations {
	
	
	/**
	 * get the revision for the given line number
	 * @param lineNumber
	 * @return
	 */
	public abstract long getRevision(int lineNumber);

	/**
	 * get the author for the given line number or null
	 * @param lineNumber
	 * @return
	 */
	public abstract String getAuthor(int lineNumber);

	/**
	 * get the given line
	 * @param lineNumber
	 * @return
	 */
	public abstract String getLine(int lineNumber);

	/**
	 * get an inputstream of the content of the file
	 * @return
	 */
	public abstract InputStream getInputStream();
	
	
	public abstract int size();
}