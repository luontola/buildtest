// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JarUtilsTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none().handleAssertionErrors();

    @Test
    public void can_iterate_all_classes_in_a_JAR() {
        List<String> classNames = new ArrayList<String>();
        for (ClassNode classNode : JarUtils.classesIn(Testing.getDummyJar())) {
            classNames.add(classNode.name);
        }
        Collections.sort(classNames);

        assertThat(classNames, is(asList("DummyAbstractClass", "DummyClass", "DummyClass$1", "DummyInterface")));
    }

    @Test
    public void can_test_that_JAR_contains_only_allowed_entries() throws Exception {
        List<String> expected = new ArrayList<String>(asList(
                "DummyInterface.class", "DummyClass.class", "DummyClass$1.class", "DummyAbstractClass.class",
                "META-INF/maven/com.example/", "META-INF/MANIFEST.MF"));
        JarUtils.assertContainsOnly(Testing.getDummyJar(), expected);

        expected.remove("DummyInterface.class");
        thrown.expect(AssertionError.class); // XXX: workaround for https://github.com/junit-team/junit/pull/583
        thrown.expectMessage("dummy-1.0.jar contained a not allowed entry: DummyInterface.class");
        JarUtils.assertContainsOnly(Testing.getDummyJar(), expected);
    }
}
