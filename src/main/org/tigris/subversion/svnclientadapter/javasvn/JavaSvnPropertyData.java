/*
 *  Copyright(c) 2003-2004 by the authors indicated in the @author tags.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tigris.subversion.svnclientadapter.javasvn;

import java.io.File;

import org.tigris.subversion.svnclientadapter.ISVNProperty;


public class JavaSvnPropertyData implements ISVNProperty {
    private String name;
    private String value;
    private File file;
    private byte[] data;
    
    
    public JavaSvnPropertyData(File file, String name, String value, byte[] data) {
        this.file = file;
        this.name = name;
        this.value = value;
        this.data = data;
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getValue()
     */
    public String getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getFile()
     */
    public File getFile() {
        return file;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getData()
     */
    public byte[] getData() {
        return data;
    }

}
