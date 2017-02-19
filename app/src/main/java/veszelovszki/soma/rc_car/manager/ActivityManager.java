package veszelovszki.soma.rc_car.manager;

import android.app.Activity;

import java.util.ArrayList;

import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;

/**
 * Manages activities throughout the lifetime of the application.
 * This is a SINGLETON class.
 * Responsible for storing active (not destroyed) activities with their current states
 * (e.g. preference events that they have not handled yet)
 *
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016.07.21.
 */
public final class ActivityManager {

    private static final String TAG = ActivityManager.class.getCanonicalName();

    /**
     * PrefManager event types.
     * A preference event is an event indicating a change in the default shared preferences.
     */
    public enum PREF_EVENT {
        LANGUAGE_CHANGED
    }

    public interface PreferenceEventListener {
        void onPreferenceEvent(PREF_EVENT event);

        void onFirstRun();
    }

    private static ActivityManager instance;

    // list of ACTIVE activity states -> when an activity is destroyed it is removed from the list
    private ActivityStateList activityStateList = new ActivityStateList();

    public synchronized static ActivityManager getInstance(){
        if (instance == null) {
            instance = new ActivityManager();
        }

        return instance;
    }

    /**
     * Notifies all active activities about a preference event.
     *
     * @param event preference event
     */
    public void notifyAllActivities(PREF_EVENT event) {
        for (ActivityState state : activityStateList) {
            state.notifyActivity(event);
        }
    }

    public void add(PreferenceAdaptActivity activity) {
        if (!activityStateList.contains(activity)) {
            this.activityStateList.add(new ActivityState(activity));
        }
    }

    public void remove(PreferenceAdaptActivity activity) {
        this.activityStateList.remove(activity);
    }

    /**
     * List of activity states.
     * List functions (add, remove, indexOf) by activity.
     */
    private final class ActivityStateList extends ArrayList<ActivityState> {

        public Boolean contains(PreferenceAdaptActivity activity) {
            for (ActivityState state : this) {
                if (state.getActivity().equals(activity)) {
                    return true;
                }
            }
            return false;
        }

        public ActivityState get(PreferenceAdaptActivity activity) {
            for (ActivityState state : this) {
                if (state.getActivity().equals(activity)) {
                    return state;
                }
            }
            return null;
        }

        public Boolean remove(PreferenceAdaptActivity activity) {
            for (ActivityState state : this) {
                if (state.getActivity().equals(activity)) {
                    return super.remove(state);
                }
            }
            return false;
        }

    }

    /**
     * Contains activity state.
     */
    private final class ActivityState {

        private PreferenceAdaptActivity activity;

        public ActivityState(PreferenceAdaptActivity activity) {
            this.activity = activity;
        }

        public Activity getActivity() {
            return this.activity;
        }

        public void notifyActivity(PREF_EVENT event) {
            activity.onPreferenceEvent(event);
        }
    }
}
