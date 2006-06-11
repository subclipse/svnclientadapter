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

import java.util.Date;

import org.tigris.subversion.javahl.BlameCallback;
import org.tigris.subversion.svnclientadapter.Annotations;

/**
 * A BlameCallback implementation class for JavaHL blame() method.  
 * 
 */
public class JhlAnnotations extends Annotations implements BlameCallback {
	
    /**
     * the method will be called for every line in a file.
     * @param changed   the date of the last change.
     * @param revision  the revision of the last change.
     * @param author    the author of the last change.
     * @param line      the line in the file
     */
    public void singleLine(Date changed, long revision, String author,
                           String line) {
    	Annotation annotation = new Annotation(revision, author, changed, line);
    	addAnnotation(annotation);
    }
}
