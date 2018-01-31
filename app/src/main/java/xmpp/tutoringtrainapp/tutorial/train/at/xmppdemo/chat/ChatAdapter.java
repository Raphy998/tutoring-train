package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat;

import android.app.Activity;
import android.content.Context;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private DataStore ds;
    private ChatAdapter adapter;
    private Activity activity;

    private DateFormat df = SimpleDateFormat.getTimeInstance();

    private class OnChatsChangedCallback extends ObservableList.OnListChangedCallback implements Runnable {
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

    public ChatAdapter(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.ds = DataStore.getInstance();
        this.adapter = this;

        this.ds.addOnChatsChangedCallback(new OnChatsChangedCallback());
    }

    @Override
    public int getCount() {
        return ds.getChatMessageCount();
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
        ChatMessage message = ds.getChatMessageAt(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.chatbubble, null);

        TextView msg = (TextView) vi.findViewById(R.id.message_text);
        msg.setText(message.getBody());
        TextView timestamp = (TextView) vi.findViewById(R.id.message_timestamp);
        timestamp.setText(df.format(message.getDateTime()));

        LinearLayout layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout_parent);

        // if message is mine then align to right
        if (message.isMine()) {
            layout.setBackgroundResource(R.drawable.bubble2);
            parent_layout.setGravity(Gravity.END);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.bubble1);
            parent_layout.setGravity(Gravity.START);
        }
        msg.setTextColor(Color.BLACK);
        return vi;
    }
}