package org.tigris.subversion.svnclientadapter.commandline;

import junit.framework.TestCase;

import org.tigris.subversion.javahl.Notify;
import org.tigris.subversion.svnclientadapter.commandline.parser.SvnOutputParser;


public class SvnOutputParserTest extends TestCase {

	private class Notification {
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
		parser.addListener(new Notify() {
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
		
		parser.parse(" U /home/cedric/project/mytarget.txt");
		assertEquals("/home/cedric/project/mytarget.txt",notification[0].path);
		assertEquals(Notify.Status.unchanged,notification[0].contentState);
		assertEquals(Notify.Status.changed,notification[0].propState);

		parser.parse("C  /home/cedric/project/mytarget.txt");
		assertEquals("/home/cedric/project/mytarget.txt",notification[0].path);
		assertEquals(Notify.Status.conflicted,notification[0].contentState);
		assertEquals(Notify.Status.unchanged,notification[0].propState);
		
		
		parser.parse("At revision 53.");
		assertEquals(53, notification[0].revision);

		notification[0] = null;
		parser.parse("P  /home/cedric/project/mytarget.txt");
		assertEquals(null, notification[0]);
	}
	
}
