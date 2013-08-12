// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.text.*;
import java.util.Date;

public class DeprecationsTest {

    private static final String LABEL_DEPRECATED_CLASS = DeprecatedClass.class.getName();
    private static final String LABEL_DEPRECATED_METHOD = HasDeprecatedMethod.class.getName() + "#theMethod(java.lang.String, int)";
    private static final String LABEL_DEPRECATED_FIELD = HasDeprecatedField.class.getName() + "#theField";

    @Rule
    public final ExpectedException thrown = ExpectedException.none().handleAssertionErrors();

    private final Deprecations deprecations = new Deprecations();

    @Test
    public void it_is_ok_to_have_no_deprecations() {
        deprecations.verify(new StubClasses(RegularClass.class));
    }

    @Test
    public void it_is_ok_to_have_expected_deprecations() {
        deprecations
                .add(LABEL_DEPRECATED_CLASS)
                .add(LABEL_DEPRECATED_METHOD)
                .add(LABEL_DEPRECATED_FIELD);
        deprecations.verify(new StubClasses(DeprecatedClass.class, HasDeprecatedMethod.class, HasDeprecatedField.class));
    }

    @Test
    public void fails_if_a_deprecation_has_been_around_longer_than_the_transition_period() {
        deprecations.add(LABEL_DEPRECATED_CLASS, "2000-01-01", 10);
        deprecations.verify(new StubClasses(DeprecatedClass.class), dateTime("2000-01-11 23:59"));

        thrown.expect(AssertionError.class);
        thrown.expectMessage("It is now time to remove the following deprecated things");
        thrown.expectMessage(LABEL_DEPRECATED_CLASS);

        deprecations.verify(new StubClasses(DeprecatedClass.class), dateTime("2000-01-12 00:01"));
    }

    @Test
    public void fails_if_class_was_deprecated_without_us_expecting_it() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("There were unexpected deprecations");
        thrown.expectMessage(LABEL_DEPRECATED_CLASS);

        deprecations.verify(new StubClasses(DeprecatedClass.class));
    }

    @Test
    public void fails_if_method_was_deprecated_without_us_expecting_it() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("There were unexpected deprecations");
        thrown.expectMessage(LABEL_DEPRECATED_METHOD);

        deprecations.verify(new StubClasses(HasDeprecatedMethod.class));
    }

    @Test
    public void fails_if_field_was_deprecated_without_us_expecting_it() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("There were unexpected deprecations");
        thrown.expectMessage(LABEL_DEPRECATED_FIELD);

        deprecations.verify(new StubClasses(HasDeprecatedField.class));
    }

    @Test
    public void fails_if_we_expect_something_to_be_deprecated_but_is_not() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Expected some things to be deprecated by they were not");
        thrown.expectMessage(LABEL_DEPRECATED_CLASS);

        deprecations.add(LABEL_DEPRECATED_CLASS);
        deprecations.verify(new StubClasses());
    }

    @Test
    public void reports_multiple_failures_in_a_list_and_ready_to_be_copy_pasted_as_strings() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("\n- \"" + LABEL_DEPRECATED_CLASS + "\"\n");
        thrown.expectMessage("\n- \"" + LABEL_DEPRECATED_FIELD + "\"\n");

        deprecations.add(LABEL_DEPRECATED_CLASS)
                .add(LABEL_DEPRECATED_FIELD);
        deprecations.verify(new StubClasses());
    }


    private Date dateTime(String s) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

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

