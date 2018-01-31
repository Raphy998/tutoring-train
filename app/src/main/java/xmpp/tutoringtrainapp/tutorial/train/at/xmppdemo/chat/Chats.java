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
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XMPPHandler;


public class Chats extends Fragment implements OnClickListener {

    private EditText msg_edittext;
    private ImageButton sendButton;
    private ListView msgListView;

    private String myUser, otherUser;
    private Random random;
    private ChatAdapter chatAdapter;

    public void setUsers(String myUser, String otherUser) {
        this.myUser = myUser;

        if (this.otherUser == null || !this.otherUser.equals(otherUser)) {
            try {
                XMPPHandler.getInstance().loadArchivedMsgs(otherUser);
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
            myUser = savedInstanceState.getString("myUser");
            otherUser = savedInstanceState.getString("otherUser");
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
        msgListView.setAdapter(chatAdapter);
        return view;
    }

    private void getViews(View view) {
        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        sendButton = (ImageButton) view.findViewById(R.id.sendMessageButton);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("myUser", myUser);
        outState.putString("otherUser", otherUser);
        super.onSaveInstanceState(outState);
    }

    public void sendTextMessage() throws XmppStringprepException {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(myUser, otherUser,
                    message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.setBody(message);
            msg_edittext.setText("");

            XMPPHandler.getInstance().sendMessage(chatMessage);
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
            ex.printStackTrace();
            Toast.makeText(this.getContext(), ex.getMessage(), Toast.LENGTH_LONG);
        }
    }
}