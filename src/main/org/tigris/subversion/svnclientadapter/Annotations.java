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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Annotations implements ISVNAnnotations {
	private List annotations = new ArrayList();

	protected Annotation getAnnotation(int i) {
		if (i >= annotations.size()) {
			return null;
		}
		return (Annotation) annotations.get(i);
	}

	protected void addAnnotation(Annotation ann)
	{
		this.annotations.add(ann);
	}
	
	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
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

	public Date getChanged(int lineNumber) {
		Annotation annotation = getAnnotation(lineNumber);
		if (annotation == null) {
			return null;
		} else {
			return annotation.getChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getInputStream()
	 */
	public InputStream getInputStream() {
		return new AnnotateInputStream(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#size()
	 */
	public int size() {
		return annotations.size();
	}

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
		public String toString()
		{
			return getRevision() + ":" + getAuthor() + ":" + getLine();
		}
	}
}
