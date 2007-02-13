/*******************************************************************************
 * Copyright (c) 2005, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

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
