package veszelovszki.soma.rc_car.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Pointf;
import veszelovszki.soma.rc_car.view.AccelerationSeekBar;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 18.
 */

public class GyroscopeControlFragment extends ControlFragment {

    public static final String TAG = GyroscopeControlFragment.class.getCanonicalName();

    private AccelerationSeekBar mAccelerationSeekBar;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    public SensorEventListener mGyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {}

        public void onSensorChanged(SensorEvent event) {
            Float x = event.values[0];
            Float y = event.values[1];
            Float z = event.values[2];

            //TODO decide which one is needed
        }
    };

    public static GyroscopeControlFragment newInstance() {
        return new GyroscopeControlFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_steering_wheel, container, false);

        mAccelerationSeekBar = (AccelerationSeekBar) view.findViewById(R.id.accelerator_seek_bar);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mGyroListener, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(mGyroListener);
    }

    @Override
    public float getSpeed() {
        return mAccelerationSeekBar.getProgress();
    }

    @Override
    public float getSteeringAngle() {
        //TODO
        return 0.0f;
    }

    @Override
    public void setCarEnvironmentEnabled(Boolean enabled) {
        // TODO
    }

    @Override
    public Boolean isCarEnvironmentEnabled() {
        return null; // TODO
    }

    @Override
    public void updateCarEnvironmentPoint(int idx, Pointf point) {
        // TODO
    }
}
