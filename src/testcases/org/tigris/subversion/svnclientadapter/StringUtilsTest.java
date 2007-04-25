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
package org.tigris.subversion.svnclientadapter;

import junit.framework.TestCase;

import org.tigris.subversion.svnclientadapter.utils.StringUtils;

/**
 * tests StringUtils 
 */
public class StringUtilsTest extends TestCase {

	public void testSplitWithChar() throws Exception {
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

	public void testSplitWithString() throws Exception {
		String[] segments = StringUtils.split("org\n\rtigris\n\rsubversion\n\rsvnclientadapter","\n\r");
		assertEquals(4,segments.length);
		assertEquals("org",segments[0]);
		assertEquals("tigris",segments[1]);
		assertEquals("subversion",segments[2]);
		assertEquals("svnclientadapter",segments[3]);

		// make sure we have the same result than String.split when the string ends with the separator
		String path = "first\n\rsecond\n\r";
		segments = StringUtils.split(path,"\n\r");
		assertEquals(2,segments.length);
	}
	

}
