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
 *  status of the text or the property of the item triggering the notification
 */
public interface CmdLineNotifyStatus
{
    /** It not applicable*/
    public static final int inapplicable = 0;

    /** Notifier doesn't know or isn't saying. */
    public static final int unknown = 1;

    /** The state did not change. */
    public static final int unchanged = 2;

    /** The item wasn't present. */
    public static final int missing = 3;

    /** An unversioned item obstructed work. */
    public static final int obstructed = 4;

    /** Pristine state was modified. */
    public static final int changed = 5;

    /** Modified state had mods merged in. */
    public static final int merged = 6;

    /** Modified state got conflicting mods. */
    public static final int conflicted = 7;

    /**
     * the textual represention for the status types
     */
    public static final String[] statusNames =
    {
        "inapplicable",
        "unknown",
        "unchanged",
        "missing",
        "obstructed",
        "changed",
        "merged",
        "conflicted",
    };
}

