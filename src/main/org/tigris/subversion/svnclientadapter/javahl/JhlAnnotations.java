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
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tigris.subversion.javahl.BlameCallback;
import org.tigris.subversion.svnclientadapter.AnnotateInputStream;
import org.tigris.subversion.svnclientadapter.ISVNAnnotations;

/**
 * for now, we don't use this class because jhl blame method that takes
 * a BlameCallback as parameter does not seem to work ...  
 * 
 */
public class JhlAnnotations implements ISVNAnnotations, BlameCallback {
	private List annotations = new ArrayList();

	private class Annotation {
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
    	annotation.author = author;
    	annotation.changed = changed;
    	annotation.revision = revision;
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
