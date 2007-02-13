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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.SVNKeywords;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class PropertiesTest extends SVNTest {

    public void testBasicProperties() throws Throwable {
        // build the test setup.
        OneTest thisTest = new OneTest("basicProperties",
                getGreekTestConfig());

        File dir = new File(thisTest.getWorkingCopy() + "/A");
        File file = new File(thisTest.getWorkingCopy() + "/A/mu");
        SVNUrl fileUrl = new SVNUrl(thisTest.getUrl()+ "/A/mu");

        client.propertySet(file, "myProp", "my value", false);
        client.propertySet(dir, "myProp2", "my value 2", true);

        // get properties using getProperties
        ISVNProperty[] properties = client.getProperties(file);
        assertEquals(2, properties.length);
        Map map = new HashMap();
        for (int i = 0; i < properties.length; i++) {
            map.put(properties[i].getName(), properties[i]);
        }
        ISVNProperty prop = (ISVNProperty) map.get("myProp");
        assertNotNull(prop);
        assertEquals("my value", prop.getValue());
        prop = (ISVNProperty) map.get("myProp2");
        assertNotNull(prop);
        assertEquals("my value 2", prop.getValue());
        
        // get property using propertyGet on file
        prop = client.propertyGet(file, "myProp");
        assertNotNull(prop);
        assertEquals("myProp", prop.getName());
        assertEquals("my value", prop.getValue());
        assertEquals(file, prop.getFile());
        assertNull(prop.getUrl());

        //commit the wc so we can test the properties on URL
        client.commit(new File[] {dir}, "Commited properties", true);
        
        // get property using propertyGet on url
        prop = client.propertyGet(fileUrl, "myProp");
        assertNotNull(prop);
        assertEquals("myProp", prop.getName());
        assertEquals("my value", prop.getValue());
        assertEquals(fileUrl, prop.getUrl());
        assertNull(prop.getFile());
        
        // delete property
        client.propertyDel(dir,"myProp2",true);
        prop = client.propertyGet(file, "myProp2");
        assertNull(prop);
    }

    public void testBasicKeywords() throws Throwable {
        OneTest thisTest = new OneTest("basicGetProperties",
                getGreekTestConfig());

        File dir = new File(thisTest.getWorkingCopy() + "/A");
        SVNKeywords keywords = new SVNKeywords();
        keywords.setLastChangedDate(true);
        keywords.setLastChangedRevision(true);
        client.setKeywords(dir, keywords,true);
        
        keywords = client.getKeywords(dir);
        assertFalse(keywords.isLastChangedDate());
        assertFalse(keywords.isLastChangedRevision());
        
        File fileMu = new File(thisTest.getWorkingCopy() + "/A/mu");
        keywords = new SVNKeywords();
        keywords.setLastChangedDate(true);
        keywords.setLastChangedRevision(true);
        client.setKeywords(dir, keywords,true);
        
        keywords = client.getKeywords(fileMu);
        assertTrue(keywords.isLastChangedDate());
        assertTrue(keywords.isLastChangedRevision());
    }

}