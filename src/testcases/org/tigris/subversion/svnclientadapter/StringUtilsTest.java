
package org.tigris.subversion.svnclientadapter;

import junit.framework.TestCase;

/**
 * tests StringUtils 
 */
public class StringUtilsTest extends TestCase {

	public void testStringUtils() throws Exception {
		String[] segments = StringUtils.split("org.tigris.subversion.svnclientadapter",'.');
		assertEquals(4,segments.length);
		assertEquals("org",segments[0]);
		assertEquals("tigris",segments[1]);
		assertEquals("subversion",segments[2]);
		assertEquals("svnclientadapter",segments[3]);
		
		// make sure we have the same result than String.split when the string ends with the separator
		String path = "first/second/";
		segments = StringUtils.split(path,'/');
		assertEquals(2,segments.length);
	}

}
