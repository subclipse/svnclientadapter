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
package org.tigris.subversion.svnclientadapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Generic implementation of {@link ISVNAnnotations} interface.
 * It's expected to be filled with annotation data by 
 * {@link #addAnnotation(Annotations.Annotation)} method.
 *  
 */
public class Annotations implements ISVNAnnotations {
	
	/** list of annotation records (lines) */
	private List annotations = new ArrayList();

	protected Annotation getAnnotation(int i) {
		if (i >= this.annotations.size()) {
			return null;
		}
		return (Annotation) this.annotations.get(i);
	}

	/**
	 * Append the given annotation record the list of annotation
	 * @param annotation
	 */
	public void addAnnotation(Annotation annotation)
	{
		this.annotations.add(annotation);
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getRevision(int)
	 */
	public long getRevision(int lineNumber) {
		Annotation annotation = getAnnotation(lineNumber);
		if (annotation == null) {
			return -1;
		} else {
			return annotation.getRevision();
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
			return annotation.getAuthor();
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getChanged(int)
	 */
	public Date getChanged(int lineNumber) {
		Annotation annotation = getAnnotation(lineNumber);
		if (annotation == null) {
			return null;
		} else {
			return annotation.getChanged();
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
			return annotation.getLine();
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getInputStream()
	 */
	public InputStream getInputStream() {
		return new AnnotateInputStream(this);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#numberOfLines()
	 */
	public int numberOfLines() {
		return this.annotations.size();
	}

	/**
	 * Class represeting one line of the annotations, i.e. an annotation record
	 * 
	 */
	public static class Annotation {

		private long revision;

		private String author;

		private Date changed;

		private String line;

		/**
		 * Constructor
		 * 
		 * @param revision
		 * @param author
		 * @param changed
		 * @param line
		 */
		public Annotation(long revision, String author, Date changed,
				String line) {
			super();
			this.revision = revision;
			this.author = author;
			this.changed = changed;
			this.line = line;
		}

		/**
		 * @return Returns the author.
		 */
		public String getAuthor() {
			return author;
		}

		/**
		 * @return Returns the changed.
		 */
		public Date getChanged() {
			return changed;
		}

		/**
		 * @return Returns the line.
		 */
		public String getLine() {
			return line;
		}

		/**
		 * @param line The line to set.
		 */
		public void setLine(String line) {
			this.line = line;
		}

		/**
		 * @return Returns the revision.
		 */
		public long getRevision() {
			return revision;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return getRevision() + ":" + getAuthor() + ":" + getLine();
		}
	}
	
	protected static class AnnotateInputStream extends InputStream {
	    private ISVNAnnotations annotations;
		private int currentLineNumber;
	    private int currentPos;
	    private String currentLine;
	    private int available;
	    
	    /**
	     * Constructor
	     * @param annotations
	     */
	    public AnnotateInputStream(ISVNAnnotations annotations) {
			this.annotations = annotations;
			initialize();
	    }
	    
	    private void initialize() {
	    	currentLine = annotations.getLine(0);
	        currentLineNumber = 0;
	        currentPos = 0;
	        
	        available = 0;
	        int annotationsSize = annotations.numberOfLines();
	        for (int i = 0; i < annotationsSize;i++) {
	        	available += annotations.getLine(i).length(); 
	        	if (i != annotationsSize-1) {
	        		available++; // +1 for \n
	        	}
	        }
	    }

	    private void getNextLine() {
	        currentLineNumber++;
	        currentPos = 0;
	        currentLine = annotations.getLine(currentLineNumber);
	    }

	    /* (non-Javadoc)
	     * @see java.io.InputStream#read()
	     */
	    public int read() throws IOException {
	        if (currentLineNumber >= annotations.numberOfLines())
	            return -1; // end of stream
	        if (currentPos > currentLine.length()) {
	            getNextLine();
	            if (currentLineNumber >= annotations.numberOfLines())
	                return -1; // end of stream                
	        }
	        int character;
	        if (currentPos == currentLine.length())
	        	character = '\n';
	        else
	        	character = currentLine.charAt(currentPos);
	        currentPos++;
	        available--;
	        return character;
	    }
	    
	    /* (non-Javadoc)
	     * @see java.io.InputStream#available()
	     */
	    public int available() throws IOException {
	        return available;
	    }
		
		/* (non-Javadoc)
		 * @see java.io.InputStream#reset()
		 */
		public synchronized void reset() throws IOException {
			initialize();
		}
	}
}
