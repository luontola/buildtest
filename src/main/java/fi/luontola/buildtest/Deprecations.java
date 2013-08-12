// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.*;

public class Deprecations {

    private static final Type DEPRECATED = Type.getType(Deprecated.class);

    public void verify(Iterable<ClassNode> classes) {
        for (ClassNode clazz : classes) {
            if (isDeprecated(clazz)) {
                throw new AssertionError("There were unexpected deprecations: " +
                        "class " + format(clazz) + " was deprecated");
            }
            for (MethodNode method : clazz.methods) {
                if (isDeprecated(method)) {
                    throw new AssertionError("There were unexpected deprecations: " +
                            "method " + format(clazz, method) + " was deprecated");

                }
            }
            for (FieldNode field : clazz.fields) {
                if (isDeprecated(field)) {
                    throw new AssertionError("There were unexpected deprecations: " +
                            "field " + format(clazz, field) + " was deprecated");
                }
            }
        }
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
}
