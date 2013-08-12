// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import com.google.common.collect.AbstractIterator;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.ZipEntry;

import static org.junit.Assert.assertNotNull;

public class JarUtils {

    public static Properties getProperties(File jarFile, String resource) {
        try {
            URLClassLoader cl = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
            InputStream in = cl.getResourceAsStream(resource);
            assertNotNull("resource not found: " + resource, in);

            Properties p = new Properties();
            p.load(in);
            in.close();
            return p;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkAllClasses(File jarFile, CompositeMatcher<ClassNode> matcher) {
        for (ClassNode classNode : classesIn(jarFile)) {
            matcher.check(classNode);
        }
        try {
            matcher.rethrowErrors();
        } catch (AssertionError e) {
            // XXX: get the parameterized runner improved so that it would be easier to see which of the parameters broke a test
            System.err.println("Found errors in " + jarFile);
            throw e;
        }
    }

    public static void assertContainsOnly(final File jarFile, final List<String> expected) throws Exception {
        JarInputStream in = new JarInputStream(new FileInputStream(jarFile));
        try {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                if (!isWhitelisted(entry, expected)) {
                    throw new AssertionError(jarFile + " contained a not allowed entry: " + entry.getName());
                }
            }
        } finally {
            in.close();
        }
    }

    public static Iterable<ClassNode> classesIn(final File jarFile) {
        return new Iterable<ClassNode>() {
            @Override
            public Iterator<ClassNode> iterator() {
                try {
                    return new ClassNodeIterator(jarFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private static boolean isWhitelisted(ZipEntry entry, List<String> whitelist) {
        for (String s : whitelist) {
            if (entry.getName().startsWith(s)) {
                return true;
            }
        }
        return false;
    }


    private static class ClassNodeIterator extends AbstractIterator<ClassNode> {

        private final JarInputStream in;

        public ClassNodeIterator(File jarFile) throws IOException {
            // TODO: iterate this JAR using FileSystem instead of JarInputStream?
            // http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
            in = new JarInputStream(new FileInputStream(jarFile));
        }

        @Override
        protected ClassNode computeNext() {
            try {
                JarEntry entry;
                while ((entry = in.getNextJarEntry()) != null) {
                    if (!isClassFile(entry)) {
                        continue;
                    }
                    return AsmUtils.readClass(in);
                }
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return endOfData();
        }

        private static boolean isClassFile(JarEntry entry) {
            return !entry.isDirectory() && entry.getName().endsWith(".class");
        }
    }
}
