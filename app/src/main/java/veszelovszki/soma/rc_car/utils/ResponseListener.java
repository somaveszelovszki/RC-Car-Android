package veszelovszki.soma.rc_car.utils;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 07.
 */

public abstract class ResponseListener<T> {
    public abstract void onResponse(T response);
    public abstract void onError(Exception e);
}
