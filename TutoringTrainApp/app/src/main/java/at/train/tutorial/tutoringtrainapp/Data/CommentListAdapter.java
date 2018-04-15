package at.train.tutorial.tutoringtrainapp.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import at.train.tutorial.tutoringtrainapp.R;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    public CommentListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public CommentListAdapter (Context context, int resource, List<Comment> comments){
        super(context,resource,comments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if(v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.commen_list_row,null);
        }

        Comment c = getItem(position);

        if(c!= null){
            TextView commentText = (TextView) v.findViewById(R.id.txt_commentText);
            TextView commentUser = (TextView) v.findViewById(R.id.txt_commentUser);
            TextView commentDate = (TextView) v.findViewById(R.id.txt_commentDate);

            commentText.setText(c.getText());
            commentUser.setText(c.getUser().getUsername());

            //DateFormat dateFormat =
                    //android.text.format.DateFormat.getDateFormat(getContext());
            android.text.format.DateFormat df = new android.text.format.DateFormat();

            //commentDate.setText("Posted on: " + dateFormat.format(c.getPostedOn()));
            commentDate.setText("Posted on: " + df.format("dd.MM.yyyy", c.getPostedOn()) + " at "+ df.format("kk:mm:ss", c.getPostedOn()));
        }
        return v;
    }
}
