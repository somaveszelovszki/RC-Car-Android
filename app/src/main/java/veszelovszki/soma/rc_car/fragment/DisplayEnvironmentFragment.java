package veszelovszki.soma.rc_car.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Pointf;
import veszelovszki.soma.rc_car.view.EnvironmentView;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.10.25.
 */

public class DisplayEnvironmentFragment extends Fragment {
    public static final String TAG = DisplayEnvironmentFragment.class.getCanonicalName();

    private EnvironmentView mEnvironmentView;

    public static DisplayEnvironmentFragment newInstance() {
        return new DisplayEnvironmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_environment, container, false);
        mEnvironmentView = (EnvironmentView) view.findViewById(R.id.environment_view);

        testInit();

        return view;
    }

    private void testInit() {
        List<Pointf> points = new ArrayList<>();
        points.add(new Pointf(60.0f, 40.0f));
        points.add(new Pointf(45.0f, 30.0f));

        points.add(new Pointf(-30.0f, 50.0f));
        points.add(new Pointf(-60.0f, 70.0f));

        points.add(new Pointf(-30.0f, -90.0f));
        points.add(new Pointf(-10.0f, -90.0f));

        points.add(new Pointf(30.0f, -75.0f));
        points.add(new Pointf(70.0f, -65.0f));

        mEnvironmentView.updatePoints(points);
    }

    public void updatePoint(int idx, Pointf point){
        mEnvironmentView.updatePoint(idx, point);
    }
}
