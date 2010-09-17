/*******************************************************************************
 * Copyright (c) 2003, 2006 svnClientAdapter project and others.
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
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.File;
import java.net.MalformedURLException;

import org.tigris.subversion.javahl.PropertyData;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * A JavaHL based implementation of {@link ISVNProperty}.
 * Actually just an adapter from {@link org.tigris.subversion.javahl.PropertyData}
 * 
 * @author philip schatz
 */
public class JhlPropertyData implements ISVNProperty
{
    private PropertyData _propertyData;
    private boolean isForUrl;
    
    /**
     * Factory method for properties on local resource (file or dir)
     * @param propertyData
     * @return a JhlPropertyData constructed from supplied propertyData
     */
    public static JhlPropertyData newForFile(PropertyData propertyData)
    {
    	return new JhlPropertyData(propertyData, false);
    }

    /**
     * Factory method for properties on remote resource (url)
     * @param propertyData
     * @return a JhlPropertyData constructed from supplied propertyData
     */
    public static JhlPropertyData newForUrl(PropertyData propertyData)
    {
    	return new JhlPropertyData(propertyData, true);
    }

    /**
     * Constructor
     * @param propertyData
     */
    private JhlPropertyData(PropertyData propertyData, boolean isForUrl)
    {
        this._propertyData = propertyData;
        this.isForUrl = isForUrl;
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
    	return isForUrl ? null : new File(_propertyData.getPath()).getAbsoluteFile();
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getUrl()
     */
    public SVNUrl getUrl()
    {
		try {
	    	return isForUrl ? new SVNUrl(_propertyData.getPath()) : null;
        } catch (MalformedURLException e) {
            //should never happen.
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNProperty#getData()
     */
    public byte[] getData()
    {
        return _propertyData.getData();
    }
}
