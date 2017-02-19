package veszelovszki.soma.rc_car.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import veszelovszki.soma.rc_car.R;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 07.
 */
public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private static Integer RESOURCE_ID = R.layout.list_item;

    public DeviceListAdapter(Context context) {
        super(context, RESOURCE_ID);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(RESOURCE_ID, null);
        }

        BluetoothDevice item = getItem(position);

        if (item != null) {
            TextView titleTextView = (TextView) view.findViewById(R.id.list_item_title);

            if (titleTextView != null) {

                titleTextView.setText(item.getName());
            }
        }

        return view;
    }
}