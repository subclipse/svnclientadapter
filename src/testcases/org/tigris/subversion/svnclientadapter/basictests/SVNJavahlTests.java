package org.tigris.subversion.svnclientadapter.basictests;

import junit.framework.TestSuite;

public class SVNJavahlTests extends TestSuite {

    public SVNJavahlTests(String name) {
        super(name);
    }

    public static TestSuite suite() {
        System.setProperty("test.clientType","javahl");
        
        TestSuite testSuite = new SVNJavahlTests("Test group");

        testSuite.addTestSuite(AddTest.class);
        testSuite.addTestSuite(CatTest.class);
        testSuite.addTestSuite(CheckOutTest.class);
        testSuite.addTestSuite(CommitTest.class);
        testSuite.addTestSuite(DeleteTest.class);
        testSuite.addTestSuite(ImportTest.class);
        testSuite.addTestSuite(InfoTest.class);
        testSuite.addTestSuite(LogTest.class);
        testSuite.addTestSuite(LsTest.class);
        testSuite.addTestSuite(MkdirTest.class);
        testSuite.addTestSuite(ResolveTest.class);
        testSuite.addTestSuite(RevertTest.class);
        testSuite.addTestSuite(StatusTest.class);
        testSuite.addTestSuite(SwitchTest.class);
        testSuite.addTestSuite(UpdateTest.class);
        
        return testSuite;
    }     
    
}
