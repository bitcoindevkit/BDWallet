package org.btcdk.app

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_balance, R.id.navigation_deposit, R.id.navigation_withdraw
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        val app = application as ExampleApp
        val workDir = filesDir.toPath()
        val config = app.btcDkApi.loadConfig(workDir, app.network)
        if (!config.isPresent) {
            Log.d(TAG, "Config is not present.")
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_init)
        }
    }

    fun hideNav() {
        nav_view.visibility = View.GONE
    }

    fun showNav() {
        nav_view.visibility = View.VISIBLE
    }
}
