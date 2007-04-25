/*******************************************************************************
 * Copyright (c) 2003, 2006 svnClientAdapter project and others.
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
