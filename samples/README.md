# SVNClientAdapter Sample

This example project shows how to use the SVNClientAdapter.
It is a Maven project and demonstrates the Maven dependencies
to add to your own project.  Without Maven, you could just download
the JAR files for the Maven artifacts and add them to your classpath.

This example use the SVNKit pure-Java adapter for simplicity
but it would be easy to adjust the code to use a different adapter.

To build run this command:

```
mvn install
```

To run the code just execute this command:

```
mvn exec:java -Dexec.mainClass="org.tigris.subversion.svnclientadapter.samples.Sample"
```



