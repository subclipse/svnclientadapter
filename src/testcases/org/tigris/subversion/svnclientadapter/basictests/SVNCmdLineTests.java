/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
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

public class SVNCmdLineTests extends SVNBasicTestsSuite {

    public SVNCmdLineTests(String name) {
        super(name);
    }

    public static TestSuite suite() {
        System.setProperty("test.clientType","commandline");
        
        TestSuite testSuite = new SVNCmdLineTests("Test group");
        addTestsToSuite(testSuite);        
        return testSuite;
    }     
    
}
