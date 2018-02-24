package veszelovszki.soma.rc_car.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Pointf;
import veszelovszki.soma.rc_car.view.RelativeEnvironmentView;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.10.25.
 */

public class DisplayEnvironmentFragment extends Fragment {
    public static final String TAG = DisplayEnvironmentFragment.class.getCanonicalName();

    private RelativeEnvironmentView mRelativeEnvironmentView;

    public static DisplayEnvironmentFragment newInstance() {
        return new DisplayEnvironmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_environment, container, false);
        mRelativeEnvironmentView = (RelativeEnvironmentView) view.findViewById(R.id.environment_view);

        return view;
    }

    public void updatePoint(int idx, Pointf point){
        mRelativeEnvironmentView.updatePoint(idx, point);
    }
}
