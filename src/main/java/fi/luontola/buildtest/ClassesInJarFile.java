// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import com.google.common.collect.AbstractIterator;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.Iterator;
import java.util.jar.*;

public class ClassesInJarFile implements Iterable<ClassNode> {
    private final File jarFile;

    public ClassesInJarFile(File jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public Iterator<ClassNode> iterator() {
        try {
            return new ClassNodeIterator(jarFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
