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
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tigris.subversion.javahl.BlameCallback;
import org.tigris.subversion.svnclientadapter.AnnotateInputStream;
import org.tigris.subversion.svnclientadapter.ISVNAnnotations;

/**
 * A BlameCallback implementation class for JavaHL blame() method.  
 * 
 */
public class JhlAnnotations implements ISVNAnnotations, BlameCallback {
	private List annotations = new ArrayList();

	private static class Annotation {
		long revision;
		String author;
		Date changed;
		String line;
	}
	
	private Annotation getAnnotation(int i) {
		if (i >= annotations.size()) {
			return null;
		}
		return (Annotation)annotations.get(i);
	}
	
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getRevision(int)
	 */
	public long getRevision(int lineNumber) {
		Annotation annotation = getAnnotation(lineNumber);
		if (annotation == null) {
			return -1;
		} else {
			return annotation.revision;
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getAuthor(int)
	 */
	public String getAuthor(int lineNumber) {
		Annotation annotation = getAnnotation(lineNumber);
		if (annotation == null) {
			return null;
		} else {
			return annotation.author;
		}
	}

	public Date getChanged(int lineNumber) {
		Annotation annotation = getAnnotation(lineNumber);
		if (annotation == null) {
			return null;
		} else {
			return annotation.changed;
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getLine(int)
	 */
	public String getLine(int lineNumber) {
		Annotation annotation = getAnnotation(lineNumber);
		if (annotation == null) {
			return null;
		} else {
			return annotation.line;
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getInputStream()
	 */
	public InputStream getInputStream() {
		return new AnnotateInputStream(this);
	}

	
	
    /**
     * the method will be called for every line in a file.
     * @param changed   the date of the last change.
     * @param revision  the revision of the last change.
     * @param author    the author of the last change.
     * @param line      the line in the file
     */
    public void singleLine(Date changed, long revision, String author,
                           String line) {
    	Annotation annotation = new Annotation();
    	annotation.changed = changed;
    	annotation.revision = revision;
    	annotation.author = author;
    	annotation.line = line;
    	annotations.add(annotation);
    }
    
    
    
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#size()
	 */
	public int size() {
		return annotations.size();
	}
}
