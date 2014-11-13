// Copyright © 2011-2014 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import java.io.*;
import java.util.Properties;

public class Testing {

    public static File getDummyJar() {
        try {
            Properties p = ResourcesUtil.getProperties("testing.properties");
            File testResourcesDir = new File(p.getProperty("testResourcesDir"));
            return new ProjectArtifacts(testResourcesDir, new VersionNumbering()).getProjectJar("dummy");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
