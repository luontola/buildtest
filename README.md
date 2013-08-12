BuildTest
=========

Utilites for asserting about build artifacts. For usage examples, see
[Jumi's BuildTest.java](https://github.com/orfjackal/jumi/blob/master/end-to-end-tests/src/test/java/fi/jumi/test/BuildTest.java)

Requires Java 6 or greater.


Version History
---------------

### Next Release

Initial release, extracted from [Jumi](http://jumi.fi/)'s build tests.
Contains the following utilities:

- `AsmMatchers` - Some Hamcrest matchers for classes
- `AsmUtils` - Checking whether classes are annotated with specific annotations
- `ClassesInJarFile` - Iterable over all classes in a JAR file
- `CompositeMatcher` - Asserting over multiple items and reporting *all* failures
- `Deprecations` - Reminder to remove deprecated classes/methods/fields after some time
- `JarUtils` - Inspecting and asserting about the contents of a JAR file
- `MavenUtils` - Reading the runtime dependencies from a Maven POM file
- `PartiallyParameterized` - JUnit runner for classes with parameterized and non-parameterized tests
- `ProjectArtifacts` - Finding project artifacts from a directory where presumably the build copied them
- `ResourcesUtil` - Reading properties files from classpath
- `VersionNumbering` - Checking the format of version numbers
- `XmlUtils` - Parsing XML files
