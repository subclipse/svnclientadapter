# Command Line Adapter

This adapter is not actively maintained. It should still work but as new methods have been
added to ISVNClientAdapter over the last several years, implementations have not been added
to this adapter.  It is also possible the output of the SVN command line has changed over
the years since the original code was added so some of the methods which do have implementations
might not work correctly.

This adapter works by running the svn command line process and capturing and interpreting the
results.  It will run the version of svn it finds on PATH so Subversion command line
must be installed and available on PATH.

