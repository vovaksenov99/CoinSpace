package com.example.alexmelnikov.coinspace

import android.support.annotation.NonNull
import android.support.test.espresso.Espresso
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import org.junit.Rule
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    /**
     * Данные тесты не являются достаточным минимум покрытия кода. Я просто не успел добавить больше
     * TODO
     */

    @get:Rule
    var activityTestRule: ActivityTestRule<MainActivity> =
        ActivityTestRule(MainActivity::class.java)

    @Test
    fun checkTransactionAdd() {

        onView(withId(R.id.fab_new_action)).perform(click())
        onView(allOf(withId(R.id.btn_expense), isCompletelyDisplayed())).perform(click())

        onView(withId(R.id.et_sum))
        .perform(clearText(), typeText("12.65")).check(matches(withText("12.65")))

        Espresso.closeSoftKeyboard()


        onView(withId(R.id.currency_spinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("RUB"))).perform(click())
        onView(withId(R.id.currency_spinner)).check(matches(withSpinnerText(containsString("RUB"))))


        onView(withId(R.id.fab_new_action)).perform(click())

        onView(withIndex(withId(R.id.currency), 0))
            .check(matches(withText("RUB")))

        onView(withIndex(withId(R.id.sum), 0))
            .check(matches(withText("-12.65")))

        Espresso.closeSoftKeyboard()

        //onView(allOf(withId(R.id.accounts_viewpager), isCompletelyDisplayed())).perform(swipeLeft())

        onView(allOf(withId(R.id.show_periodic_transaction), isCompletelyDisplayed()))
            .perform(click())

        onView(withIndex(withId(R.id.currency), 0))
            .check(matches(withText("RUB")))

        onView(withIndex(withId(R.id.sum), 0))
            .check(matches(withText("-12.65")))

    }

    fun atPosition(position:Int, @NonNull itemMatcher:Matcher<View>):Matcher<View> {
        checkNotNull(itemMatcher)
        return object: BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: org.hamcrest.Description?) {
                description!!.appendText("has item at position " + position + ": ")
                itemMatcher.describeTo(description)
            }
            override fun matchesSafely(view:RecyclerView):Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                if (viewHolder == null)
                {
                    // has no item on such position
                    return false
                }
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: org.hamcrest.Description?) {
                description!!.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            internal var currentIndex = 0

            override fun matchesSafely(view: View): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }
}

