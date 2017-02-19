package veszelovszki.soma.rc_car.fragment;

import android.app.Fragment;
import android.content.Context;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 18.
 */

public abstract class ControlFragment extends Fragment {

    public static final String TAG = ControlFragment.class.getCanonicalName();

    // Container activity must implement this interface
    public interface ControlFragmentListener {
    }

    protected ControlFragmentListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container context has implemented
        // the listener interface. If not, it throws an exception
        try {
            mListener = (ControlFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ControlFragmentListener");
        }
    }

    public abstract Integer getSpeed();

    public abstract Integer getSteeringAngle();
}