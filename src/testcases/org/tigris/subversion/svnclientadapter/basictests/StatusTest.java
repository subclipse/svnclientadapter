/**
 * @copyright
 * ====================================================================
 * Copyright (c) 2003-2004 CollabNet.  All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at http://subversion.tigris.org/license-1.html.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 *
 * This software consists of voluntary contributions made by many
 * individuals.  For exact contribution history, see the revision
 * history and logs, available at http://subversion.tigris.org/.
 * ====================================================================
 * @endcopyright
 */
package org.tigris.subversion.svnclientadapter.basictests;


public class StatusTest extends SVNTest {

    /**
     * test the basic SVNClient.status functionality
     * @throws Throwable
     */
    public void testBasicStatus() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("basicStatus",getGreekTestConfig());

        // check the status of the working copy
        thisTest.checkStatusesExpectedWC();
    }    
    
}
