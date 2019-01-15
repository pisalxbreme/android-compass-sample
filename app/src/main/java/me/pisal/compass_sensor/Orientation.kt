package me.pisal.compass_sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView

class Orientation(context: Context){
    private var mContext: Context
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mMagnetometer: Sensor? = null
    private val mLastAccelerometer = FloatArray(3)
    private val mLastMagnetometer = FloatArray(3)
    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false
    private val mR = FloatArray(9)
    private val mOrientation = FloatArray(3)
    private var mCurrentDegree = 0f

    init {
        this.mContext = context
        initSensor()
    }

    private fun initSensor(){
        mSensorManager = mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mMagnetometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    private var sensorEventListener: SensorEventListener = object: SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor == mAccelerometer) {
                System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.size)
                mLastAccelerometerSet = true
            } else if (event.sensor == mMagnetometer) {
                System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.size)
                mLastMagnetometerSet = true
            }
            if (mLastAccelerometerSet && mLastMagnetometerSet) {
                SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer)
                SensorManager.getOrientation(mR, mOrientation)
                val azimuthInRadians = mOrientation[0]
                val azimuthInDegress = (Math.toDegrees(azimuthInRadians.toDouble()) + 360).toFloat() % 360
                Log.d("Debug", "Bearing: ${azimuthInDegress}")
                orientationListener?.invoke(azimuthInDegress)

                val ra = RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )

                ra.duration = 250
                ra.fillAfter = true
                pointer?.startAnimation(ra)

                mCurrentDegree = -azimuthInDegress
            }
        }
    }

    //region public members
    var orientationListener: OrientationListener? = null
    /**
     * (Optional)The image view that indicates the current orientation
     */
    var pointer: ImageView? = null

    fun pause(){
        mSensorManager!!.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
        mSensorManager!!.registerListener(sensorEventListener, mMagnetometer, SensorManager.SENSOR_DELAY_GAME)
    }
    fun resume(){
        mSensorManager!!.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
        mSensorManager!!.registerListener(sensorEventListener, mMagnetometer, SensorManager.SENSOR_DELAY_GAME)
    }
    //endregion
}

typealias OrientationListener = (Float) -> Unit
fun ImageView.rotate(from: Float, to: Float) {
    val ra = RotateAnimation(
        from,
        -to,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )

    ra.duration = 250

    ra.fillAfter = true

    this.startAnimation(ra)
}