package veszelovszki.soma.rc_car.utils;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 10.
 */

public class NavigationDrawerListItem extends ListItem {

    private Class<? extends PreferenceAdaptActivity> mCallbackClass;

    public NavigationDrawerListItem(){
        super();
    }

    public NavigationDrawerListItem(Integer id, String title, Class<? extends PreferenceAdaptActivity> callbackClass) {
        super(id, title);
        mCallbackClass = callbackClass;
    }

    public void setCallbackClass(Class<? extends PreferenceAdaptActivity> callbackClass) {
        mCallbackClass = callbackClass;
    }

    public Class<? extends PreferenceAdaptActivity> getCallbackClass() {
        return mCallbackClass;
    }
}
