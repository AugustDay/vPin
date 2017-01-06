package uw.virtualpin;

import android.app.Instrumentation;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.core.IsNot;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Random;

import uw.virtualpin.Activities.InboxActivity;
import uw.virtualpin.Activities.LoginActivity;
import uw.virtualpin.Activities.RegisterActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Austin on 12/7/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegisterActivityInstrumentationTest {

    private Random r = new Random();

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(
            RegisterActivity.class);

    private void lookForToast(String theToastMessage) {
        //verify that the correct Toast appears
        onView(withText(theToastMessage))
                .inRoot(withDecorView(IsNot.not(is(mActivityRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private String generateValidUniqueEmail() {
        return "validEmail" + (r.nextInt(10) + 1)
                + (r.nextInt(10) + 1) + (r.nextInt(10) + 1)
                + (r.nextInt(10) + 1) + (r.nextInt(100) + 1)
                + "@uw.edu";
    }

    private String generateValidUniqueUsername() {
        return "validUsername" + (r.nextInt(10) + 1)
                + (r.nextInt(10) + 1) + (r.nextInt(10) + 1)
                + (r.nextInt(10) + 1) + (r.nextInt(100) + 1);
    }

    @Test
    public void testRegistrationAttemptWithoutEnteringCredentials() {
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Enter username");
    }

    @Test
    public void testRegistrationAttemptWithTooShortUsername() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("short"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Enter username of at least 6 characters");
    }

    @Test
    public void testRegistrationAttemptWithNoPassword() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("validUsername"));
        onView(withId(R.id.editText_email))
                .perform(typeText("validEmail@gmail.com"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Enter password");
    }

    @Test
    public void testRegistrationAttemptWithTooShortPassword() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("validUsername"));
        onView(withId(R.id.editText_email))
                .perform(typeText("validEmail@gmail.com"));
        onView(withId(R.id.editText_password))
                .perform(typeText("short"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Enter password of at least 6 characters");
    }

    @Test
    public void testRegistrationAttemptWithInvalidEmail() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("validUsername"));
        onView(withId(R.id.editText_email))
                .perform(typeText("invalidEmail"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Enter a valid email address");
    }

    @Test
    public void testRegistrationAttemptWithNoFirstName() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("validUsername"));
        onView(withId(R.id.editText_email))
                .perform(typeText("validEmail@gmail.com"));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Enter Your First Name");
    }

    @Test
    public void testRegistrationAttemptWithNoLastName() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("validUsername"));
        onView(withId(R.id.editText_email))
                .perform(typeText("validEmail@gmail.com"));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.editText_firstName))
                .perform(typeText("validFirstName"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Enter Your Last Name");
    }

    @Test
    public void testRegistrationAttemptInvalidUsername() {
        //Generate an email address and username
        String email = generateValidUniqueEmail();

        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("invalid!"));
        onView(withId(R.id.editText_email))
                .perform(typeText(email));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.editText_firstName))
                .perform(typeText("validFirstName"));
        onView(withId(R.id.editText_lastname))
                .perform(typeText("validLasttName"));
        onView(withId(R.id.button_register))
                .perform(click());

        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("invalid!"));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.button_login))
                .perform(click());

        lookForToast("Invalid login information.");
    }

    @Test
    public void testRegistrationAttemptValidCredentials() {
        //Generate an email address and username
        String email = generateValidUniqueEmail();
        String username = generateValidUniqueUsername();

        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText(username));
        onView(withId(R.id.editText_email))
                .perform(typeText(email));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.editText_firstName))
                .perform(typeText("validFirstName"));
        onView(withId(R.id.editText_lastname))
                .perform(typeText("validLasttName"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Register successfully added!");
    }

    @Test
    public void testRegistrationAttemptDuplicateUsername() {
        //Generate an email address and username
        String email = generateValidUniqueEmail();

        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("AlreadyTakenUsername"));
        onView(withId(R.id.editText_email))
                .perform(typeText(email));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.editText_firstName))
                .perform(typeText("validFirstName"));
        onView(withId(R.id.editText_lastname))
                .perform(typeText("validLasttName"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Username already in Use ");
    }

    @Test
    public void testRegistrationAttemptDuplicateEmail() {
        //Generate an email address and username
        String email = generateValidUniqueEmail();
        String username = generateValidUniqueUsername();

        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText(username));
        onView(withId(R.id.editText_email))
                .perform(typeText("AlreadyTaken@gmail.com"));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.editText_firstName))
                .perform(typeText("validFirstName"));
        onView(withId(R.id.editText_lastname))
                .perform(typeText("validLasttName"));
        onView(withId(R.id.button_register))
                .perform(click());

        lookForToast("Email already in Use ");
    }

    @Test
    public void testReachLoginActivity() {
        // register next activity that need to be monitored.
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(), null, false);

        onView(withId(R.id.button_backToLogin))
                .perform(click());

        //Watch for the timeout
        LoginActivity nextActivity = (LoginActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // next activity is opened and captured.
        assertNotNull(nextActivity);
    }

    @Test
    public void testRegistrationAttemptValidCredentialsCanLogin() {
        //Generate an email address and username
        String email = generateValidUniqueEmail();
        String username = generateValidUniqueUsername();

        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText(username));
        onView(withId(R.id.editText_email))
                .perform(typeText(email));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.editText_firstName))
                .perform(typeText("validFirstName"));
        onView(withId(R.id.editText_lastname))
                .perform(typeText("validLasttName"));
        onView(withId(R.id.button_register))
                .perform(click());

        //lookForToast("Register successfully added!");

        // register next activity that need to be monitored.
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(InboxActivity.class.getName(), null, false);

        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText(username));
        onView(withId(R.id.editText_password))
                .perform(typeText("validPassword"));
        onView(withId(R.id.button_login))
                .perform(click());

        //Watch for the timeout
        InboxActivity nextActivity = (InboxActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // next activity is opened and captured.
        assertNotNull(nextActivity);
    }
}
