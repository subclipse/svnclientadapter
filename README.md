#SVNClientAdapter

SVNClientAdapter is a high-level Java API for Subversion.

It can use one of 3 low-level svn client implementations to provide access to the Subversion API:

- JavaHL (JNI) subversion library (http://svn.collab.net/repos/svn/trunk/subversion/bindings/java/javahl/)
- SVNKit (pure Java) (http://svnkit.com/)
- svn(.exe) command line client (some functionality unavailable/unreliable with <1.3 command line clients)

SVNClientAdapter is easier to use than SVNClient class and has more features.

#How to use it :
You will need to add svnClientAdapter.jar in your classpath. 
If you wish to use JNI client (recommended), you will need svnjavahl.jar in your classpath.
If you wish to use pure java implementation of subversion low-level api, you will need svnkit.jar and ganymed.jar in your classpath.
If you wish to use command line client, you will need that client be present on your system path.

See the src/samples folder to see how to use it.

# History

This repository was cloned from its original home at http://subclipse.tigris.org
The original SVN repository housed three projects (Subclipse, svnClientAdapter and svnAnt)
Best attempt possible was made to prune out the history though branches and tags did not
make it and features like svn:externals do not translate to Git.

# License
The source code for this project is licensed under Apache 2.0 license.



