package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

public class ExportTest extends SVNTest {

    public void testExport() throws Exception {
        // build the test setup
        OneTest thisTest = new OneTest("basicExport",getGreekTestConfig());

        try {
            client.doExport(new SVNUrl(thisTest.getUrl() + "/A"), thisTest.getWCPath(),
                    SVNRevision.HEAD, false);
            fail("missing exception");
        }
        catch (SVNClientException e)
        {
        }
        
        client.doExport(new SVNUrl(thisTest.getUrl()+"/A"), new File(thisTest.getWCPath()+"/exported"),
                SVNRevision.HEAD, true);
        
        
    }
    
}
