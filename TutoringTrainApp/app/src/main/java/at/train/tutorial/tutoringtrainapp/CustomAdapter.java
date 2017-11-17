package at.train.tutorial.tutoringtrainapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Moe on 16.11.2017
 */

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        ArrayList<String> list;

        public CustomAdapter(ArrayList<String> planetList, Context context) {
            this.list = planetList;
        }

        @Override
        public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row,parent,false);
            CustomViewHolder viewHolder=new CustomViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
           // holder.image.setImageResource(R.drawable.ic_list_black_24dp);
           // holder.txtHeadline.setText(list.get(position));
            holder.txtHeadline.setText("Angebot " + position);
            holder.txtUser.setText("admin ");
            holder.txtDesc.setText("Beschreibung für das angebot/Nachfrage... ");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class CustomViewHolder extends RecyclerView.ViewHolder{


            protected TextView txtHeadline;
            protected TextView txtUser;
            protected TextView txtDesc;

            public CustomViewHolder(View itemView) {
                super(itemView);
                txtHeadline = (TextView) itemView.findViewById(R.id.txt_headline);
                txtUser = (TextView) itemView.findViewById(R.id.txt_user);
                txtDesc = (TextView) itemView.findViewById(R.id.txt_description);
            }
        }
}
