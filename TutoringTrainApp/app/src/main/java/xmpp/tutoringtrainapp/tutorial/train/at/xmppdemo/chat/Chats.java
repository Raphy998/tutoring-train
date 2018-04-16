package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Random;

import at.train.tutorial.tutoringtrainapp.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.ChatHistoryLoadedListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XmppHandler;


public class Chats extends Fragment implements OnClickListener, ChatHistoryLoadedListener {

    private EditText msg_edittext;
    private ImageButton sendButton;
    private ListView msgListView;
    private ProgressBar progressBar;
    private TextView emptyView;

    private Contact myUser, otherUser;
    private Random random;
    private ChatAdapter chatAdapter;

    public void setUsers(Contact myUser, final Contact otherUser) {
        this.myUser = myUser;

        if (this.otherUser == null || !this.otherUser.equals(otherUser)) {
            try {
                chatAdapter.setWithUser(otherUser.getUsername());
                XmppHandler.setChatCreated(false);

                //Show or hide progress spinner for loading msgs
                if (DataStore.getInstance().isChatHistoryLoaded(otherUser.getUsername()))
                    progressBar.setVisibility(View.GONE);
                else
                    progressBar.setVisibility(View.VISIBLE);

                if (!DataStore.getInstance().isChatHistoryLoaded(otherUser.getUsername())) {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                XmppHandler.getInstance().loadArchivedMsgs(otherUser.getUsername(),
                                        DataStore.getInstance().getLastQueryResult());
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    };
                    Thread th = new Thread(r);
                    th.setDaemon(true);
                    th.start();

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
        DataStore.getInstance().addChatHistoryLoadedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_layout, container, false);
        getViews(view);

        sendButton.setOnClickListener(this);
        // ----Set auto-scroll of ListView when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        chatAdapter = new ChatAdapter(getActivity());
        chatAdapter.setWithUser(otherUser != null ? otherUser.getUsername() : null);
        msgListView.setAdapter(chatAdapter);
        registerForContextMenu(msgListView);

        if (otherUser == null || DataStore.getInstance().isChatHistoryLoaded(otherUser.getUsername()))
            progressBar.setVisibility(View.GONE);
        else
            progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.chats_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            switch (item.getItemId()) {
                case R.id.copy_text:

                    if (info.position >= 0) {
                        ChatMessage selectedMsg = ((ChatMessage) chatAdapter.getItem(info.position));

                        if (getActivity() != null) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(selectedMsg.getBody(), selectedMsg.getBody());
                            clipboard.setPrimaryClip(clip);
                        }
                    }

                    return true;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            emptyView.setText(getResources().getString(R.string.xmpp_no_messages));
            msgListView.setEmptyView(emptyView);
        }
    }

    private boolean listIsAtTop()   {
        if(msgListView.getChildCount() == 0) return true;
        return msgListView.getChildAt(0).getTop() == 0;
    }

    private void getViews(View view) {
        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        sendButton = (ImageButton) view.findViewById(R.id.sendMessageButton);
        progressBar = (ProgressBar) view.findViewById(R.id.msg_loading_progress);
        emptyView = (TextView) view.findViewById(R.id.emptyResults);
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

    @Override
    public void onLoadingChatHistoryDone(String username) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}