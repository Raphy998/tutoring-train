package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.FragmentInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.RosterInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XMPPHandler;


public class Roster extends Fragment implements RosterInteractionListener, AdapterView.OnItemClickListener {

    private RosterAdapter rosterAdapter;
    private ListView contactsListView;
    private FragmentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.roster_layout, container, false);
        contactsListView = (ListView) view.findViewById(R.id.rosterListView);

        rosterAdapter = new RosterAdapter(getActivity(), this);
        contactsListView.setAdapter(rosterAdapter);
        contactsListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentInteractionListener");
        }
    }

    @Override
    public void removeFromRoster(Contact c) {
        try {
            System.out.println("------ REMOVE FROM ROSTER: " + c);
            XMPPHandler.getInstance().removeFromRoster(c);
            DataStore.getInstance().removeContact(c);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addToRoster(Contact c) {
        try {
            System.out.println("------ ADD TO ROSTER: " + c);
            XMPPHandler.getInstance().addToRoster(c);
            c.setType(Contact.Type.APPROVED);
            DataStore.getInstance().updateContact(c);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact c = (Contact) rosterAdapter.getItem(position);

        if (c.getType().equals(Contact.Type.APPROVED))
            listener.openChatWithUser(this, c.getUsername());
    }
}