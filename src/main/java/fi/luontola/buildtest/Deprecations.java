// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import com.google.common.base.Joiner;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.*;

public class Deprecations {

    private static final Type DEPRECATED = Type.getType(Deprecated.class);

    private final List<String> expectedDeprecations = new ArrayList<String>();

    public Deprecations add(String expectedDeprecation) {
        this.expectedDeprecations.add(expectedDeprecation);
        return this;
    }

    public void verify(Iterable<ClassNode> classes) {
        List<String> actual = findDeprecations(classes);
        List<String> unexpected = new ArrayList<String>();
        unexpected.addAll(actual);
        unexpected.removeAll(expectedDeprecations);
        assertEmpty("There were unexpected deprecations", unexpected);

        List<String> unaccounted = new ArrayList<String>();
        unaccounted.addAll(expectedDeprecations);
        unaccounted.removeAll(actual);
        assertEmpty("Expected some things to be deprecated by they were not", unaccounted);
    }

    private List<String> findDeprecations(Iterable<ClassNode> classes) {
        List<String> deprecations = new ArrayList<String>();
        for (ClassNode clazz : classes) {
            if (isDeprecated(clazz)) {
                deprecations.add(format(clazz));
            }
            for (MethodNode method : clazz.methods) {
                if (isDeprecated(method)) {
                    deprecations.add(format(clazz, method));
                }
            }
            for (FieldNode field : clazz.fields) {
                if (isDeprecated(field)) {
                    deprecations.add(format(clazz, field));
                }
            }
        }
        return deprecations;
    }

    private static boolean isDeprecated(ClassNode clazz) {
        return containsDeprecated(clazz.visibleAnnotations);
    }

    private boolean isDeprecated(MethodNode method) {
        return containsDeprecated(method.visibleAnnotations);
    }

    private boolean isDeprecated(FieldNode field) {
        return containsDeprecated(field.visibleAnnotations);
    }

    private static boolean containsDeprecated(List<AnnotationNode> annotations) {
        for (AnnotationNode annotation : nonNull(annotations)) {
            if (annotation.desc.equals(DEPRECATED.getDescriptor())) {
                return true;
            }
        }
        return false;
    }

    private static <T> List<T> nonNull(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }


    // pretty printing

    private static String format(ClassNode clazz) {
        return Type.getObjectType(clazz.name).getClassName();
    }

    private static String format(ClassNode clazz, MethodNode method) {
        StringBuilder sb = new StringBuilder();
        sb.append(format(clazz));
        sb.append("#");
        sb.append(method.name);
        sb.append("(");
        Type[] args = Type.getMethodType(method.desc).getArgumentTypes();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(args[i].getClassName());
        }
        sb.append(")");
        return sb.toString();
    }

    private String format(ClassNode clazz, FieldNode field) {
        return format(clazz) + "#" + field.name;
    }

    private static void assertEmpty(String message, List<String> actual) {
        if (!actual.isEmpty()) {
            throw new AssertionError(message + ":\n" + format(actual));
        }
    }

    private static String format(List<String> deprecations) {
        String prefix = "- \"";
        String suffix = "\"\n";
        return prefix + Joiner.on(suffix + prefix).join(deprecations) + suffix;
    }
}
