package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Random;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XmppHandler;


public class Chats extends Fragment implements OnClickListener {

    private EditText msg_edittext;
    private ImageButton sendButton;
    private ListView msgListView;

    private Contact myUser, otherUser;
    private Random random;
    private ChatAdapter chatAdapter;

    public void setUsers(Contact myUser, Contact otherUser) {
        this.myUser = myUser;

        if (this.otherUser == null || !this.otherUser.equals(otherUser)) {
            try {
                chatAdapter.setWithUser(otherUser.getUsername());
                XmppHandler.setChatCreated(false);
                if (!DataStore.getInstance().isChatLoaded(otherUser.getUsername())) {
                    XmppHandler.getInstance().loadArchivedMsgs(otherUser.getUsername(),
                            DataStore.getInstance().getLastQueryResult());
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.otherUser = otherUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            myUser = new Contact(savedInstanceState.getString("myUser"), savedInstanceState.getString("myUserName"));
            otherUser = new Contact(savedInstanceState.getString("otherUser"), savedInstanceState.getString("otherUserName"));
        }

        random = new Random();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_layout, container, false);
        getViews(view);

        sendButton.setOnClickListener(this);

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        chatAdapter = new ChatAdapter(getActivity());
        chatAdapter.setWithUser(otherUser != null ? otherUser.getUsername() : null);
        msgListView.setAdapter(chatAdapter);

        return view;
    }

    private boolean listIsAtTop()   {
        if(msgListView.getChildCount() == 0) return true;
        return msgListView.getChildAt(0).getTop() == 0;
    }

    private void getViews(View view) {
        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        sendButton = (ImageButton) view.findViewById(R.id.sendMessageButton);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("myUser", myUser != null ? myUser.getUsername() : null);
        outState.putString("myUserName", myUser != null ? myUser.getFullName() : null);
        outState.putString("otherUser", otherUser != null ? otherUser.getUsername() : null);
        outState.putString("otherUserName", otherUser != null ? otherUser.getFullName() : null);
        super.onSaveInstanceState(outState);
    }

    public void sendTextMessage() throws XmppStringprepException {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(myUser.getUsername(), otherUser.getUsername(),
                    message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.setBody(message);
            msg_edittext.setText("");

            XmppHandler.getInstance().sendMessage(chatMessage);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.sendMessageButton:
                    sendTextMessage();

            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    public Contact getWith() {
        return this.otherUser;
    }
}