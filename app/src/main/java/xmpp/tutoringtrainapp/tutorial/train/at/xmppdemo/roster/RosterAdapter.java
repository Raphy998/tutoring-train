package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster;

import android.app.Activity;
import android.content.Context;
import android.databinding.ObservableList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.RosterInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;

public class RosterAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private DataStore ds;
    private RosterAdapter adapter;
    private Activity activity;
    private RosterInteractionListener listener;

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

    public RosterAdapter(Activity activity, RosterInteractionListener listener) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.listener = listener;
        this.ds = DataStore.getInstance();
        this.adapter = this;

        this.ds.addOnRosterChangedCallback(new OnRosterChangedCallback());
    }

    @Override
    public int getCount() {
        return ds.getContactCount();
    }

    @Override
    public Object getItem(int position) {
        return ds.getContactAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Contact c = ds.getContactAt(position);
        View vi;

        if (c.getType().equals(Contact.Type.APPROVED)) {
            vi = inflater.inflate(R.layout.roster_list_item_approved, null);
            ((ViewGroup) vi).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

            TextView txtName = (TextView) vi.findViewById(R.id.name);
            TextView txtUsername = (TextView) vi.findViewById(R.id.username);
            txtName.setText(c.getFullName());
            txtUsername.setText(c.getUsername());

            ImageButton btnRemove = (ImageButton) vi.findViewById(R.id.btnRemoveContact);
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.removeFromRoster(c);
                }
            });
        }
        else {
            vi = inflater.inflate(R.layout.roster_list_item_requested, null);
            ((ViewGroup) vi).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

            TextView txtName = (TextView) vi.findViewById(R.id.name);
            TextView txtUsername = (TextView) vi.findViewById(R.id.username);
            txtName.setText(c.getFullName());
            txtUsername.setText(c.getUsername());

            ImageButton btnRemove = (ImageButton) vi.findViewById(R.id.btnRemoveContact);
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.removeFromRoster(c);
                }
            });

            ImageButton btnAdd = (ImageButton) vi.findViewById(R.id.btnAddContact);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.addToRoster(c);
                }
            });
        }

        return vi;
    }
}