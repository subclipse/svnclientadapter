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

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javasvn.JavaSvnConfig;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;

public class CheckoutCommand extends JavaSvnCommand {

    /**
     * @param config
     */
    public CheckoutCommand(JavaSvnConfig config) {
        super(config);
    }

    public void checkout(SVNUrl moduleName, File destPath,
            SVNRevision revision, boolean recurse) throws SVNClientException {
        notificationHandler.setCommand(ISVNNotifyListener.Command.CHECKOUT);
        notificationHandler.logCommandLine("checkout" + (recurse ? "" : " -N")
                + " -r " + revision.toString() + " " + moduleName.toString());
        if (!destPath.exists()) {
            destPath.mkdirs();
        }
        try {
            ISVNWorkspace ws = getWorkspace(destPath);
            SVNRepositoryLocation location = SVNRepositoryLocation
                    .parseURL(moduleName.toString());
            SVNRepository repository = getRepository(location);
            long rev = getRevisionNumber(revision, repository, null, null);
            ws.checkout(SVNRepositoryLocation.parseURL(moduleName.toString()),
                    rev, false, recurse);
        } catch (SVNException e) {
            notificationHandler.logException(e);
            throw new SVNClientException(e);
        }
    }    
    
}
