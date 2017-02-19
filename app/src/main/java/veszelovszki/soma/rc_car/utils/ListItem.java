package veszelovszki.soma.rc_car.utils;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class ListItem {
    private Integer mId;
    private String mTitle;

    public ListItem(){}

    public ListItem(Integer id, String title) {
        mId = id;
        mTitle = title;
    }

    public Integer getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
