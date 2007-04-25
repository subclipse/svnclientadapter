/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class CatTest extends SVNTest {

    private void modifyAMu(OneTest thisTest) throws FileNotFoundException {
        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        PrintWriter pw = new PrintWriter(new FileOutputStream(mu, true));
        pw.print("some text");
        pw.close();
    }
    
    /**
     * test the basic SVNClient.fileContent functionality
     * 
     * @throws Throwable
     */
    public void testHeadCat() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("testHeadCat", getGreekTestConfig());

        // modify A/mu
        modifyAMu(thisTest);

        // get the content from the repository
        InputStream is = client.getContent(new File(thisTest.getWCPath()
                + "/A/mu"), SVNRevision.HEAD);
        byte[] content = new byte[is.available()];
        is.read(content);
        byte[] testContent = thisTest.getExpectedWC().getItemContent("A/mu").getBytes();

        // the content should be the same
        assertTrue("content changed", Arrays.equals(content, testContent));
    }

    public void testBaseCat() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("testBaseCat", getGreekTestConfig());

        // modify A/mu
        modifyAMu(thisTest);
        
        // get the content from BASE
        InputStream is = client.getContent(new File(thisTest.getWCPath()
                + "/A/mu"), SVNRevision.BASE);
        byte[] content = new byte[is.available()];
        is.read(content);
        byte[] testContent = thisTest.getExpectedWC().getItemContent("A/mu").getBytes();

        // the content should be the same
        assertTrue("content changed", Arrays.equals(content, testContent));
    }

    public void testDateCat() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("testDateCat", getGreekTestConfig());

        // modify A/mu
        modifyAMu(thisTest);
        
        // get the content using date
        InputStream is = client.getContent(new File(thisTest.getWCPath()
                + "/A/mu"), new SVNRevision.DateSpec(new Date()));
        byte[] content = new byte[is.available()];
        is.read(content);
        byte[] testContent = thisTest.getExpectedWC().getItemContent("A/mu").getBytes();

        // the content should be the same
        assertTrue("content changed", Arrays.equals(content, testContent));
    }

    public void testDateStringCat() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("testDateCat", getGreekTestConfig());

        // modify A/mu        
        modifyAMu(thisTest);
        
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm a", Locale.US);

        // get the content using date
        InputStream is = client.getContent(new File(thisTest.getWCPath()
                + "/A/mu"), SVNRevision.getRevision(df.format(new Date())));
        byte[] content = new byte[is.available()];
        is.read(content);
        byte[] testContent = thisTest.getExpectedWC().getItemContent("A/mu").getBytes();

        // the content should be the same
        assertTrue("content changed", Arrays.equals(content, testContent));
    }

    public void testUrlCat() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("testUrlCat", getGreekTestConfig());
        InputStream is = client.getContent(new SVNUrl(thisTest.getUrl()
                + "/A/mu"), SVNRevision.HEAD);
        byte[] content = new byte[is.available()];
        is.read(content);
        byte[] testContent = thisTest.getExpectedWC().getItemContent("A/mu").getBytes();

        // the content should be the same
        assertTrue("content is not the same", Arrays.equals(content,
                testContent));
    }

}