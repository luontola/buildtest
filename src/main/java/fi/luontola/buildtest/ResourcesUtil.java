// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import java.io.*;
import java.util.Properties;

public class ResourcesUtil {

    public static Properties getProperties(String name) {
        Properties p = new Properties();
        try {
            InputStream in = ResourcesUtil.class.getClassLoader().getResourceAsStream(name);
            p.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }
}
