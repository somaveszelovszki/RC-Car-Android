package veszelovszki.soma.rc_car.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import veszelovszki.soma.rc_car.R;
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

        mEnvironmentView = (EnvironmentView) view.findViewById(R.id.accelerator_seek_bar);

        return view;
    }
}
