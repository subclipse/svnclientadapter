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

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import junit.framework.TestCase;

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.commandline.parser.SvnOutputParser;


public class SvnOutputParserTest extends TestCase {

	private static class Notification {
		public String path;
		int action;
        int kind;
        String mimeType;
        int contentState;
        int propState;
        long revision;
        
        public Notification(String path,int action,int kind,String mimeType,int contentState,int propState,long revision) {
        	this.path = path;
        	this.action = action;
        	this.kind = kind;
        	this.mimeType = mimeType;
        	this.contentState = contentState;
        	this.propState = propState;
        	this.revision = revision;
        }
	}
	
	public void testParser() {
		SvnOutputParser parser = new SvnOutputParser();
		final Notification[] notification = new Notification[1]; 
		parser.addListener(new CmdLineNotify() {
			public void onNotify(
					String path,
			        int action,
			        int kind,
			        String mimeType,
			        int contentState,
			        int propState,
			        long revision) {
				notification[0] = new Notification(path,action,kind,mimeType,contentState,propState,revision);
			}
			
		});
		parser.parse("Skipped missing target: '/home/cedric/project/mytarget.txt'");
		assertEquals("/home/cedric/project/mytarget.txt",notification[0].path);
		
		parser.parse(" U   /home/cedric/project/mytarget.txt");
		assertEquals("/home/cedric/project/mytarget.txt",notification[0].path);
		assertEquals(CmdLineNotify.Status.unchanged,notification[0].contentState);
		assertEquals(CmdLineNotify.Status.changed,notification[0].propState);

		parser.parse("C    /home/cedric/project/mytarget.txt");
		assertEquals("/home/cedric/project/mytarget.txt",notification[0].path);
		assertEquals(CmdLineNotify.Status.conflicted,notification[0].contentState);
		assertEquals(CmdLineNotify.Status.unchanged,notification[0].propState);
		
		
		parser.parse("At revision 53.");
		assertEquals(53, notification[0].revision);

		notification[0] = null;
		parser.parse("P    /home/cedric/project/mytarget.txt");
		assertEquals(null, notification[0]);
	}

	public void testXmlLogMessageParserVerbose() throws UnsupportedEncodingException, SVNClientException, ParseException {

		String xml ="<?xml version='1.0' encoding='UTF-8'?>\r\n" +
			"<log>\r\n" +
			"<logentry revision='5'>\r\n" +
			"	<author>Jesper</author>\r\n" +
			"	<date>2005-06-18T10:42:52.338920Z</date>\r\n" +
			"	<paths>\r\n" +
			"		<path action='A' copyfrom-path='/trunk/org' copyfrom-rev='1'>/trunk/Subclipse-test/org</path>\r\n" +
			"		<path action='M'>/&#xe8;l</path>\r\n" +
			"	</paths>\r\n" +
			"	<msg>This one is really really cool, too!</msg>\r\n" +
			"</logentry>\r\n" +
			"</log>";
	
		CmdLineLogMessage[] entries = CmdLineLogMessage.createLogMessages(xml.getBytes("UTF-8"));

		// Expect one entry
		assertEquals(1, entries.length);
		assertEquals("This one is really really cool, too!", entries[0].getMessage());
		assertEquals(2, entries[0].getChangedPaths().length);

		// First path
		assertEquals('A', entries[0].getChangedPaths()[0].getAction());
		assertEquals("/trunk/Subclipse-test/org", entries[0].getChangedPaths()[0].getPath());
		assertEquals("/trunk/org", entries[0].getChangedPaths()[0].getCopySrcPath());
		assertEquals(SVNRevision.getRevision("1"), entries[0].getChangedPaths()[0].getCopySrcRevision());

		// Second path
		assertEquals('M', entries[0].getChangedPaths()[1].getAction());
		assertEquals("/\u00e8l", entries[0].getChangedPaths()[1].getPath());
		assertEquals("/\u00e8l", entries[0].getChangedPaths()[1].getPath());
		assertEquals(null, entries[0].getChangedPaths()[1].getCopySrcPath());
		assertEquals(null, entries[0].getChangedPaths()[1].getCopySrcRevision());
	}
	
	public void testXmlLogMessageParserSimple() throws UnsupportedEncodingException, SVNClientException {
	
		String xml="<?xml version='1.0' encoding='UTF-8'?>\r\n" +
			"<log>\r\n" +
			"<logentry revision='5'>\r\n" +
			"	<author>Jesper</author>\r\n" +
			"	<date>2005-06-18T10:42:52.338920Z</date>\r\n" +
			"	<msg>This one is really really cool, too!</msg>\r\n" +
			"</logentry>\r\n" +
			"</log>";

		CmdLineLogMessage[] entries = CmdLineLogMessage.createLogMessages(xml.getBytes("UTF-8"));
		assertEquals(1, entries.length);
		assertEquals("This one is really really cool, too!", entries[0].getMessage());
		assertEquals(0, entries[0].getChangedPaths().length);
	}

	public void testXmlLogMessageParserNoMsg() throws UnsupportedEncodingException, SVNClientException {
			
		String xml="<?xml version='1.0' encoding='UTF-8'?>\r\n" +
			"<log>\r\n" +
			"<logentry revision='5'>\r\n" +
			"	<author>Jesper</author>\r\n" +
			"	<date>2005-06-18T10:42:52.338920Z</date>\r\n" +
			"	<msg></msg>\r\n" +
			"</logentry>\r\n" +
			"</log>";

		CmdLineLogMessage[] entries = CmdLineLogMessage.createLogMessages(xml.getBytes("UTF-8"));
		assertEquals(1, entries.length);
		assertEquals("Jesper", entries[0].getAuthor());
		assertEquals("", entries[0].getMessage());
		assertEquals(0, entries[0].getChangedPaths().length);
	}

	public void testXmlLogMessageParserMultiple() throws UnsupportedEncodingException, SVNClientException, ParseException {
		
		String xml0="<?xml version='1.0' encoding='UTF-8'?>\r\n" +
			"<log/>\r\n";
		
		assertEquals(0, CmdLineLogMessage.createLogMessages(xml0.getBytes("UTF-8")).length);

		String xml3="<?xml version='1.0' encoding='UTF-8'?>\r\n" +
			"<log>\r\n" +
			"<logentry revision='5'>\r\n" +
			"	<author>j</author><date>2005-06-18T09:45:52.826529Z</date><msg></msg>\r\n" +
			"</logentry>\r\n" +
			"<logentry revision='6'>\r\n" +
			"	<author>j</author><date>2005-06-18T10:42:52.338920Z</date><msg></msg>\r\n" +
			"</logentry>\r\n" +
			"<logentry revision='7'>\r\n" +
			"	<author>j</author><date>2005-06-18T10:59:21.187452Z</date><msg></msg>\r\n" +
			"</logentry>\r\n" +
			"</log>";

		CmdLineLogMessage[] entries = CmdLineLogMessage.createLogMessages(xml3.getBytes("UTF-8"));
		assertEquals(3, entries.length);
		
		assertEquals(SVNRevision.getRevision("5"), entries[0].getRevision());
		assertEquals(SVNRevision.getRevision("6"), entries[1].getRevision());
		assertEquals(SVNRevision.getRevision("7"), entries[2].getRevision());
	}
	
	
}
