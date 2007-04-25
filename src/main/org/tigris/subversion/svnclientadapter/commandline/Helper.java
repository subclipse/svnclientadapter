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
