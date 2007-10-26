package org.tigris.subversion.svnclientadapter;

/**
 * The result returned by the callback API used to handle conflicts
 * encountered during merge/update/switch operations.
 */
public class SVNConflictResult {
    /**
     * Nothing done to resolve the conflict; conflict remains.
     */
    public static final int postpone = 0;

    /**
     * Resolve the conflict by choosing the base file.
     */
    public static final int chooseBase = 1;

    /**
     * Resolve the conflict by choosing the incoming (repository)
     * version of the object.
     */
    public static final int chooseTheirs = 2;

    /**
     * Resolve the conflict by choosing own (local) version of the
     * object.
     */
    public static final int chooseMine = 3;

    /**
     * Resolve the conflict by choosing the merged object
     * (potentially manually edited).
     */
    public static final int chooseMerged = 4;

    /**
     * A value corresponding to the
     * <code>svn_wc_conflict_choice_t</code> enum.
     */
    private int choice;

    /**
     * The path to the result of a merge, or <code>null</code>.
     */
    private String mergedPath;

    /**
     * Create a new conflict result instace.
     */
    public SVNConflictResult(int choice, String mergedPath)
    {
      this.choice = choice;
      this.mergedPath = mergedPath;
    }

    /**
     * @return A value corresponding to the
     * <code>svn_wc_conflict_choice_t</code> enum.
     */
    public int getChoice()
    {
        return choice;
    }

    /**
     * @return The path to the result of a merge, or <code>null</code>.
     */
    public String getMergedPath()
    {
        return mergedPath;
    }	
}
