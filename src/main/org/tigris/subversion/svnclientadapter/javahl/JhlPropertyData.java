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
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.File;

import org.tigris.subversion.javahl.PropertyData;
import org.tigris.subversion.svnclientadapter.ISVNProperty;

/**
 * A JavaHL based implementation of {@link ISVNProperty}.
 * Actually just an adapter from {@link org.tigris.subversion.javahl.PropertyData}
 * 
 * @author philip schatz
 */
public class JhlPropertyData implements ISVNProperty
{
    private PropertyData _propertyData;
    
    /**
     * Constructor
     * @param propertyData
     */
    public JhlPropertyData(PropertyData propertyData)
    {
        _propertyData = propertyData;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getName()
     */
    public String getName()
    {
        return _propertyData.getName();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getValue()
     */
    public String getValue()
    {
        return _propertyData.getValue();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getFile()
     */
    public File getFile()
    {
        return new File(_propertyData.getPath()).getAbsoluteFile();
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getData()
     */
    public byte[] getData()
    {
        return _propertyData.getData();
    }
}
