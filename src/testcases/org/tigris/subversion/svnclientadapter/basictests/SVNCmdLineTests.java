package org.tigris.subversion.svnclientadapter.basictests;

import junit.framework.TestSuite;

public class SVNCmdLineTests extends TestSuite {

    public SVNCmdLineTests(String name) {
        super(name);
    }

    public static TestSuite suite() {
        System.setProperty("test.clientType","commandline");
        
        TestSuite testSuite = new SVNCmdLineTests("Test group");

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
