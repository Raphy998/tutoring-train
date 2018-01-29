package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster;

import android.app.Activity;
import android.content.Context;
import android.databinding.ObservableList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;

public class RosterAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private DataStore ds;
    private RosterAdapter adapter;
    private Activity activity;

    private class OnRosterChangedCallback extends ObservableList.OnListChangedCallback implements Runnable {
        @Override
        public void onChanged(ObservableList observableList) {
            activity.runOnUiThread(this);
        }

        @Override
        public void onItemRangeChanged(ObservableList observableList, int i, int i1) {
            activity.runOnUiThread(this);
        }

        @Override
        public void onItemRangeInserted(ObservableList observableList, int i, int i1) {
            activity.runOnUiThread(this);
        }

        @Override
        public void onItemRangeMoved(ObservableList observableList, int i, int i1, int i2) {
            activity.runOnUiThread(this);
        }

        @Override
        public void onItemRangeRemoved(ObservableList observableList, int i, int i1) {
            activity.runOnUiThread(this);
        }

        @Override
        public void run() {
            adapter.notifyDataSetChanged();
        }
    }

    public RosterAdapter(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.ds = DataStore.getInstance();
        this.adapter = this;

        this.ds.getRoster().addOnListChangedCallback(new OnRosterChangedCallback());
    }

    @Override
    public int getCount() {
        return ds.getRoster().size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact c = ds.getRoster().get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.roster_list_item, null);

        TextView txt = (TextView) vi.findViewById(R.id.name);
        txt.setText(c.getFullName());
        TextView type = (TextView) vi.findViewById(R.id.type);
        type.setText(c.getType().name());

        return vi;
    }
}