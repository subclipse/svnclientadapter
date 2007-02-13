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
package org.tigris.subversion.svnclientadapter.commandline;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

public class HelperTest extends TestCase {

	public void testConvertXMLDate() throws Exception {
		
		// before patch from Jennifer Bevan, svnClientAdapter was incorrectly
		// setting dates at 12:xx PM to 12:xx AM  
	    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    cal.set(2003, 0, 10,23,21,54);
		assertEquals(cal.getTime().toString(), Helper.convertXMLDate("2003-01-10T23:21:54.831325Z").toString());
		cal.set(2003, 0, 11,12,01,06);
		assertEquals(cal.getTime().toString(), Helper.convertXMLDate("2003-01-11T12:01:06.649052Z").toString());
		cal.set(2003, 0,11,0,4,33);
		assertEquals(cal.getTime().toString(), Helper.convertXMLDate("2003-01-11T00:04:33.633658Z").toString());
		cal.set(2003,0,11,12,13,31);
		assertEquals(cal.getTime().toString(), Helper.convertXMLDate("2003-01-11T12:13:31.499504Z").toString());
	}

}
