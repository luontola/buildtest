// Copyright © 2011-2013, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.test.util;

import com.google.common.collect.AbstractIterator;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.jar.*;

public class JarFileUtils {

    public static Iterable<ClassNode> classesIn(final Path jarFile) {
        return () -> {
            try {
                return new ClassNodeIterator(jarFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static void walkZipFile(Path jarFile, SimpleFileVisitor<Path> visitor) throws Exception {
        URI uri = new URI("jar", jarFile.toUri().toString(), null);
        HashMap<String, String> env = new HashMap<>();
        try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
            Files.walkFileTree(fs.getPath("/"), visitor);
        }
    }

    private static class ClassNodeIterator extends AbstractIterator<ClassNode> {

        private final JarInputStream in;

        public ClassNodeIterator(Path jarFile) throws IOException {
            // TODO: iterate this JAR using FileSystem instead of JarInputStream?
            // http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
            in = new JarInputStream(Files.newInputStream(jarFile));
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
