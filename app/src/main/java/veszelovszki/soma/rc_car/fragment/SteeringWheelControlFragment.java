package veszelovszki.soma.rc_car.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.view.AccelerationSeekBar;
import veszelovszki.soma.rc_car.view.SteeringWheelView;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 11. 13.
 */
public class SteeringWheelControlFragment extends ControlFragment {

    public static final String TAG = SteeringWheelControlFragment.class.getCanonicalName();

    private SteeringWheelView mSteeringWheelView;
    private AccelerationSeekBar mAccelerationSeekBar;

    public static SteeringWheelControlFragment newInstance() {
        return new SteeringWheelControlFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_steering_wheel, container, false);

        mSteeringWheelView = (SteeringWheelView) view.findViewById(R.id.steering_wheel_view);
        mAccelerationSeekBar = (AccelerationSeekBar) view.findViewById(R.id.accelerator_seek_bar);

        return view;
    }

    @Override
    public Integer getSpeed() {
        return mAccelerationSeekBar.getProgress();
    }

    @Override
    public Integer getSteeringAngle() {
        return mSteeringWheelView.getWheelRotation().intValue();
    }
}

