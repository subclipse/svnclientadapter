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

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @author philip schatz
 */
public class SVNClientException extends Exception {

	/**
	 * 
	 */
	public SVNClientException() {
		super();
	}

	/**
	 * @param message
	 */
	public SVNClientException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SVNClientException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SVNClientException(Throwable cause) {
		super(cause);
	}

	/*
	 * Static helper methods for creating exceptions
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
		return new SVNClientException(e);
	}

}
