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

import java.io.File;

import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * 
 * @author Philip Schatz (schatz at tigris)
 */
class CmdLineProperty implements ISVNProperty {
	private String propName;
	private String propValue;
	private File file;
	private SVNUrl url;
	private byte[] data;

	CmdLineProperty(String name, String value, File file, byte[] data) {
		this.propName = name;
		this.propValue = value;
		this.url = null;
		this.file = file.getAbsoluteFile();
		this.data = data;
	}

	CmdLineProperty(String name, String value, SVNUrl url, byte[] data) {
		this.propName = name;
		this.propValue = value;
		this.url = url;
		this.file = null;
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getName()
	 */
	public String getName() {
		return propName;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getValue()
	 */
	public String getValue() {
		return propValue;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getFile()
	 */
	public File getFile() {
		return file;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getUrl()
	 */
	public SVNUrl getUrl() {
		return url;
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getData()
	 */
	public byte[] getData() {
		return data;
	}
}
