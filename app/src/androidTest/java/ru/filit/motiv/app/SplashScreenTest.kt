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
class SplashScreenTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashScreen::class.java)

    @Test
    fun splashScreenTest() {
        val textInputEditText = onView(
            allOf(
                withId(R.id.etLoginPhone),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutTextInputPhone),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("9"), closeSoftKeyboard())
        Thread.sleep(1500);
        val textInputEditText2 = onView(
            allOf(
                withId(R.id.etLoginPhone), withText("+79"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutTextInputPhone),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText("+7 (900) 038-30-82"))
        Thread.sleep(1500);

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.etPassword),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutTextInput),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText3.perform(replaceText("1234Pass"))//, closeSoftKeyboard())
        onView(withId(R.id.buttonAuth)).perform(click())
        Thread.sleep(15000);

        onView(withId(R.id.ivProfilePic)).perform(click());
        Thread.sleep(6000);


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

class NestedScrollToAction : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return allOf(
            withEffectiveVisibility(Visibility.VISIBLE),
            isDescendantOfA(
                Matchers.anyOf(
                    isAssignableFrom(ScrollView::class.java),
                    isAssignableFrom(HorizontalScrollView::class.java),
                    isAssignableFrom(ListView::class.java),
                    isAssignableFrom(NestedScrollView::class.java)
                )
            )
        )
    }

    override fun perform(
        uiController: UiController,
        view: View
    ) {
        if (isDisplayingAtLeast(90).matches(view)) {
            Log.i(
                TAG,
                "View is already displayed. Returning."
            )
            return
        }
        val rect = Rect()
        view.getDrawingRect(rect)
        if (!view.requestRectangleOnScreen(rect, true /* immediate */)) {
            Log.w(
                TAG,
                "Scrolling to view was requested, but none of the parents scrolled."
            )
        }
        uiController.loopMainThreadUntilIdle()
        if (!isDisplayingAtLeast(90).matches(view)) {
            throw PerformException.Builder()
                .withActionDescription(this.description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(
                    RuntimeException(
                        "Scrolling to view was attempted, but the view is not displayed"
                    )
                )
                .build()
        }
    }

    override fun getDescription(): String {
        return "scroll to"
    }

    companion object {
        private val TAG = ScrollToAction::class.java.simpleName
    }
}

