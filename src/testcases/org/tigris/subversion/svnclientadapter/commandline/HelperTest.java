/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
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
