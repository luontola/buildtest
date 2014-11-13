// Copyright © 2011-2014 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ProjectArtifacts {

    private final File dir;
    private final VersionNumbering versionNumbering;

    public ProjectArtifacts(File dir, VersionNumbering versionNumbering) {
        this.dir = dir;
        this.versionNumbering = versionNumbering;
    }

    public File getProjectJar(String artifactId) throws IOException {
        return getProjectArtifact(pattern(artifactId, "jar"));
    }

    public File getProjectPom(String artifactId) throws IOException {
        return getProjectArtifact(pattern(artifactId, "pom"));
    }

    private String pattern(String artifactId, String extension) {
        return Pattern.quote(artifactId) + "-" + versionNumbering.getPattern() + "\\." + extension;
    }

    public File getProjectArtifact(String regex) throws IOException {
        File[] files = nonNull(dir.listFiles());
        for (File file : files) {
            if (file.getName().matches(regex)) {
                return file;
            }
        }
        throw new IllegalArgumentException("could not find the artifact " + regex + " in " + Arrays.toString(files));
    }

    private static File[] nonNull(File[] files) {
        return files == null ? new File[0] : files;
    }
}
