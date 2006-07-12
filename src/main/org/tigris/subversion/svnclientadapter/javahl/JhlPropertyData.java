/*******************************************************************************
 * Copyright (c) 2003, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
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
