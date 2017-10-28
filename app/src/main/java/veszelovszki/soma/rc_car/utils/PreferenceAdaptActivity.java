package veszelovszki.soma.rc_car.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.Locale;

import veszelovszki.soma.rc_car.DisplayEnvironmentActivity;
import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.SettingsActivity;
import veszelovszki.soma.rc_car.adapter.NavigationDrawerListAdapter;
import veszelovszki.soma.rc_car.fragment.DisplayEnvironmentFragment;

/**
 * Parent class for all activities in the project.
 * Adapts to preference changes (e.g. language).
 * Contains default settings and elements (e.g. default options menu).
 *
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016.07.21.
 */
public abstract class PreferenceAdaptActivity extends AppCompatActivity {

    private static final String TAG = PreferenceAdaptActivity.class.getCanonicalName();

    protected Boolean mTwoPaneMode;
    private Boolean mIsNavigationDrawerEnabled = false;

    private boolean mIsFloatingActionButtonEnabled = false;
    protected FloatingActionButton mFloatingActionButton;

    private DrawerLayout mNavigationDrawerLayout = null;
    private NavigationView mNavigationView = null;
    private ActionBarDrawerToggle mNavigationDrawerToggle;

    protected Menu mOptionsMenu;

    protected PrefManager mPrefManager;
    //protected DatabaseManager mDbManager;

    protected static Long APP_LANGUAGE_ID;
    public static final String EXTRA_LANGUAGE_ID = "language_id";
    protected static final String EXTRA_IS_RECREATED = "is_recreated";

    private Boolean mIsFirstStart;
    private Boolean mIsRecreated;

    public enum State {
        CREATED, RESUMED, PAUSED
    };

    private State mState;

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

    protected Boolean isRecreated(){
        return mIsRecreated;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mDbManager = DatabaseManager.newInstance(this);
        mPrefManager = new PrefManager(this);

        super.onCreate(savedInstanceState);

        mState = State.CREATED;

        mTwoPaneMode = false;//getResources().getBoolean(R.bool.two_pane_mode);
        Log.d(TAG, (mTwoPaneMode ? "2" : "1") + "-pane mode");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mState = State.RESUMED;

        Intent intent = getIntent();
        if (intent != null) {
            mIsRecreated = intent.getBooleanExtra(EXTRA_IS_RECREATED, false);
            intent.putExtra(EXTRA_IS_RECREATED, false);
        }

        updateFloatingActionButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mIsNavigationDrawerEnabled && mNavigationDrawerToggle.onOptionsItemSelected(item)) return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_show_car_environment:
                startActivity(DisplayEnvironmentActivity.class);
                return true;

            case R.id.action_settings:
                startActivity(SettingsActivity.class);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        if (mIsNavigationDrawerEnabled)
            initializeNavigationDrawer();

//        if (mIsFloatingActionButtonEnabled)
//            initializeFloatingActionButton();
        checkForFirstRun();

    }

    private void initializeFloatingActionButton() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.floating_action_button, getRootView());
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
    }

    protected ViewGroup getRootView() {
        return (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void handleIntent(Intent intent){}

    @Override
    protected void onPause() {
        mState = State.PAUSED;
        super.onPause();
    }

    public void setNavigationDrawerEnabled(Boolean isNavigationDrawerEnabled) {
        mIsNavigationDrawerEnabled = isNavigationDrawerEnabled;
        updateNavigationDrawer();
    }

    public Boolean isNavigationDrawerEnabled() {
        return mIsNavigationDrawerEnabled;
    }

    public void setFloatingActionButtonEnabled(Boolean isFloatingActionButtonEnabled) {
        mIsFloatingActionButtonEnabled = isFloatingActionButtonEnabled;
        updateFloatingActionButton();
    }

    public boolean isFloatingActionButtonEnabled() {
        return mIsFloatingActionButtonEnabled;
    }

    private void updateFloatingActionButton() {
        if (mFloatingActionButton != null) {
            mFloatingActionButton.setVisibility(mIsFloatingActionButtonEnabled ? View.VISIBLE : View.GONE);
        } else if (mIsFloatingActionButtonEnabled)
            initializeFloatingActionButton();
    }

    private void initializeNavigationDrawer() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNavigationDrawerLayout = (DrawerLayout) inflater.inflate(R.layout.navigation_drawer, null);

        mNavigationView = (NavigationView) mNavigationDrawerLayout.findViewById(R.id.navigation_view);
        mNavigationView.setItemIconTintList(null);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                closeDrawer();
                return onOptionsItemSelected(item);
            }
        });

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mNavigationDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mNavigationDrawerLayout,         /* DrawerLayout object */
                R.string.action_open_drawer,  /* "open drawer" description for accessibility */
                R.string.action_close_drawer  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mNavigationDrawerLayout.addDrawerListener(mNavigationDrawerToggle);

        if (mIsNavigationDrawerEnabled) {
            addNavigationDrawerToView();
            updateNavigationDrawer();

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    private void addNavigationDrawerToView() {
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();

        // HACK: "steal" the first child of decor view
        View child = decor.getChildAt(0);
        decor.removeView(child);
        FrameLayout container = (FrameLayout) mNavigationDrawerLayout.findViewById(R.id.content_frame);
        container.addView(child, 0);
        mNavigationDrawerLayout.findViewById(R.id.drawer_layout).setPadding(0, getStatusBarHeight(), 0, 0);
        decor.addView(mNavigationDrawerLayout);
    }

    private void updateNavigationDrawer() {

        if (mNavigationDrawerLayout != null && mNavigationDrawerToggle != null) {
            // drawer has been initialized, updates properties
            mNavigationDrawerLayout.setVisibility(mIsNavigationDrawerEnabled ? View.VISIBLE : View.GONE);
            mNavigationDrawerLayout.setDrawerLockMode(mIsNavigationDrawerEnabled ?
                    DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            mNavigationDrawerToggle.onDrawerStateChanged(mIsNavigationDrawerEnabled ?
                    DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mNavigationDrawerToggle.setDrawerIndicatorEnabled(mIsNavigationDrawerEnabled);
            mNavigationDrawerToggle.syncState();
        } else if (mIsNavigationDrawerEnabled)
            initializeNavigationDrawer();
    }

    public void closeDrawer() {
        if (isNavigationDrawerEnabled())
            mNavigationDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public Boolean isFirstStart() {
        return mIsFirstStart;
    }

    /**
     * Checks if activity is running for the first time, and - if yes - calls method onFirstStart().
     */
    protected void checkForFirstRun() {

        PrefManager.PREFERENCE firstStartPreference = this.getFirstStartPreference();

        // reads value from preferences
        mIsFirstStart = (Boolean) mPrefManager.readPref(firstStartPreference);
        if (mIsFirstStart) {
            // write false value to preferences
            // -> from now on it is not the first time for the activity to run
            mPrefManager.writePref(firstStartPreference, false);
        }
    }

    public abstract PrefManager.PREFERENCE getFirstStartPreference();

    /**
     * Sets locale from preferences.
     */
    protected void setLocaleFromPrefs() {

        // gets app language iso code from preferences and sets it as locale
        setLocale(getAppLanguageIsoCodeFromPrefs());
    }

    /**
     * Gets app language from preferences - returns default value if preference is not present.
     * @return language iso code
     */
    public String getAppLanguageIsoCodeFromPrefs() {
        return (String)mPrefManager.readPref(PrefManager.PREFERENCE.APP_LANGUAGE);
    }

    /**
     * Sets locale.
     * @param iso639_1_Code language iso-639-1 code
     */
    protected void setLocale(String iso639_1_Code) {
        Locale locale = new Locale(iso639_1_Code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    protected View getContentView() {
        return findViewById(android.R.id.content);
    }

    /**
     * Starts new activity or (if new activity is the same as current) handles extras.
     * @param newActivityClass
     * @param <T>
     */
    public <T extends Activity> void startActivity(Class<T> newActivityClass) {
        startActivity(newActivityClass, null);
    }

    public <T extends Activity> void startActivity(Class<T> newActivityClass, Bundle extras) {

        this.closeDrawer();

        Intent intent;

        // if new activity class is the same as the current, recreates activity
        if (this.getClass().equals(newActivityClass)) {

            if (extras != null) {
                intent = this.getIntent();
                if (intent != null) {
                    if (intent.getExtras() != null)
                        intent.getExtras().clear();

                    intent.putExtras(extras);

                    this.handleIntent(intent);
                }
            }

        } else {
            intent = new Intent(this, newActivityClass);
            this.startActivity(intent);
        }
    }

    public <T extends Activity> void startActivityAndFinish(Class<T> newActivityClass) {
        startActivity(newActivityClass);
        this.finish();
    }

    public <T extends Activity> void startActivityAndFinish(Class<T> newActivityClass, Bundle extras) {
        startActivity(newActivityClass, extras);
        this.finish();
    }

    protected void onError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getContentView(), R.string.error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    protected void onError(final Throwable e) {
        e.printStackTrace();
        onError();
    }
}