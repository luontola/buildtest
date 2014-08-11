// Copyright © 2011-2014 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.containsString;

public class CompositeMatcherTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void passes_if_everything_matched() {
        CompositeMatcher<String> m = new CompositeMatcher<String>()
                .assertThatIt(containsString("a"));

        m.check("aa");
        m.check("ab");

        m.rethrowErrors();
    }

    @Test
    public void throws_error_if_at_least_one_did_not_match() {
        CompositeMatcher<String> m = new CompositeMatcher<String>()
                .assertThatIt(containsString("a"));

        m.check("aa");
        m.check("bb");

        thrown.expect(AssertionError.class);
        m.rethrowErrors();
    }

    @Test
    public void thrown_exception_shows_messages_of_all_failed_matches() {
        CompositeMatcher<String> m = new CompositeMatcher<String>()
                .assertThatIt(containsString("x"));

        m.check("foo");
        m.check("bar");

        thrown.expect(AssertionError.class);
        thrown.expectMessage("was \"foo\"");
        thrown.expectMessage("was \"bar\"");
        m.rethrowErrors();
    }
}
