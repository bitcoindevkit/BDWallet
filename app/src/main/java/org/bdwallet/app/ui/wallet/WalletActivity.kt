package org.bdwallet.app.ui.wallet


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class WalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_deposit,
                R.id.navigation_balance,
                R.id.navigation_withdraw
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        generateQRcode()
    }


    private fun generateQRcode(){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        var address:String = BDWApplication.instance.getNewAddress()
        writeToFile(address,"BTCAddress.txt")

        val url = URL("https://www.bitcoinqrcodemaker.com/api/?style=bitcoin&address=" + address)
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        //Write qrcode to file as png
        bitmapToFile(bmp, "QRCODE.png")
    }

    private fun writeToFile(address: String,fileNameToSave: String): File?{
        var file: File? = null
        return try {
            file = File(this.getExternalFilesDir(null)!!.absolutePath
                .toString() + File.separator + fileNameToSave
            )


            //WHERE the permission denied happened
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()


            //write the bytes in file
            val fos = FileOutputStream(file)
            println(file.absolutePath)
            fos.write(address.toByteArray())
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
    private fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
//        verifyStoragePermissions(onResume());
        // Check whether this app has write external storage permission or not.
//        val PERMISSIONS_STORAGE = arrayOf(
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//        // Check whether this app has write external storage permission or not.
//        val writeExternalStoragePermission: Int = ContextCompat.checkSelfPermission(this,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//// If do not grant write external storage permission.
//// If do not grant write external storage permission.
//        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            // Request user to grant write external storage permission.
//
//            ActivityCompat.requestPermissions(this,
//                PERMISSIONS_STORAGE,
//                1
//            )
//        }

        var file: File? = null
        return try {
            file = File(this.getExternalFilesDir(null)!!.absolutePath
                .toString() + File.separator + fileNameToSave
            )
//            file.mkdir()

            //WHERE the permission denied happened
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            println(file.absolutePath)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
}