package org.tigris.subversion.svnclientadapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;

import junit.framework.TestCase;

/**
 * @author Brock Janiczak
 */
public abstract class SubversionTestCase extends TestCase {

	/**
	 * Client adapter for making calls into SVN
	 */
	protected ISVNClientAdapter client;
	private File workingCopy;
	private SVNUrl repositoryUrl;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected final void setUp() throws Exception {
		super.setUp();
		client = SVNClientAdapterFactory.createSVNClient(getClientType());
		File repository = createRepository(getRepositoryName(), getRepositoryKind());
		repositoryUrl = new SVNUrl(repository.toURI().toString().replaceFirst("file:/","file:///"));
		workingCopy = createWorkingCopy(getWorkingCopyName());
		createStandardLayout();
	}
	
	/**
	 * What SVN client type is this test case for.  <code>SVNClientAdapterFactory.JAVAHL_CLIENT</code> or
	 * <code>SVNClientAdapterFactory.COMMANDLINE_CLIENT</code>
	 * @return SVN Client Type
	 */
	protected abstract int getClientType();
	
	/**
	 * Name used for this repository.  The repository will be stored in the user's temp directory.
	 * @return Name of test repository
	 */
	protected String getRepositoryName() {
		return "SubclipseTest_Repo";
	}
	
	/**
	 * Name used for the working copy root.  THe working copy will be stored in the user's temp directory.
	 * @return
	 */
	protected String getWorkingCopyName() {
		return "SubclipseTest_WC";
	}
	
	/**
	 * What kind of repository is required for this test.  Override if a non default repository type is required.
	 * The default repository type is FSFS
	 * @return Repository Type
	 */
	protected String getRepositoryKind() {
		return ISVNClientAdapter.REPOSITORY_FSFS;
	}
	
	/**
	 * @throws SVNClientException
	 * Helper to create a repository of a specified type
	 * @param relativePath relative location of repository (from temp directory)
	 * @param kind Type of repository to create
	 * @return Location of repository
	 * @exception SVNClientException Unable to create repository
	 */
	private File createRepository(String relativePath, String kind) throws SVNClientException, IOException {
		File repositoryFile = new File(System.getProperty("java.io.tmpdir")+ File.separatorChar + relativePath);
//		repositoryFile.delete();
		removeDir(repositoryFile);
		
		client.createRepository(repositoryFile, kind);
		return repositoryFile;
	}
	
	/**
	 * Creates the working copy root in the users temp area
	 * @param relativePath Location relative tot he temp directory
	 * @return Location of working copy root
	 * @throws IOException
	 */
	private File createWorkingCopy(String relativePath) throws IOException {
		File wc = new File(System.getProperty("java.io.tmpdir")+ File.separatorChar + relativePath + "_WC");
		removeDir(wc);
//		wc.delete();
		wc.mkdirs();
		return wc;
	}
	
	/**
	 * Get teh base URL this test is using
	 * @return Base URL of repository being used
	 */
	protected final SVNUrl getRepositoryUrl() {
		return repositoryUrl;
	}
	
	/**
	 * Gets the current workign copy root for this test
	 * @return Location of working copy
	 */
	protected final File getWorkingCopy() {
		return workingCopy;
	}

	/**
	 * Create a new project.  This creates the remote directory and checks it out in the path specified
	 * in the current working copy.
	 * @param repoRelativePath Location project exists in repository
	 * @param wcRelativePath Location to checkout project in working copy
	 * @throws Exception
	 */
	protected final void createProject(String repoRelativePath, String wcRelativePath) throws Exception {
		SVNUrl project = new SVNUrl(getRepositoryUrl().toString() + repoRelativePath);
		createRemoteDirectory(repoRelativePath);
		
		File projectDir = createDirectory(wcRelativePath);
		client.checkout(project, projectDir, SVNRevision.HEAD, true);
	}

	/**
	 * Create a directory in the working copy
	 * @param wcRelativePath Location to create directory at
	 * @return Location of new directory
	 * @throws IOException
	 */
	protected final File createDirectory(String wcRelativePath) throws IOException {
		File directory = new File(workingCopy, wcRelativePath);
		directory.mkdirs();
		return directory;
	}
	
	/**
	 * Creates a new file in your working copy with the specified content
	 * @param wcRelativePath Location in working copy to create file.  Directories must already exist
	 * @param content Content to place into file
	 * @return Location of new file
	 * @throws IOException Failed to write file
	 */
	protected final File createFile(String wcRelativePath, String content) throws IOException {
		File file = new File(workingCopy, wcRelativePath);
		file.createNewFile();
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		w.write(content);
		w.close();
		return file;
	}
	
	/**
	 * Creates the 'standard' directories in the repository
	 * @throws Exception
	 */
	private void createStandardLayout() throws Exception {
		createRemoteDirectory("/trunk");
		createRemoteDirectory("/tags");
		createRemoteDirectory("/branches");
	}
	
	/**
	 * Create a directory in the repository using the default commit message
	 * @param repoRelativePath Relative location in the repository
	 * @throws MalformedURLException URL not valid
	 * @throws SVNClientException Unable to create directory
	 */
	protected final void createRemoteDirectory(String repoRelativePath) throws MalformedURLException, SVNClientException {
		SVNUrl trunk = new SVNUrl(getRepositoryUrl().toString() + repoRelativePath);
		client.mkdir(trunk, "Created Directory");
	}
	
	/**
	 * remove the given directory 
	 * @param d
	 * @throws IOException
	 */
	private void removeDir(File d) throws IOException {
		if (!d.exists()) {
			return;
		}
		
		String[] list = d.list();
		if (list == null) {
			list = new String[0];
		}
		for (int i = 0; i < list.length; i++) {
			String s = list[i];
			File f = new File(d, s);
			if (f.isDirectory()) {
				removeDir(f);
			} else {
				if (!f.delete()) {
					String message = "Unable to delete file " 
						+ f.getAbsolutePath();
					throw new IOException(message);
				}
			}
		}
		if (!d.delete()) {
			String message = "Unable to delete directory " 
				+ d.getAbsolutePath();
			throw new IOException(message);
		}
	}
}
