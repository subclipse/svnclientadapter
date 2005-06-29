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

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.ws.fs.FSEntryFactory;

/**
 * Configuration for java svn client adapter
 */
public class JavaSvnConfig {
    private String myUserName;

    private String myPassword;

    private JavaSvnNotificationHandler notificationHandler;
    
    public JavaSvnConfig() {
        notificationHandler = new JavaSvnNotificationHandler();
    }    

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addNotifyListener(org.tigris.subversion.svnclientadapter.ISVNNotifyListener)
     */
    public void addNotifyListener(ISVNNotifyListener listener) {
        notificationHandler.add(listener);
    }
    
    /**
     * @return Returns the notificationHandler.
     */
    public JavaSvnNotificationHandler getNotificationHandler() {
        return notificationHandler;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#removeNotifyListener(org.tigris.subversion.svnclientadapter.ISVNNotifyListener)
     */
    public void removeNotifyListener(ISVNNotifyListener listener) {
        notificationHandler.remove(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setUsername(java.lang.String)
     */
    public void setUsername(String username) {
        this.myUserName = username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setPassword(java.lang.String)
     */
    public void setPassword(String password) {
        this.myPassword = password;
    }    

    public String getUsername() {
        return myUserName;
    }
    
    public String getPassword() {
        return myPassword;
    }
    
    
    
}
