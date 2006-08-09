package org.tigris.subversion.svnclientadapter.testUtils;

public interface ExpectedStructure {
	
    /**
     * Add a new item to the structure (e.g. working copy or repository)
     * @param path      the path of the item
     * @param content   the content of the item. A null content signifies a
     *                  directory
     */
    void addItem(String path, String content);


}
