package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat;

import android.app.Activity;
import android.content.Context;
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

import at.train.tutorial.tutoringtrainapp.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private DataStore ds;
    private ChatAdapter adapter;
    private Activity activity;
    private String withUser;

    private DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.LONG);      // dd. MON. yyyy
    private DateFormat tf = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);     // hh:mm

    public ChatAdapter(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.ds = DataStore.getInstance();
        this.adapter = this;

        this.ds.addOnChatsChangedObserver(this);
    }

    public String getWithUser() {
        return withUser;
    }

    public void setWithUser(String withUser) {
        this.withUser = withUser;
    }

    @Override
    public int getCount() {
        return ds.getChatMessageCount(withUser);
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
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.chatbubble, null);
            holder.msg = (TextView) convertView.findViewById(R.id.message_text);
            holder.timestamp = (TextView) convertView.findViewById(R.id.message_timestamp);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.bubble_layout);
            holder.parent_layout = (LinearLayout) convertView.findViewById(R.id.bubble_layout_parent);
            holder.sec_hr = (TextView) convertView.findViewById(R.id.sec_header);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatMessage message = ds.getChatMessageAt(position, withUser);

        holder.msg.setText(message.getBody());
        holder.timestamp.setText(tf.format(message.getDateTime()));

        // if message is mine then align to right
        if (message.isMine()) {
            holder.layout.setBackgroundResource(R.drawable.bubble2);
            holder.parent_layout.setGravity(Gravity.END);
        }
        // If not mine then align to left
        else {
            holder.layout.setBackgroundResource(R.drawable.bubble1);
            holder.parent_layout.setGravity(Gravity.START);
        }
        holder.msg.setTextColor(Color.BLACK);


        holder.sec_hr.setText(df.format(message.getDateTime()));
        holder.sec_hr.setVisibility(View.VISIBLE);

        try {
            if (position - 1 >= 0) {
                ChatMessage prevMessage = ds.getChatMessageAt(position - 1, withUser);
                if (df.format(message.getDateTime()).equals(df.format(prevMessage.getDateTime()))) {
                    holder.sec_hr.setVisibility(View.GONE);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder {
        TextView msg;
        TextView timestamp;
        LinearLayout layout;
        LinearLayout parent_layout;
        TextView sec_hr;
    }
}