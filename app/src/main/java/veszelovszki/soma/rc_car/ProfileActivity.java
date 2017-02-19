package veszelovszki.soma.rc_car;

import android.os.Bundle;

import veszelovszki.soma.rc_car.utils.PrefManager;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;

public class ProfileActivity extends PreferenceAdaptActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    /**
     * Checks for permissions - called during onCreate().
     */
    protected void checkPermissions() {

    }

    /**
     * Method is called when activity runs for the first time - must be overwritten by every descendant.
     */
    public void onFirstRun(){

    }

    public PrefManager.PREFERENCE getFirstRunPreference() {
        return PrefManager.PREFERENCE.FIRST_START_PROFILE;
    }
}
