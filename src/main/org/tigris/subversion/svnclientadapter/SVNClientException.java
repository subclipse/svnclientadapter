/*******************************************************************************
 * Copyright (c) 2003, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter;

import java.lang.reflect.InvocationTargetException;

/**
 * A generic exception thrown from any {@link ISVNClientAdapter} methods
 *  
 * @author philip schatz
 */
public class SVNClientException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
	 */
	public SVNClientException() {
		super();
	}

	/**
     * Constructs a new exception with the specified detail message.
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
	 */
	public SVNClientException(String message) {
		super(message);
	}

	/**
     * Constructs a new exception with the specified detail message and
     * cause.
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
	 */
	public SVNClientException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructs a new exception with the specified cause.
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
	 */
	public SVNClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * Facorty method for creating a delegating/wrapping exception.
	 * @param e exception to wrap SVNClientException around
	 * @return an SVNClientException instance
	 */
	public static SVNClientException wrapException(Exception e) {
		Throwable t = e;
		if (e instanceof InvocationTargetException) {
			Throwable target = ((InvocationTargetException) e).getTargetException();
			if (target instanceof SVNClientException) {
				return (SVNClientException) target;
			}
			t = target;
		}
		return new SVNClientException(t);
	}

}
