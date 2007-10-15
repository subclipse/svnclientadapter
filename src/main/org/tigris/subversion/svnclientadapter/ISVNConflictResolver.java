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
    public final class Choice
    {
        /**
         * User did nothing; conflict remains.
         */
        public static final int postpone = 0;

        /**
         * User chooses the base file.
         */
        public static final int chooseBase = 1;

        /**
         * User chooses the repository file.
         */
        public static final int chooseTheirs = 2;

        /**
         * User chooses own version of file.
         */
        public static final int chooseMine = 3;

        /**
         * User chooses the merged-file (which she may have manually
         * edited).
         */
        public static final int chooseMerged = 4;
    }

}
