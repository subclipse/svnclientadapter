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
