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

import junit.framework.TestSuite;

public class SVNSvnKitTests extends SVNBasicTestsSuite {

    public SVNSvnKitTests(String name) {
        super(name);
    }

    public static TestSuite suite() {
        System.setProperty("test.clientType","svnkit");
        System.setProperty("test.protocol","svn");
//        System.setProperty("test.serverHostname","127.0.0.1");
//        System.setProperty("test.serverPort","svn");
        
        TestSuite testSuite = new SVNSvnKitTests("Test group");
        addTestsToSuite(testSuite);        
        return testSuite;
    }     
    
}
