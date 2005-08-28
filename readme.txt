
SVNClientAdapter
===============

SVNClientAdapter is a high-level Java API for Subversion.

It can use one of 3 low-level svn client implementations to provide access to the Subversion API:

- JavaHL (JNI) subversion library (http://svn.collab.net/repos/svn/trunk/subversion/bindings/java/javahl/)
- JavaSVN (pure Java) (http://tmate.org/svn/)
- svn(.exe) command line client (experimental)

SVNClientAdapter is easier to use than SVNClient class and has more features.

How to use it :
=============
You will need to add svnClientAdapter.jar and jakarta-regexp-1.4.jar in your classpath. 
If you wish to use jni client (recommended), you will need svnjavahl.jar in your classpath.
If you wish to use pure java implementation of subversion low-level api, you will need javasvn.jar and jsch.jar in your classpath.

See the src/samples folder to see how to use it.

cchabanois at no-log.org 