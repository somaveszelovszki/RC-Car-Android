package veszelovszki.soma.rc_car.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.Collection;

import veszelovszki.soma.rc_car.adapter.DeviceListAdapter;
import veszelovszki.soma.rc_car.R;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 07.
 */

public class DeviceListFragment extends ListFragment {

    public static final String TAG = DeviceListFragment.class.getCanonicalName();

    // Container activity must implement this interface
    public interface DeviceListFragmentListener {
        void searchDevices();

        void onDeviceSelected(BluetoothDevice device);
    }

    DeviceListFragmentListener mListener;

    Button mSearchDevicesButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        mSearchDevicesButton = (Button) view.findViewById(R.id.search_devices_button);

        mSearchDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.searchDevices();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        this.setListAdapter(new DeviceListAdapter(getContext()));

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mListener.onDeviceSelected(getListAdapter().getItem(position));
            }
        });
    }


    @Override
    public DeviceListAdapter getListAdapter() {
        return (DeviceListAdapter) super.getListAdapter();
    }

    public void setList(Collection<BluetoothDevice> devices) {

        DeviceListAdapter adapter = this.getListAdapter();
        adapter.clear();
        adapter.addAll(devices);
        adapter.notifyDataSetChanged();
    }

    public static DeviceListFragment newInstance() {
        return new DeviceListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container context has implemented
        // the listener interface. If not, it throws an exception
        try {
            mListener = (DeviceListFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DeviceListFragmentListener");
        }
    }
}
