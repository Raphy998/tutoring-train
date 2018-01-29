package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.R;


public class Roster extends Fragment {

    public ArrayList<RosterEntry> roster;
    public RosterAdapter rosterAdapter;
    public ListView contactsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.roster_layout, container, false);
        contactsListView = (ListView) view.findViewById(R.id.rosterListView);

        roster = new ArrayList<>();
        rosterAdapter = new RosterAdapter(getActivity());
        contactsListView.setAdapter(rosterAdapter);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
}