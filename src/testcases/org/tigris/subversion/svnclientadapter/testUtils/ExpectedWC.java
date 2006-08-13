/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.testUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
/**
 * This class describe the expected state of the working copy
 */
public class ExpectedWC implements ExpectedStructure
{
    /**
     * the map of the items of the working copy. The relative path is the key
     * for the map
     */
    protected Map items = new HashMap();

    /**
     * Generate from the expected state of the working copy a new working copy
     * @param root      the working copy directory
     * @throws IOException
     */
    public void materialize(File root) throws IOException
    {
        // generate all directories first
        Iterator it = items.values().iterator();
        while (it.hasNext())
        {
            Item item = (Item) it.next();
            if (item.myContent == null) // is a directory
            {
                File dir = new File(root, item.myPath);
                if (!dir.exists())
                    dir.mkdirs();
            }
        }
        // generate all files with the content in the second run
        it = items.values().iterator();
        while (it.hasNext())
        {
            Item item = (Item) it.next();
            if (item.myContent != null) // is a file
            {
                File file = new File(root, item.myPath);
                PrintWriter pw = new PrintWriter(new FileOutputStream(file));
                pw.print(item.myContent);
                pw.close();
            }
        }
    }
    /**
     * Add a new item to the working copy
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
     * Set text (content) status of the item at a path
     * @param path      the path, where the status is set
     * @param status    the new text status
     */
    public void setItemTextStatus(String path, SVNStatusKind status)
    {
        ((Item) items.get(path)).textStatus = status;
    }

    /**
     * Set property status of the item at a path
     * @param path      the path, where the status is set
     * @param status    the new property status
     */
    public void setItemPropStatus(String path, SVNStatusKind status)
    {
        ((Item) items.get(path)).propStatus = status;
    }

    /**
     * Set the revision number of the item at a path
     * @param path      the path, where the revision number is set
     * @param revision  the new revision number
     */
    public void setItemWorkingCopyRevision(String path, long revision)
    {
        ((Item) items.get(path)).workingCopyRev = new SVNRevision.Number(revision);
    }

    /**
     * Returns the file content of the item at a path
     * @param path  the path, where the content is retrieved
     * @return  the content of the file
     */
    public String getItemContent(String path)
    {
        return ((Item) items.get(path)).myContent;
    }

    /**
     * Set the file content of the item at a path
     * @param path      the path, where the content is set
     * @param content   the new content
     */
    public void setItemContent(String path, String content)
    {
        // since having no content signals a directory, changes of removing the
        // content or setting a former not set content is not allowed. That
        // would change the type of the item.
        Assert.assertNotNull("cannot unset content", content);
        Item i = (Item) items.get(path);
        Assert.assertNotNull("cannot set content on directory", i.myContent);
        i.myContent = content;
    }

    /**
     * set the flag to check the content of item at a path during next check.
     * @param path      the path, where the flag is set
     * @param check     the flag
     */
    public void setItemCheckContent(String path, boolean check)
    {
        Item i = (Item) items.get(path);
        i.checkContent = check;
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
     * Set the expected lock state at a path
     * @param path      the path, where the lock state is set
     * @param isLocked  the flag
     */
    public void setItemIsLocked(String path, boolean isLocked)
    {
        Item i = (Item) items.get(path);
        i.isLocked = isLocked;
    }

    public void setItemIsCopied(String path, boolean isCopied) {
        Item i = (Item) items.get(path);
        i.isCopied = isCopied;
    }
    
    /**
     * Set the expected switched flag at a path
     * @param path          the path, where the switch flag is set
     * @param isSwitched    the flag
     */
    public void setItemIsSwitched(String path, boolean isSwitched)
    {
        Item i = (Item) items.get(path);
        i.isSwitched = isSwitched;
    }

    /**
     * Copy an expected working copy state
     * @return the copy of the exiting object
     */
    public ExpectedWC copy()
    {
        ExpectedWC c = new ExpectedWC();
        Iterator it = items.values().iterator();
        while (it.hasNext())
        {
            ((Item) it.next()).copy(c);
        }
        return c;
    }

    public ExpectedWC addExternalPartWC(ExpectedWC externalWC, String externalRootPath)
    {
    	for (Iterator iter = externalWC.items.values().iterator(); iter.hasNext();) {
			Item item = (Item) iter.next();
			if (!"".equals(item.myPath)) {
				addItem(externalRootPath + "/" + item.myPath, item.myContent);
			}
		}
    	return this;
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
            Assert.assertNotNull("not found in working copy", item);
            if (item.myContent != null)
            {
                Assert.assertEquals("state says file, working copy not",
                        tested[i].getNodeKind(),
                        item.nodeKind == SVNNodeKind.NONE ? SVNNodeKind.FILE : item.nodeKind);
            }
            else
            {
                Assert.assertEquals("state says dir, working copy not",
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
     * Check the result of a SVNClient.status versus the expected state
     * @param tested            the result to be tested
     * @param workingCopyPath   the path of the working copy
     * @throws Exception
     */
    void check(ISVNStatus[] tested, String workingCopyPath) throws Exception
    {
        // clear the touched flag of all items
        Iterator it = items.values().iterator();
        while (it.hasNext())
        {
            Item item = (Item) it.next();
            item.touched = false;
        }

        String normalizeWCPath =
                workingCopyPath.replace(File.separatorChar, '/');

        // check all result Staus object
        for (int i = 0; i < tested.length; i++)
        {
            String path = tested[i].getPath().replace(File.separatorChar, '/');
            Assert.assertTrue("status path starts not with working copy path",
                    path.startsWith(normalizeWCPath));

            // we calculate the relative path to the working copy root
            if (path.length() > workingCopyPath.length() + 1)
            {
                Assert.assertEquals("missing '/' in status path",
                        '/', path.charAt(workingCopyPath.length()));
                path = path.substring(workingCopyPath.length() + 1);
            }
            else
                // this is the working copy root itself
                path = "";

            Item item = (Item) items.get(path);
            Assert.assertNotNull("status not found in working copy for "+path, item);
            Assert.assertEquals("wrong text status in working copy for "+path,
                    item.textStatus, tested[i].getTextStatus());
            if (item.workingCopyRev != SVNRevision.INVALID_REVISION)
                Assert.assertEquals("wrong revision number in working copy for "+path,
                        item.workingCopyRev, tested[i].getRevision());
            Assert.assertEquals("lock status wrong for "+path,
                    item.isLocked, tested[i].isWcLocked());
            Assert.assertEquals("switch status wrong",
                    item.isSwitched, tested[i].isSwitched());
            Assert.assertEquals("copied status wrong for "+path,
                    item.isCopied, tested[i].isCopied());
            Assert.assertEquals("wrong prop status in working copy for "+path,
                    item.propStatus, tested[i].getPropStatus());
            if (item.myContent != null)
            {
                // file
                
                // if Item.nodeKind == NONE ==> do not check
                if (item.nodeKind != SVNNodeKind.NONE) {
                    Assert.assertEquals("state says file, working copy not for "+path,
                            item.nodeKind,
                            tested[i].getNodeKind());
                }
                if (tested[i].getTextStatus() == SVNStatusKind.NORMAL ||
                        item.checkContent)
                {
                    File input = new File(workingCopyPath, item.myPath);
                    Reader rd =
                            new InputStreamReader(new FileInputStream(input));
                    StringBuffer buffer = new StringBuffer();
                    int ch;
                    while ((ch = rd.read()) != -1)
                    {
                        buffer.append((char) ch);
                    }
                    rd.close();
                    Assert.assertEquals("content mismatch for "+path, 
                            item.myContent, buffer.toString());
                }
            }
            else
            {
                if (item.nodeKind != SVNNodeKind.NONE) {
                    Assert.assertEquals("state says dir, working copy not for "+path,
                            item.nodeKind,
                            tested[i].getNodeKind());
                }
            }
            item.touched = true;
        }

        // all items which have the touched flag not set, are missing in the
        // result array
        it = items.values().iterator();
        while (it.hasNext())
        {
            Item item = (Item) it.next();
            Assert.assertTrue("item "+item.myPath+" in working copy not found in status",
                    item.touched);
        }
    }

    /**
     * internal class to discribe a single working copy item
     */
    public class Item
    {
        /**
         * the content of a file. A directory has a null content
         */
        String myContent;
        /**
         * the relative path of the item
         */
        String myPath;
        /**
         * the text (content) status of the item
         */
        SVNStatusKind textStatus = SVNStatusKind.NORMAL;
        /**
         * the property status of the item.
         */
        SVNStatusKind propStatus = SVNStatusKind.NONE;
        /**
         * the expected revision number. INVALID_REVISION means do not check.
         */
        SVNRevision.Number workingCopyRev = SVNRevision.INVALID_REVISION;
        /**
         * flag if item has been touched. To detect missing items.
         */
        boolean touched;
        /**
         * flag if the content will be checked
         */
        boolean checkContent;
        /**
         * expected node kind. SVNNodeKind.NONE means do not check.
         */
        SVNNodeKind nodeKind = SVNNodeKind.NONE;
        /**
         * expected locked status
         */
        boolean isLocked;
        /**
         * expected switched status
         */
        boolean isSwitched;
        
        /**
         * expected copied status
         */
        boolean isCopied;

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
         * @param owner     the WC of the copy
         */
        private Item(Item source, ExpectedWC owner)
        {
            myPath = source.myPath;
            myContent = source.myContent;
            textStatus = source.textStatus;
            propStatus = source.propStatus;
            owner.items.put(myPath, this);
        }
        /**
         * copy this item
         * @param owner the new WC
         * @return  the copied item
         */
        protected Item copy(ExpectedWC owner)
        {
            return new Item(this, owner);
        }
    }
    
}
