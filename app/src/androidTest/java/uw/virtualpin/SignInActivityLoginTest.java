package uw.virtualpin;

import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Random;

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
public class SignInActivityLoginTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

/*    @Before
    public void takeSomeTime() {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    } */

    @Test
    public void testLoginAttemptWithoutEnteringCredentials() {
        onView(withId(R.id.button_login))
                .perform(click());

        //verify that the correct Toast appears
        onView(withText("Enter Username"))
                .inRoot(withDecorView(IsNot.not(is(mActivityRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithoutPassword() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("adi1996@gmail.com"));
        onView(withId(R.id.button_login))
                .perform(click());

        //verify that the correct Toast appears
        onView(withText("Enter password"))
                .inRoot(withDecorView(IsNot.not(is(mActivityRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithShortPassword() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("adi1996@gmail.com"));
        onView(withId(R.id.editText_password))
                .perform(typeText("short"));
        onView(withId(R.id.button_login))
                .perform(click());

        //verify that the correct Toast appears
        onView(withText("Enter password of at least 6 characters"))
                .inRoot(withDecorView(IsNot.not(is(mActivityRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithWrongPassword() {
        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("august"));
        onView(withId(R.id.editText_password))
                .perform(typeText("NotActuallyMyPassword"));
        onView(withId(R.id.button_login))
                .perform(click());

        //verify that the correct Toast appears
        onView(withText("Invalid login information."))
                .inRoot(withDecorView(IsNot.not(is(mActivityRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithCorrectCredentials() {
        // register next activity that need to be monitored.
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

        // Type text and then press the button.
        onView(withId(R.id.editText_username))
                .perform(typeText("august"));
        onView(withId(R.id.editText_password))
                .perform(typeText("password"));
        onView(withId(R.id.button_login))
                .perform(click());

        //Watch for the timeout
        MainActivity nextActivity = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // next activity is opened and captured.
        assertNotNull(nextActivity);
    }

    @Test
    public void testReachRegisterActivity() {
        // register next activity that need to be monitored.
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(RegisterActivity.class.getName(), null, false);

        onView(withId(R.id.button_goToRegister))
                .perform(click());

        //Watch for the timeout
        RegisterActivity nextActivity = (RegisterActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // next activity is opened and captured.
        assertNotNull(nextActivity);
    }
}
