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
package org.tigris.subversion.svnclientadapter.javasvn.commands;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnPropertyData;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;

public class PropertyGetCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public PropertyGetCommand(JavaSvnConfig config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyGet(java.io.File,
     *      java.lang.String)
     */
    public ISVNProperty propertyGet(File path, String propertyName)
            throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.PROPGET);

            notificationHandler.logCommandLine("propget " + propertyName + " "
                    + path);

            String value = null;
            ISVNWorkspace ws = getRootWorkspace(path);
            value = ws.getPropertyValue(getWorkspacePath(ws, path),
                    propertyName);
            if (value == null) {
                return null;
            }
            return new JavaSvnPropertyData(path, propertyName, value, value
                    .getBytes());

        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getProperties(java.io.File)
     */
    public ISVNProperty[] getProperties(File path) throws SVNClientException {
        try {
            notificationHandler.setCommand(ISVNNotifyListener.Command.PROPLIST);
            notificationHandler.logCommandLine("proplist " + path);

            Map properties = null;
            ISVNWorkspace ws = getRootWorkspace(path);
            properties = ws.getProperties(getWorkspacePath(ws, path), true,
                    false);

            if (properties == null) {
                return new ISVNProperty[0];
            }
            Collection result = new LinkedList();
            for (Iterator names = properties.keySet().iterator(); names
                    .hasNext();) {
                String name = (String) names.next();
                String value = (String) properties.get(name);
                result.add(new JavaSvnPropertyData(path, name, value,
                        (value != null) ? value.getBytes() : null));
            }
            return (ISVNProperty[]) result.toArray(new ISVNProperty[result
                    .size()]);

        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }
    
    
    
}
