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
 * @author Cedric Chabanois (cchab at tigris.org)
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

		String[] file1Parts = StringUtils.split(file1CanonPath,File.separatorChar);
		String[] file2Parts = StringUtils.split(file2CanonPath,File.separatorChar);
		
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
        File rootDir = getRootDir(files);

        // get the common part between current directory and other files
        File baseDir = getCommonPart(rootDir, new File("."));
        return baseDir;
	}

	/**
     * get the root directory for a set of files ie the ancestor of all given files
	 * @param files
	 * @return
	 * @throws SVNClientException
	 */
    static public File getRootDir(File[] files) throws SVNClientException {
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
            if (commonPart.isFile()) {
                return commonPart.getParentFile();
            } else {
                return commonPart;
            }
        } catch(IOException e) {
            throw SVNClientException.wrapException(e);
        }
    }

    /**
     * get path of file relative to rootDir 
     * @param rootDir
     * @param file
     * @return
     * @throws SVNClientException
     */
    static public String getRelativePath(File rootDir, File file) throws SVNClientException {
        try {
            String rootPath = rootDir.getCanonicalPath();
            String filePath = file.getCanonicalPath();
            if (!filePath.startsWith(rootPath)) {
                return null;
            }
            return filePath.substring(rootPath.length());
        } catch (IOException e) {
            throw SVNClientException.wrapException(e);
        }
    }
    
}
