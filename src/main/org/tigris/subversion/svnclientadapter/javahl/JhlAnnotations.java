/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.javahl;

import java.util.Date;

import org.tigris.subversion.javahl.BlameCallback;
import org.tigris.subversion.svnclientadapter.Annotations;

/**
 * JavaHL specific subclass of {@link Annotations}.
 * It implements a {@link org.tigris.subversion.javahl.BlameCallback}
 * as means of constructing the annotation records.  
 * 
 */
public class JhlAnnotations extends Annotations implements BlameCallback {
	
    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.BlameCallback#singleLine(java.util.Date, long, java.lang.String, java.lang.String)
     */
    public void singleLine(Date changed, long revision, String author,
                           String line) {
    	addAnnotation(new Annotation(revision, author, changed, line));
    }
}
