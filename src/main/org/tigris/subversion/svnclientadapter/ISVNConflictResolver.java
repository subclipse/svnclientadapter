package org.tigris.subversion.svnclientadapter;

public interface ISVNConflictResolver {
    /**
     * The callback method invoked for each conflict during a
     * merge/update/switch operation.
     *
     * @param descrip A description of the conflict.
     * @return The result of any conflict resolution.
     * @throws SubversionException If an error occurs.
     */
    public int resolve(SVNConflictDescriptor descrip) throws SVNClientException;

    /**
     * From JavaHL
     */
    public final class Result
    {
        /**
         * User did nothing; conflict remains.
         */
        public static final int conflicted = 0;

        /**
         * User has resolved the conflict.
         */
        public static final int resolved = 1;

        /**
         * User chooses the base file.
         */
        public static final int choose_base = 2;

        /**
         * User chooses the repository file.
         */
        public static final int choose_repos = 3;

        /**
         * User chooses own version of file.
         */
        public static final int choose_user = 4;

        /**
         * User chooses the merged-file (which she may have manually
         * edited).
         */
        public static final int choose_merged = 5;
    }

}
