svnClientAdapter
===============

SVNClientAdapter uses javahl Subversion library (http://svn.collab.net/repos/svn/trunk/subversion/bindings/java/javahl/) to provide access to the Subversion API.

svnClientAdapter is easier to use than SVNClient class and has more features.



How to use it :
=============

// first create the SVNClient from factory
// SVNClientAdapterFactory.JAVAHL_CLIENT to use JNI client (recommanded)
// SVNClientAdapterFactory.COMMANDLINE_CLIENT to use command line client
ISVNClientAdapter svnClient = SVNClientAdapterFactory.createSVNClient(SVNClientAdapterFactory.JAVAHL_CLIENT);

// set username and password if necessary
svnClient.setUsername(username);
svnClient.setPassword(password);

// add a listener if you wish
svnClient.addNotifyListener(new ISVNNotifyListener {
    public void setCommand(int cmd) {
        // the command that is being executed. See ISVNNotifyListener.Command
		// ISVNNotifyListener.Command.ADD for example 
    }
    public void logMessage(String message) {
		
		System.out.println(message);
	}

    public void logCommandLine(String message) {
		// the command line used
        System.out.println(message);
    }

    public void logError(String message) {
		// when an error occurs
        System.out.println("error :" +message);
    }
    
    public void logCompleted(String message) {
		// when command completed
        System.out.println(message);
    }

    public void onNotify(String path, SVNNodeKind nodeKind) {
        // each time the status of a file or directory changes (file added, reverted ...)
		// nodeKind is SVNNodeKind.FILE or SVNNodeKind.DIR
    }
});

// use the svn commands 
svnClient.addFile(new File("myFile.txt");


When you use javahl client, you can use the listener JhlNotificationHandler to have the same output than svn.


cchabanois@ifrance.com