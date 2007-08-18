/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.javahl;

import java.util.Date;

import org.tigris.subversion.javahl.BlameCallback;
import org.tigris.subversion.javahl.BlameCallback2;
import org.tigris.subversion.svnclientadapter.Annotations;

/**
 * JavaHL specific subclass of {@link Annotations}.
 * It implements a {@link org.tigris.subversion.javahl.BlameCallback}
 * as means of constructing the annotation records.  
 * 
 */
public class JhlAnnotations extends Annotations implements BlameCallback, BlameCallback2 {
	
    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.BlameCallback#singleLine(java.util.Date, long, java.lang.String, java.lang.String)
     */
    public void singleLine(Date changed, long revision, String author,
                           String line) {
    	addAnnotation(new Annotation(revision, author, changed, line));
    }

	public void singleLine(Date changed, long revision, String author,
			Date merged_date, long merged_revision, String merged_author,
			String mergedPath, String line) {
	   	addAnnotation(new Annotation(getRevision(revision, merged_revision), getAuthor(author, merged_author), getDate(changed, merged_date), line));
	}
	
	private long getRevision(long revision, long merged_revision) {
		if (merged_revision == -1)
			return revision;
		else
			return merged_revision;
	}
	
	private String getAuthor(String author, String merged_author) {
		if (merged_author == null)
			return author;
		else
			return merged_author;
	}
	
	private Date getDate(Date changed, Date merged_date) {
		if (merged_date == null)
			return changed;
		else
			return merged_date;
	}
}
