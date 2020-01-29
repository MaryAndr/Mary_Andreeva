package ru.filit.motiv.app

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import ru.filit.motiv.app.fragments.main.CostsAndReplenishment
import ru.filit.motiv.app.fragments.main.HelpFragment
import ru.filit.motiv.app.fragments.main.MainPageFragment
import ru.filit.motiv.app.fragments.main.MyTariffFragment

class MainPageActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val fragment = MainPageFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_costs -> {
                    val fragment = CostsAndReplenishment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_tariff -> {
                    val fragment = MyTariffFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_help -> {
                    val fragment = HelpFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.costs)))
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.abs_layout)

        val fragment = MainPageFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
            .commit()

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }
}
