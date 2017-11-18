package veszelovszki.soma.rc_car.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import veszelovszki.soma.rc_car.utils.Pointf;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 18.
 */

public abstract class ControlFragment extends Fragment {

    public static final String TAG = ControlFragment.class.getCanonicalName();

    // Container activity must implement this interface
    public interface EventListener {
        void onCarEnvironmentEnabled();
        void onCarEnvironmentDisabled();
    }

    protected EventListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container context has implemented
        // the listener interface. If not, it throws an exception
        try {
            mListener = (EventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EventListener");
        }
    }

    public abstract float getSpeed();

    public abstract float getSteeringAngle();

    public abstract void setCarEnvironmentEnabled(Boolean enabled);

    public abstract Boolean isCarEnvironmentEnabled();

    public abstract void updateCarEnvironmentPoint(int idx, Pointf point);

}
