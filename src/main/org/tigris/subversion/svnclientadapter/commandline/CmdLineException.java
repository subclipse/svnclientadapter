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
package org.tigris.subversion.svnclientadapter.commandline;

import java.lang.reflect.InvocationTargetException;

/**
 * An exception that wraps the "svn" error message.
 * 
 * @author Philip Schatz (schatz at tigris)
 */
class CmdLineException extends Exception {

	private static final long serialVersionUID = 1L;

	//Constructors
	CmdLineException() {
		super();
	}

	CmdLineException(String message) {
		super(message);
	}

	CmdLineException(Throwable cause) {
		super(cause);
	}

	/*
	 * Static helper method for creating exceptions
	 */
	static CmdLineException wrapException(Exception e) {
		if (e instanceof InvocationTargetException) {
			Throwable target =
				((InvocationTargetException) e).getTargetException();
			if (target instanceof CmdLineException) {
				return (CmdLineException) target;
			}
		}
		return new CmdLineException(e);
	}
}
