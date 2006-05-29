package org.tigris.subversion.svnclientadapter.basictests;

import junit.framework.TestSuite;

public class SVNJavahlTests extends SVNBasicTestsSuite {

    public SVNJavahlTests(String name) {
        super(name);
    }

    public static TestSuite suite() {
        System.setProperty("test.clientType","javahl");
        System.setProperty("test.protocol","svn");
        
        TestSuite testSuite = new SVNJavahlTests("Test group");
        addTestsToSuite(testSuite);        
        return testSuite;
    }     
    
}
