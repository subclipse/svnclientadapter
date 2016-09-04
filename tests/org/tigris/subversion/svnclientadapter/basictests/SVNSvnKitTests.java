/*******************************************************************************
 * Copyright (c) 2005, 2006 svnClientAdapter project and others.
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
