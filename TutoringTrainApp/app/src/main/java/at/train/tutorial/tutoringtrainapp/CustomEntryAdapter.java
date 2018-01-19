package at.train.tutorial.tutoringtrainapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import at.train.tutorial.tutoringtrainapp.Data.Entry;

/**
 * Created by Moe on 16.11.2017
 */

    public class CustomEntryAdapter extends RecyclerView.Adapter<CustomEntryAdapter.CustomViewHolder> {

    ArrayList<Entry> entries;
    private Context context;

    public CustomEntryAdapter(ArrayList<Entry> entries, Context context) {
        this.entries = entries;
        this.context = context;
    }

    @Override
    public CustomEntryAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.txtHeadline.setText(entry.getHeadline());
        holder.txtUser.setText(entry.getUser().getUsername());
        holder.txtSubject.setText(entry.getSubject().getName());
        holder.txtDate.setText(formatDate(entry.getPostedOn()));
    }

    private String formatDate(Date date){
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return dateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {


        protected TextView txtHeadline;
        protected TextView txtUser;
        protected TextView txtSubject;
        protected TextView txtDate;


        public CustomViewHolder(View itemView) {
            super(itemView);
            txtHeadline = (TextView) itemView.findViewById(R.id.txt_headline);
            txtUser = (TextView) itemView.findViewById(R.id.txt_user);
            txtSubject = (TextView) itemView.findViewById(R.id.txt_subject);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);

        }
    }
}
