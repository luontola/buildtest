// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

// XXX: workaround for https://github.com/junit-team/junit/pull/583
public class JarContentAssertionError extends AssertionError {

    public JarContentAssertionError(String message) {
        super(message);
    }
}
