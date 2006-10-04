/*******************************************************************************
 * Copyright (c) 2006 Subclipse project and others.
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.tigris.subversion.svnclientadapter.ISVNAnnotations;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class BlameTest extends SVNTest {

    private void prepareTestFile(File mu, OneTest thisTest) throws FileNotFoundException, SVNClientException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(mu, true));
        pw.println("some text in line1");
        pw.println("some text in line2");
        pw.println("some text in line3");
        pw.close();
        client.commit(new File[] {mu}, "log msg2", true);
        pw = new PrintWriter(new FileOutputStream(mu, false));
        pw.println("some new text in line1");
        pw.println("some text in line2");
        pw.println("some new text in line3");
        pw.close();
        client.commit(new File[] {mu}, "log msg3", true);
        client.setUsername(ANOTHER_TEST_USER);
        pw = new PrintWriter(new FileOutputStream(mu, true));
        pw.println("some new line4");
        pw.close();
        client.commit(new File[] {mu}, "log msg4", true);
        client.setUsername(TEST_USER);
    }

    public void testBlameHead() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("testBlameHead", getGreekTestConfig());

        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        prepareTestFile(mu, thisTest);
        
        //blame from 1->HEAD
        ISVNAnnotations annotations = client.annotate(mu, null, null);        
        // the size should match
        assertEquals("invalid number of annotation lines", 4, annotations.numberOfLines());
        //check revisions
        assertEquals("blamed revision does not match", 3 ,annotations.getRevision(0));
        assertEquals("blamed revision does not match", 2 ,annotations.getRevision(1));
        assertEquals("blamed revision does not match", 3 ,annotations.getRevision(2));
        assertEquals("blamed revision does not match", 4 ,annotations.getRevision(3));
        //check authors
        assertEquals("blamed author does not match", TEST_USER ,annotations.getAuthor(0));
        assertEquals("blamed author does not match", TEST_USER ,annotations.getAuthor(1));
        assertEquals("blamed author does not match", TEST_USER ,annotations.getAuthor(2));
        assertEquals("blamed author does not match", ANOTHER_TEST_USER ,annotations.getAuthor(3));
        //check text
        assertEquals("blamed text does not match", "some new text in line1" ,annotations.getLine(0).trim());
        assertEquals("blamed text does not match", "some text in line2" ,annotations.getLine(1).trim());
    }

    public void testBlameHeadUnUncommittedRename() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("testBlameHead", getGreekTestConfig());

        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        prepareTestFile(mu, thisTest);
        File mu2 = new File(thisTest.getWorkingCopy(), "A/mu2");
        client.move(mu, mu2, true);
        
        //blame from 1->HEAD
        ISVNAnnotations annotations = client.annotate(mu2, null, null);        
        // the size should match
        assertEquals("invalid number of annotation lines", 4, annotations.numberOfLines());
        //check revisions
        assertEquals("blamed revision does not match", 3 ,annotations.getRevision(0));
        assertEquals("blamed revision does not match", 2 ,annotations.getRevision(1));
        assertEquals("blamed revision does not match", 3 ,annotations.getRevision(2));
        assertEquals("blamed revision does not match", 4 ,annotations.getRevision(3));
        //check authors
        assertEquals("blamed author does not match", TEST_USER ,annotations.getAuthor(0));
        assertEquals("blamed author does not match", TEST_USER ,annotations.getAuthor(1));
        assertEquals("blamed author does not match", TEST_USER ,annotations.getAuthor(2));
        assertEquals("blamed author does not match", ANOTHER_TEST_USER ,annotations.getAuthor(3));
        //check text
        assertEquals("blamed text does not match", "some new text in line1" ,annotations.getLine(0).trim());
        assertEquals("blamed text does not match", "some text in line2" ,annotations.getLine(1).trim());
    }

    public void testBlameLastRev() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("testBlameLastRev", getGreekTestConfig());

        File mu = new File(thisTest.getWorkingCopy(), "iota");
        prepareTestFile(mu, thisTest);
        
        //blame from 3->HEAD
        ISVNAnnotations annotations = client.annotate(mu, SVNRevision.getRevision("3"), null);        
        // the size should match
        assertEquals("invalid number of annotation lines", 4, annotations.numberOfLines());
        //check revisions
        assertEquals("blamed revision does not match", 3 ,annotations.getRevision(0));
        assertEquals("blamed revision does not match", -1 ,annotations.getRevision(1));
        assertEquals("blamed revision does not match", 3 ,annotations.getRevision(2));
        assertEquals("blamed revision does not match", 4 ,annotations.getRevision(3));
        //check authors
        assertEquals("blamed author does not match", TEST_USER ,annotations.getAuthor(0));
        assertEquals("blamed author does not match", null ,annotations.getAuthor(1));
        assertEquals("blamed author does not match", TEST_USER ,annotations.getAuthor(2));
        assertEquals("blamed author does not match", ANOTHER_TEST_USER ,annotations.getAuthor(3));
        //check text
        assertEquals("blamed text does not match", "some new text in line1" ,annotations.getLine(0).trim());
        assertEquals("blamed text does not match", "some text in line2" ,annotations.getLine(1).trim());
    }

}
