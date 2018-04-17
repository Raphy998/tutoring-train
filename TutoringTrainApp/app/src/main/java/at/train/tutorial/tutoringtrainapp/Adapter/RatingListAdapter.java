package at.train.tutorial.tutoringtrainapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.List;

import at.train.tutorial.tutoringtrainapp.Data.Comment;
import at.train.tutorial.tutoringtrainapp.Data.Rating;
import at.train.tutorial.tutoringtrainapp.R;

public class RatingListAdapter extends ArrayAdapter<Rating> {

    public RatingListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public RatingListAdapter(Context context, int resource, List<Rating> ratings) {
        super(context, resource, ratings);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.rating_list_row, null);
        }

        Rating r = getItem(position);

        if (r != null) {
            TextView text = (TextView) v.findViewById(R.id.txt_txt);
            TextView rater = (TextView) v.findViewById(R.id.txt_rater);
            ImageView star1 = v.findViewById(R.id.star_1);
            ImageView star2 = v.findViewById(R.id.star_2);
            ImageView star3 = v.findViewById(R.id.star_3);
            ImageView star4 = v.findViewById(R.id.star_4);
            ImageView star5 = v.findViewById(R.id.star_5);

            text.setText(r.getText());
            rater.setText(r.getRatingUser().getUsername());

            int starCount = r.getStars();
            if (starCount > 1) {
                star2.setImageResource(R.drawable.ic_star_yellow_24dp);
            }
            if (starCount > 2) {
                star3.setImageResource(R.drawable.ic_star_yellow_24dp);
            }
            if (starCount > 3) {
                star4.setImageResource(R.drawable.ic_star_yellow_24dp);
            }
            if (starCount > 4) {
                star5.setImageResource(R.drawable.ic_star_yellow_24dp);
            }

        }
        return v;
    }
}
