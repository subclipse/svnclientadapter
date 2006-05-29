package org.tigris.subversion.svnclientadapter.basictests;

import junit.framework.TestSuite;

public class SVNJavaSVNTests extends SVNBasicTestsSuite {

    public SVNJavaSVNTests(String name) {
        super(name);
    }

    public static TestSuite suite() {
        System.setProperty("test.clientType","javasvn");
        System.setProperty("test.protocol","svn");
//        System.setProperty("test.serverHostname","127.0.0.1");
//        System.setProperty("test.serverPort","svn");
        
        TestSuite testSuite = new SVNJavaSVNTests("Test group");
        addTestsToSuite(testSuite);        
        return testSuite;
    }     
    
}
