/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
package org.tigris.subversion.svnclientadapter;

import java.io.File;
import java.io.IOException;

/**
 * During notification (both with svn command line and javahl), the files and directories 
 * are sometimes relative (with svn commit for ex).
 * However it is not relative to current directory but relative to the common parent of the current 
 * directory and the working copy target
 * 
 * ex : 
 * if working copy is at /home/cedric/programmation/sources/test
 * and current dir is /home/cedric/projects/subversion/subclipse
 * 
 * $svn ci /home/cedric/programmation/sources/test/essai8
 * Adding  programmation/sources/test/essai8
 * 
 */
public class SVNBaseDir {

	/**
	 * get the common directory between file1 and file2 or null if the files have nothing in common
	 * it always returns a directory unless file1 is the same file than file2
	 * @param file1
	 * @param file2
	 * @return
	 * @throws SVNClientException
	 */
	static File getCommonPart(File file1, File file2) throws SVNClientException {
		String file1CanonPath;
		String file2CanonPath;
		try {
			file1CanonPath = file1.getCanonicalPath();
			file2CanonPath = file2.getCanonicalPath();
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		}
		
		if (file1CanonPath.equals(file2CanonPath)) {
			return new File(file1CanonPath);
		}

		String separator = File.separator;
		if (File.separator.equals("\\")) {
			separator = "\\\\";
		}
		String[] file1Parts = file1CanonPath.split(separator);
		String[] file2Parts = file2CanonPath.split(separator);
		
		int parts1Length = file1Parts.length;
		int parts2Length = file2Parts.length; 
		
		int minLength = (parts1Length < parts2Length) ? parts1Length : parts2Length;
			
		String part1;
		String part2;
		StringBuffer commonsPart = new StringBuffer();
		for (int i = 0; i < minLength;i++) {
			part1 = file1Parts[i];
			part2 = file2Parts[i];
			if (!part1.equals(part2)) {
				break;				
			}
			if (commonsPart.length() == 0) {
				commonsPart.append(part1);
			} else
			{
				commonsPart.append(File.separatorChar);
				commonsPart.append(part1);
			}
		}
		if (commonsPart.length() == 0) {
			return null; // the two files have nothing in common (one on disk c: and the other on d: for ex)
		}
		return new File(commonsPart.toString());
	}

	/**
	 * get the base directory for the given file
	 * @param file
	 * @return
	 * @throws SVNClientException
	 */
	static public File getBaseDir(File file)  throws SVNClientException {
		return getBaseDir(new File[] { file} );
	} 

	/**
	 * get the base directory for a set of files or null if there is no base directory
	 * for the set of files
	 * @param files
	 * @return
	 * @throws SVNClientException
	 */
	static public File getBaseDir(File[] files) throws SVNClientException {
		try {
			File[] canonicalFiles = new File[files.length];
			 for (int i = 0; i < files.length;i++) {
				canonicalFiles[i] = files[i].getCanonicalFile();
			}
	 
			// first get the common part between all files
			File commonPart = canonicalFiles[0];
			for (int i = 0; i < files.length;i++) {
				commonPart = getCommonPart(commonPart, canonicalFiles[i]);
				if (commonPart == null) {
					return null;		 
				}
			}
		
			// get the common part between current directory and other files
			commonPart = getCommonPart(commonPart, new File("."));
			return commonPart;
		} catch(IOException e) {
			throw SVNClientException.wrapException(e);
		}
		 
	}


}
