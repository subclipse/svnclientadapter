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

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author Philip Schatz (schatz at tigris)
 * @author Daniel Rall
 */
class Helper {

	static final String NEWLINE = System.getProperty("line.separator");
	private static DateFormat df =
		new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");

	// 2003-10-13T12:54:42.957948Z
	private static DateFormat xmlFormat =
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	// Initialize timezone to GMT for xmlFormat
	static {
	    xmlFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
	}

	/**
	 * A non-instantiable class
	 */
	private Helper() {
		//non-instantiable
	}

	static SVNUrl toSVNUrl(String url) {
		try {
			return new SVNUrl(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	static SVNRevision.Number toRevNum(String rev) {
		if (rev == null)
			return null;
		try {
			return new SVNRevision.Number(Long.parseLong(rev));
		} catch (NumberFormatException e) {
			return new SVNRevision.Number(-1);
		}
	}

	static Date toDate(String date) {
		if (date == null)
			return null;
		try {
			return df.parse(date);
		} catch (ParseException e1) {
			return null;
		}
	}
	
	static Date convertXMLDate(String date) {
		if (date == null)
			return null;
		try {
			return xmlFormat.parse(date);
		} catch (ParseException e1) {
			return null;
		}
	}
}
