/*******************************************************************************
 * Copyright (c) 2005, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.commandline;

/**
 * The type of action triggering the notification
 */
public interface CmdLineNotifyAction
{
    /** Adding a path to revision control. */
    public static final int add = 0;

    /** Copying a versioned path. */
    public static final int copy = 1;

    /** Deleting a versioned path. */
    public static final int delete =2;

    /** Restoring a missing path from the pristine text-base. */
    public static final int restore = 3;

    /** Reverting a modified path. */
    public static final int revert = 4;

    /** A revert operation has failed. */
    public static final int failed_revert = 5;

    /** Resolving a conflict. */
    public static final int resolved = 6;

    /** Skipping a path. */
    public static final int skip = 7;

    /* The update actions are also used for checkouts, switches, and merges. */

    /** Got a delete in an update. */
    public static final int update_delete = 8;

    /** Got an add in an update. */
    public static final int update_add = 9;

    /** Got any other action in an update. */
    public static final int update_update = 10;

    /** The last notification in an update */
    public static final int update_completed = 11;

    /** About to update an external module, use for checkouts and switches too,
     * end with @c svn_wc_update_completed.
     */
    public static final int update_external = 12;

    /** The last notification in a status (including status on externals). */
    public static final int status_completed = 13;

    /** Running status on an external module. */
    public static final int status_external = 14;


    /** Committing a modification. */
    public static final int commit_modified = 15;

    /** Committing an addition. */
    public static final int commit_added = 16;

    /** Committing a deletion. */
    public static final int commit_deleted = 17;

    /** Committing a replacement. */
    public static final int commit_replaced = 18;

    /** Transmitting post-fix text-delta data for a file. */
    public static final int commit_postfix_txdelta = 19;

    /** Processed a single revision's blame. */
    public static final int blame_revision = 20;

    /**
     * @since 1.2
     * Locking a path
     */
    public static final int locked = 21;

    /**
     * @since 1.2
     * Unlocking a path
     */
    public static final int unlocked = 22;

    /**
     * @since 1.2
     * Failed to lock a path
     */
    public static final int failed_lock = 23;

    /**
     * @since 1.2
     * Failed to unlock a path
     */
    public static final int failed_unlock = 24;

    /**
     * textual representation of the action types
     */
    public static final String[] actionNames =
    {
        "add",
        "copy",
        "delete",
        "restore",
        "revert",
        "failed revert",
        "resolved",
        "skip",
        "update delete",
        "update add",
        "update modified",
        "update completed",
        "update external",
        "status completed",
        "status external",
        "sending modified",
        "sending added   ",
        "sending deleted ",
        "sending replaced",
        "transfer",
        "blame revision processed",
        "locked",
        "unlocked",
        "locking failed",
        "unlocking failed",
    };
}
