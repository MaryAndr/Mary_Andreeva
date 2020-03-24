package ru.filit.motiv.app


import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ListView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ScrollToAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SplashScreenTest2 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashScreen::class.java)

    @Test
    fun splashScreenTest2() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        //  pressBack()
        closeSoftKeyboard()
//        textInputEditText2.perform(click())
        Thread.sleep(1500);
        val appCompatTextView = onView(withId(R.id.tvSendSms))
        appCompatTextView.perform(click())
        Thread.sleep(1500);
        val textInputEditText = onView(withId(R.id.etSendSMSPhone))
        textInputEditText.perform(click());
        textInputEditText.perform(replaceText("9000"))
        textInputEditText.perform(typeText("383082"))
        Thread.sleep(1500);
        val appCompatButton = onView(withId(R.id.buttonGetPass))
        appCompatButton.perform(click())
        Thread.sleep(1500);

        val textInputEditText12 = onView(withId(R.id.etEnterPassSms))
        Thread.sleep(1500);
        textInputEditText12.perform(replaceText("1234Pass"))
        Thread.sleep(1500);
        val appCompatButton2 = onView(withId(R.id.buttonEnterSms))
        val perform = appCompatButton2.perform(click())
        Thread.sleep(15000);
//        onView((withId(R.id.buttonEnterSms).matches(withText(text:"Apple")))

        onView(withId(R.id.ivProfilePic)).perform(click());
        Thread.sleep(15000);
        onView(withId(R.id.tvExit)).perform( betterScrollTo(), click());

    }

    fun betterScrollTo(): ViewAction? {
        return actionWithAssertions(NestedScrollToAction())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}


