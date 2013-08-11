// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import org.junit.Test;
import org.w3c.dom.Document;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MavenUtilsTest {

    @Test
    public void runtime_dependencies_includes_unspecified_scope() throws Exception {
        Document pom = pomWithDependencies("" +
                "<dependency>" +
                "    <groupId>foo</groupId>" +
                "    <artifactId>bar</artifactId>" +
                "    <version>1.2</version>" +
                "</dependency>");
        assertThat(MavenUtils.getRuntimeDependencies(pom), is(asList("foo:bar")));
    }

    @Test
    public void runtime_dependencies_includes_compile_scope() throws Exception {
        Document pom = pomWithDependencies("" +
                "<dependency>" +
                "    <groupId>foo</groupId>" +
                "    <artifactId>bar</artifactId>" +
                "    <version>1.2</version>" +
                "    <scope>compile</scope>" +
                "</dependency>");
        assertThat(MavenUtils.getRuntimeDependencies(pom), is(asList("foo:bar")));
    }

    @Test
    public void runtime_dependencies_includes_runtime_scope() throws Exception {
        Document pom = pomWithDependencies("" +
                "<dependency>" +
                "    <groupId>foo</groupId>" +
                "    <artifactId>bar</artifactId>" +
                "    <version>1.2</version>" +
                "    <scope>runtime</scope>" +
                "</dependency>");
        assertThat(MavenUtils.getRuntimeDependencies(pom), is(asList("foo:bar")));
    }

    @Test
    public void runtime_dependencies_does_not_include_test_scope() throws Exception {
        Document pom = pomWithDependencies("" +
                "<dependency>" +
                "    <groupId>foo</groupId>" +
                "    <artifactId>bar</artifactId>" +
                "    <version>1.2</version>" +
                "    <scope>test</scope>" +
                "</dependency>");
        assertThat(MavenUtils.getRuntimeDependencies(pom), is(empty()));
    }

    @Test
    public void runtime_dependencies_does_not_include_provided_scope() throws Exception {
        Document pom = pomWithDependencies("" +
                "<dependency>" +
                "    <groupId>foo</groupId>" +
                "    <artifactId>bar</artifactId>" +
                "    <version>1.2</version>" +
                "    <scope>provided</scope>" +
                "</dependency>");
        assertThat(MavenUtils.getRuntimeDependencies(pom), is(empty()));
    }

    private static Document pomWithDependencies(String dependencies) throws Exception {
        return XmlUtils.parseXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<project>" +
                "<modelVersion>4.0.0</modelVersion>" +
                "<dependencies>" +
                dependencies +
                "</dependencies>" +
                "</project>");
    }
}
