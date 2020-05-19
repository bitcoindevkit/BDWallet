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
        if (config.isPresent) {
            Log.d(TAG, "Config is present.")
            Log.d(TAG, "config.network: ${config.get().network}")
//            Completable.fromRunnable {
//                Log.d(TAG, "api starting...")
//                btcDkApi.start(workDir, network, false)
//            }.subscribeOn(Schedulers.io()).subscribe {
//                Log.d(TAG, "api stopped.")
//            }
//            Thread.sleep(2000)
//            Log.d(TAG, "api stopping...")
//            btcDkApi.stop()
        } else {
            Log.d(TAG, "Config is not present.")
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_init)
        }
    }

    fun fullScreen() {
        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    fun hideActionBar() {
        supportActionBar!!.hide()
    }

    fun showActionBar() {
        supportActionBar!!.show()
    }

    fun hideNav() {
        nav_view.visibility = View.GONE
    }

    fun showNav() {
        nav_view.visibility = View.VISIBLE
    }
}
