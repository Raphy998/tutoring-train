package at.train.tutorial.tutoringtrainapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.R;

/**
 * Created by Moe on 16.11.2017
 */

    public class CustomUserAdapter extends RecyclerView.Adapter<CustomUserAdapter.CustomViewHolder> {

    ArrayList<User> users;
    private Context context;
    private View.OnClickListener listener;

    public CustomUserAdapter(ArrayList<User> users, Context context, View.OnClickListener listener) {
        this.users = users;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public CustomUserAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        v.setOnClickListener(listener);
        CustomViewHolder viewHolder = new CustomViewHolder(v);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        User user = users.get(position);
        holder.txtHeadline.setText(user.getUsername());
        holder.txtUser.setText(user.getName());
        holder.txtSubject.setText(user.getEducation());
    }


    @Override
    public int getItemCount() {
        return users.size();
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
