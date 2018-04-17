package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster;

import android.app.Activity;
import android.content.Context;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

import at.train.tutorial.tutoringtrainapp.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.RosterInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;

public class RosterAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private DataStore ds;
    private RosterAdapter adapter;
    private Activity activity;
    private RosterInteractionListener listener;
    private ListView lv;

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

    public RosterAdapter(Activity activity, RosterInteractionListener listener, ListView lv) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.listener = listener;
        this.ds = DataStore.getInstance();
        this.adapter = this;
        this.lv = lv;

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

        switch (c.getType()) {
            case APPROVED:
                vi = getViewApproved(c);
                break;
            case REQUESTED_BY_OTHER:
                vi = getViewRequestedByOther(c);
                break;
            case REQUESTED_BY_ME:
                vi = getViewRequestedByMe(c);
                break;
            default:
                vi = getViewApproved(c);
        }

        return vi;
    }

    private Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap b = BitmapFactory.decodeStream(arrayInputStream);
        return Bitmap.createScaledBitmap(b, 160, 160, false);
    }

    private View getViewApproved(final Contact c) {
        View vi = inflater.inflate(R.layout.roster_list_item_approved, null);
        ((ViewGroup) vi).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        TextView txtName = (TextView) vi.findViewById(R.id.name);
        TextView txtUsername = (TextView) vi.findViewById(R.id.username);
        TextView txtNumNewMsgs = (TextView) vi.findViewById(R.id.numNewMsgs);
        ImageView imgAvatar = (ImageView) vi.findViewById(R.id.imgAvatar);

        if (c.getAvatar() != null) {
            imgAvatar.setImageBitmap(convertByteArrayToBitmap(c.getAvatar()));
        }
        txtName.setText(c.getFullName());
        txtUsername.setText("@" + c.getUsername());
        txtNumNewMsgs.setText(Integer.toString(c.getCountNewMsgs()));



        if (c.getCountNewMsgs() > 0) {
            txtNumNewMsgs.setVisibility(View.VISIBLE);
        }
        else {
            txtNumNewMsgs.setVisibility(View.INVISIBLE);
        }

        ImageButton btnRemove = (ImageButton) vi.findViewById(R.id.btnRemoveContact);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.removeFromRoster(c);
            }
        });

        return vi;
    }

    private View getViewRequestedByOther(final Contact c) {
        View vi = inflater.inflate(R.layout.roster_list_item_requested_by_other, null);
        ((ViewGroup) vi).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        TextView txtName = (TextView) vi.findViewById(R.id.name);
        TextView txtUsername = (TextView) vi.findViewById(R.id.username);
        ImageView imgAvatar = (ImageView) vi.findViewById(R.id.imgAvatar);

        if (c.getAvatar() != null) {
            imgAvatar.setImageBitmap(convertByteArrayToBitmap(c.getAvatar()));
        }
        txtName.setText(c.getFullName());
        txtUsername.setText("@" + c.getUsername());

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

        return vi;
    }

    private View getViewRequestedByMe(final Contact c) {
        View vi = inflater.inflate(R.layout.roster_list_item_requested_by_me, null);
        ((ViewGroup) vi).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        TextView txtName = (TextView) vi.findViewById(R.id.name);
        TextView txtUsername = (TextView) vi.findViewById(R.id.username);
        ImageView imgAvatar = (ImageView) vi.findViewById(R.id.imgAvatar);

        if (c.getAvatar() != null) {
            imgAvatar.setImageBitmap(convertByteArrayToBitmap(c.getAvatar()));
        }
        txtName.setText(c.getFullName());
        txtUsername.setText("@" + c.getUsername());

        ImageButton btnRemove = (ImageButton) vi.findViewById(R.id.btnRemoveContact);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.removeFromRoster(c);
            }
        });

        return vi;
    }
}