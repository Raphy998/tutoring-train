package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.FragmentInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.MessageChangeListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.RosterInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XmppHandler;


public class Roster extends Fragment implements RosterInteractionListener, AdapterView.OnItemClickListener, MessageChangeListener {

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

        rosterAdapter = new RosterAdapter(getActivity(), this, contactsListView);
        contactsListView.setAdapter(rosterAdapter);
        initEmptyMessage(view);

        contactsListView.setOnItemClickListener(this);
        return view;
    }

    private void initEmptyMessage(View view) {
        TextView emptyResults = (TextView) view.findViewById(R.id.emptyResults);
        emptyResults.setText(DataStore.getInstance().getMsgRoster());
        contactsListView.setEmptyView(emptyResults);

        DataStore.getInstance().addOnMsgRosterChangedCallback(this);
    }

    @Override
    public void onMessageUpdated(final String newMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView emptyView = (TextView) getActivity().findViewById(R.id.emptyResults);
                emptyView.setText(newMsg);
                contactsListView.setEmptyView(emptyView);
            }
        });
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
            throw new RuntimeException(context.toString() + " must implement FragmentInteractionListener");
        }
    }

    @Override
    public void removeFromRoster(Contact c) {
        try {
            XmppHandler.getInstance().removeFromRoster(c);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addToRoster(Contact c) {
        try {
            XmppHandler.getInstance().addToRoster(c);
            c.setType(Contact.Type.APPROVED);
            DataStore.getInstance().updateContact(c);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Contact c = (Contact) rosterAdapter.getItem(position);

            if (c.getType().equals(Contact.Type.APPROVED))
                listener.openChatWithUser(this, c);
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }
}