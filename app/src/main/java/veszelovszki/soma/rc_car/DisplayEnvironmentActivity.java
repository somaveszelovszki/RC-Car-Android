package veszelovszki.soma.rc_car;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import veszelovszki.soma.rc_car.fragment.DisplayEnvironmentFragment;
import veszelovszki.soma.rc_car.utils.PrefManager;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;

public class DisplayEnvironmentActivity extends PreferenceAdaptActivity {

    DisplayEnvironmentFragment mDisplayEnvironmentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_environment);

        if (savedInstanceState != null) {
            mDisplayEnvironmentFragment = (DisplayEnvironmentFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, DisplayEnvironmentFragment.TAG);
        } else {
            mDisplayEnvironmentFragment = DisplayEnvironmentFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mDisplayEnvironmentFragment, DisplayEnvironmentFragment.TAG).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.car_environment);
        }
    }

    @Override
    public PrefManager.PREFERENCE getFirstStartPreference() {
        return PrefManager.PREFERENCE.FIRST_START_DISPLAY_ENVIRONMENT;
    }
}
