// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import java.io.*;

public class ProjectArtifacts {

    private final File dir;

    public ProjectArtifacts(File dir) {
        this.dir = dir;
    }

    public File getProjectJar(String artifactId) throws IOException {
        return getProjectArtifact(artifactId + "-.*\\.jar");
    }

    public File getProjectPom(String artifactId) throws IOException {
        return getProjectArtifact(artifactId + "-.*\\.pom");
    }

    public File getProjectArtifact(String regex) throws IOException {
        for (File file : dir.listFiles()) {
            if (file.getName().matches(regex)) {
                return file;
            }
        }
        throw new IllegalArgumentException("could not find the artifact " + regex);
    }
}
