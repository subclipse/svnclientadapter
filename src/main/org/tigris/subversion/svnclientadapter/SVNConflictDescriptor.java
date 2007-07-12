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
package org.tigris.subversion.svnclientadapter;

/**
 * The description of a merge conflict, encountered during
 * merge/update/switch operations.
 *
 * copied from JavaHL ConflictDescriptor
 */
public class SVNConflictDescriptor
{
    private String path;

    /**
     * @see org.tigris.subversion.javahl.NodeKind
     */
    private int nodeKind;

    private boolean isBinary;
    private String mimeType;

    private int action;
    private int reason;

    // File paths, present only when the conflict involves the merging
    // of two files descended from a common ancestor, here are the
    // paths of up to four fulltext files that can be used to
    // interactively resolve the conflict.
    private String basePath;
    private String reposPath;
    private String userPath;
    private String mergedPath;

    public SVNConflictDescriptor(String path, int nodeKind, boolean isBinary,
                       String mimeType, int action, int reason,
                       String basePath, String reposPath,
                       String userPath, String mergedPath)
    {
        this.path = path;
        this.nodeKind = nodeKind;
        this.isBinary = isBinary;
        this.mimeType = mimeType;
        this.action = action;
        this.reason = reason;
        this.basePath = basePath;
        this.reposPath = reposPath;
        this.userPath = userPath;
        this.mergedPath = mergedPath;
    }

    public String getPath()
    {
        return path;
    }

    public int getNodeKind()
    {
        return nodeKind;
    }

    public boolean isBinary()
    {
        return isBinary;
    }

    public String getMIMEType()
    {
        return mimeType;
    }

    public int getAction()
    {
        return action;
    }

    public int getReason()
    {
        return reason;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public String getReposPath()
    {
        return reposPath;
    }

    public String getUserPath()
    {
        return userPath;
    }

    public String getMergedPath()
    {
        return mergedPath;
    }
    /**
     * From JavaHL
     */
    public final class Action
    {
        /**
         * Attempting to change text or props.
         */
        public static final int edit = 0;

        /**
         * Attempting to add object.
         */
        public static final int add = 1;

        /**
         * Attempting to delete object.
         */
        public static final int delete = 2;
    }

    /**
     * From JavaHL
     */
    public final class Reason
    {
        /**
         * Local edits are already present.
         */
        public static final int edited = 0;

        /**
         * Another object is in the way.
         */
        public static final int obstructed = 1;

        /**
         * Object is already schedule-delete.
         */
        public static final int deleted = 2;

        /**
         * Object is unknown or missing.
         */
        public static final int missing = 3;

        /**
         * Object is unversioned.
         */
        public static final int unversioned = 4;
    }
}
