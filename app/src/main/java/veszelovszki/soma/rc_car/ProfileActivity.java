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

    @Override
    public PrefManager.PREFERENCE getFirstStartPreference() {
        return PrefManager.PREFERENCE.FIRST_START_PROFILE;
    }
}
