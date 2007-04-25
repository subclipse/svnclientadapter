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
package org.tigris.subversion.svnclientadapter.testUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;

/**
 * This class describe the expected state of the repository
 */
public class ExpectedRepository implements ExpectedStructure {

    /**
     * the map of the items of the repository. The relative path is the key
     * for the map
     */
    protected Map items = new HashMap();    

    /**
     * Copy an expected working copy state
     * @return the copy of the exiting object
     */
    public ExpectedRepository copy()
    {
        ExpectedRepository c = new ExpectedRepository();
        Iterator it = items.values().iterator();
        while (it.hasNext())
        {
            ((Item) it.next()).copy(c);
        }
        return c;
    }    
    

    /**
     * Add a new item to the repository
     * @param path      the path of the item
     * @param content   the content of the item. A null content signifies a
     *                  directory
     */
    public void addItem(String path, String content)
    {
        new Item(path, content);
    }

    /**
     * Returns the item at a path
     * @param path  the path, where the item is searched
     * @return  the found item
     */
    public Item getItem(String path)
    {
        return (Item) items.get(path);
    }

    /**
     * @return the number of items in WC
     */
    public int size() {
        return items.size();
    }    
    
    /**
     * Remove the item at a path
     * @param path  the path, where the item is removed
     */
    public void removeItem(String path)
    {
        items.remove(path);
    }    
   
    /**
     * Set the expected node kind at a path
     * @param path      the path, where the node kind is set
     * @param nodeKind  the expected node kind
     */
    public void setItemNodeKind(String path, SVNNodeKind nodeKind)
    {
        Item i = (Item) items.get(path);
        i.nodeKind = nodeKind;
    }    
    
    /**
     * Check the result of a single file SVNClient.list call
     * @param tested            the result array
     * @param singleFilePath    the path to be checked
     * @throws Exception
     */
    public void check(ISVNDirEntry[] tested, String singleFilePath) throws Exception
    {
        Assert.assertEquals("not a single dir entry", 1, tested.length);
        Item item = (Item)items.get(singleFilePath);
        Assert.assertNotNull("not found in working copy", item);
        Assert.assertNotNull("not a file", item.myContent);
        Assert.assertEquals("state says file, working copy not",
                tested[0].getNodeKind(),
                item.nodeKind == SVNNodeKind.NONE ? SVNNodeKind.FILE : item.nodeKind);
    }    
    
    /**
     * Check the result of a directory SVNClient.list call
     * @param tested        the result array
     * @param basePath      the path of the directory
     * @param recursive     the recursive flag of the call
     * @throws Exception
     */
    public void check(ISVNDirEntry[] tested, String basePath, boolean recursive)
            throws Exception
    {
        // clear the touched flag of all items
        Iterator it = items.values().iterator();
        while (it.hasNext())
        {
            Item item = (Item) it.next();
            item.touched = false;
        }

        // normalize directory path
        if (basePath != null && basePath.length() > 0)
        {
            basePath = basePath + "/";
        }
        else
        {
            basePath = "";
        }
        // check all returned DirEntry's
        for (int i = 0; i < tested.length; i++)
        {
            String name = basePath + tested[i].getPath();
            Item item = (Item) items.get(name);
            Assert.assertNotNull("not found in repository", item);
            if (item.myContent != null)
            {
                Assert.assertEquals("state says file, repository not",
                        tested[i].getNodeKind(),
                        item.nodeKind == SVNNodeKind.NONE ? SVNNodeKind.FILE : item.nodeKind);
            }
            else
            {
                Assert.assertEquals("state says dir, repository not",
                        tested[i].getNodeKind(),
                        item.nodeKind == SVNNodeKind.NONE ? SVNNodeKind.DIR : item.nodeKind);
            }
            item.touched = true;
        }

        // all items should have been in items, should had their touched flag
        // set
        it = items.values().iterator();
        while (it.hasNext())
        {
            Item item = (Item) it.next();
            if(!item.touched)
            {
                if(item.myPath.startsWith(basePath) &&
                        !item.myPath.equals(basePath))
                {
                    Assert.assertFalse("not found in dir entries", recursive);
                    boolean found = false;
                    for(int i = 0; i < tested.length; i++)
                    {
                        if(tested[i].getNodeKind() == SVNNodeKind.DIR)
                        {
                            if(item.myPath.
                                    startsWith(basePath+tested[i].getPath()))
                            {
                                found = true;
                                break;
                            }
                        }
                    }
                    Assert.assertTrue("not found in dir entries", found);
                }
            }
        }
    }    
    
    /**
     * internal class to discribe a single repository item
     */
    public class Item
    {
        /**
         * the relative path of the item
         */
        String myPath;

        /**
         * the content of a file. A directory has a null content
         */
        String myContent;
        
        /**
         * expected node kind. SVNNodeKind.NONE means do not check.
         */
        SVNNodeKind nodeKind = SVNNodeKind.NONE;        
        
        /**
         * flag if item has been touched. To detect missing items.
         */
        boolean touched;        
        
        /**
         * create a new item
         * @param path      the path of the item.
         * @param content   the content of the item. A null signals a directory.
         */
        protected Item(String path, String content)
        {
            myPath = path;
            myContent = content;
            items.put(path, this);
        }

        /**
         * copy constructor
         * @param source    the copy source.
         * @param owner     the repository of the copy
         */
        private Item(Item source, ExpectedRepository owner)
        {
            myPath = source.myPath;
            myContent = source.myContent;
            owner.items.put(myPath, this);
        }        
        
        /**
         * copy this item
         * @param owner the new WC
         * @return  the copied item
         */
        protected Item copy(ExpectedRepository owner)
        {
            return new Item(this, owner);
        }        
        
    }    
    
}
