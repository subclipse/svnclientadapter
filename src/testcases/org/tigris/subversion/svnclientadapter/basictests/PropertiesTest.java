package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.SVNKeywords;

public class PropertiesTest extends SVNTest {

    public void testBasicProperties() throws Throwable {
        // build the test setup.
        OneTest thisTest = new OneTest("basicProperties",
                getGreekTestConfig());

        File dir = new File(thisTest.getWorkingCopy() + "/A");
        File file = new File(thisTest.getWorkingCopy() + "/A/mu");

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
        
        // get property using propertyGet
        prop = client.propertyGet(file, "myProp");
        assertNotNull(prop);
        assertEquals("my value", prop.getValue());
        
        // delete property
        client.propertyDel(dir,"myProp2",true);
        prop = client.propertyGet(file, "myProp2");
        assertNull(prop);
    }

    public void testBasicKeywords() throws Throwable {
        OneTest thisTest = new OneTest("basicGetProperties",
                getGreekTestConfig());
        File file = new File(thisTest.getWorkingCopy() + "/A");
        SVNKeywords keywords = new SVNKeywords();
        keywords.setLastChangedDate(true);
        keywords.setLastChangedRevision(true);
        client.setKeywords(file, keywords,true);
        
        keywords = client.getKeywords(file);
        assertTrue(keywords.isLastChangedDate());
        assertTrue(keywords.isLastChangedRevision());
        
        File fileMu = new File(thisTest.getWorkingCopy() + "/A/mu");
        keywords = client.getKeywords(fileMu);
        assertTrue(keywords.isLastChangedDate());
        assertTrue(keywords.isLastChangedRevision());
    }

}