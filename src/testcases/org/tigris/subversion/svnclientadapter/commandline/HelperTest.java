package org.tigris.subversion.svnclientadapter.commandline;

import java.util.Date;

import junit.framework.TestCase;

public class HelperTest extends TestCase {

	public void testConvertXMLDate() throws Exception {
		
		// before patch from Jennifer Bevan, svnClientAdapter was incorrectly
		// setting dates at 12:xx PM to 12:xx AM  
		assertEquals(new Date(2003-1900, 0, 10, 23, 21,54), Helper.convertXMLDate("2003-01-10T23:21:54.831325Z"));
		assertEquals(new Date(2003-1900, 0, 11, 12, 01,06), Helper.convertXMLDate("2003-01-11T12:01:06.649052Z"));
		assertEquals(new Date(2003-1900, 0, 11, 0, 4,33), Helper.convertXMLDate("2003-01-11T00:04:33.633658Z"));
		assertEquals(new Date(2003-1900, 0, 11, 12, 13,31), Helper.convertXMLDate("2003-01-11T12:13:31.499504Z"));
	}

}
