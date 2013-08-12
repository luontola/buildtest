// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import org.junit.*;
import org.junit.rules.ExpectedException;

public class DeprecationsTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none().handleAssertionErrors();

    private final Deprecations deprecations = new Deprecations();

    @Test
    public void do_nothing_if_nothing_is_actually_nor_expected_to_be_deprecated() {
        deprecations.verify(new StubClasses(RegularClass.class));
    }

    @Test
    public void fails_if_class_was_deprecated_without_us_expecting_it() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("There were unexpected deprecations");
        thrown.expectMessage(DeprecatedClass.class + " was deprecated");
        deprecations.verify(new StubClasses(DeprecatedClass.class));
    }

    @Test
    public void fails_if_method_was_deprecated_without_us_expecting_it() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("There were unexpected deprecations");
        thrown.expectMessage("method " + HasDeprecatedMethod.class.getName() + "#theMethod(java.lang.String, int) was deprecated");
        deprecations.verify(new StubClasses(HasDeprecatedMethod.class));
    }

    @Test
    public void fails_if_field_was_deprecated_without_us_expecting_it() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("There were unexpected deprecations");
        thrown.expectMessage("field " + HasDeprecatedField.class.getName() + "#theField was deprecated");
        deprecations.verify(new StubClasses(HasDeprecatedField.class));
    }

    // TODO: fails if deprecated thing was removed too late
    // TODO: fails if deprecated thing was removed too early
    // TODO: collect multiple failures into one report


    private static class RegularClass {
    }

    @Deprecated
    private static class DeprecatedClass {
    }

    private static class HasDeprecatedMethod {
        @Deprecated
        public void theMethod(String s, int i) {
        }
    }

    private static class HasDeprecatedField {
        @Deprecated
        private int theField;
    }
}

