package me.pisal.compass_sensor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){

    lateinit var orientation: Orientation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        orientation = Orientation(this)
        orientation.pointer = imgCompass
        orientation.resume()

         // Optional: observe bearing change and use it as we wish
        orientation.orientationListener = { bearing ->
            txtBearing.text = getString(R.string.bearing_f, bearing)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::orientation.isInitialized) {
            orientation.pause()
        }
    }
}
