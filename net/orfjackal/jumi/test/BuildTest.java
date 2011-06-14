// Copyright © 2011, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package net.orfjackal.jumi.test;

import net.orfjackal.jumi.launcher.daemon.Daemon;
import org.intellij.lang.annotations.Language;
import org.junit.*;
import org.w3c.dom.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class BuildTest {

    private static final List<String> JAR_WHITELIST = Arrays.asList(
            "META-INF/maven/net.orfjackal.jumi/",
            "net/orfjackal/jumi/"
    );
    private static final Map<String, List<String>> DEPENDENCIES = new HashMap<String, List<String>>();

    static {
        DEPENDENCIES.put("jumi-api", Arrays.<String>asList());
        DEPENDENCIES.put("jumi-core", Arrays.asList("net.orfjackal.jumi:jumi-api"));
        DEPENDENCIES.put("jumi-daemon", Arrays.<String>asList());
        DEPENDENCIES.put("jumi-launcher", Arrays.asList("net.orfjackal.jumi:jumi-core"));
    }

    private File[] projectJars;
    private File[] projectPoms;

    @Before
    public void init() throws IOException {
        projectJars = TestEnvironment.getProjectJars();
        projectPoms = TestEnvironment.getProjectPoms();
    }

    @Test
    public void embedded_daemon_jar_contains_only_jumi_classes() throws IOException {
        assertJarContainsOnly(JAR_WHITELIST, Daemon.getDaemonJarAsStream());
    }

    @Test
    public void project_artifact_jars_contain_only_jumi_classes() throws IOException {
        assertThat("project JARs", projectJars.length, is(4));
        for (File projectJar : projectJars) {
            assertJarContainsOnly(JAR_WHITELIST, projectJar);
        }
    }

    @Test
    public void project_artifact_poms_do_not_have_external_dependencies() throws Exception {
        assertThat("project POMs", projectPoms.length, is(4));
        for (File projectPom : projectPoms) {
            Document doc = parseXml(projectPom);
            String artifactId = xpath("/project/artifactId", doc);
            assertThat("dependencies of " + artifactId, getRuntimeDependencies(doc), is(DEPENDENCIES.get(artifactId)));
        }
    }

    // helper methods

    private static void assertJarContainsOnly(List<String> whitelist, File jar) throws IOException {
        try {
            assertJarContainsOnly(whitelist, new FileInputStream(jar));
        } catch (AssertionError e) {
            throw (AssertionError) new AssertionError(jar + " " + e.getMessage()).initCause(e);
        }
    }

    private static void assertJarContainsOnly(List<String> whitelist, InputStream jarAsStream) throws IOException {
        JarInputStream in = new JarInputStream(jarAsStream);
        JarEntry entry;
        while ((entry = in.getNextJarEntry()) != null) {
            assertIsWhitelisted(entry, whitelist);
        }
    }

    private static void assertIsWhitelisted(JarEntry entry, List<String> whitelist) {
        boolean allowed = false;
        for (String s : whitelist) {
            allowed |= entry.getName().startsWith(s);
            allowed |= s.startsWith(entry.getName());
        }
        assertTrue("contained a not allowed entry: " + entry, allowed);
    }

    private static List<String> getRuntimeDependencies(Document doc) throws XPathExpressionException {
        NodeList nodes = (NodeList) xpath(
                "/project/dependencies/dependency[not(scope) or scope='compile' or scope='runtime']",
                doc, XPathConstants.NODESET);

        List<String> results = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node dependency = nodes.item(i);

            String groupId = xpath("groupId", dependency);
            String artifactId = xpath("artifactId", dependency);
            results.add(groupId + ":" + artifactId);
        }
        return results;
    }

    // xml parsing

    private static Document parseXml(File file) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false);
        return domFactory.newDocumentBuilder().parse(file);
    }

    private static String xpath(@Language("XPath") String expression, Node node) throws XPathExpressionException {
        return (String) xpath(expression, node, XPathConstants.STRING);
    }

    private static Object xpath(@Language("XPath") String expression, Node item, QName returnType) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return xpath.evaluate(expression, item, returnType);
    }
}
