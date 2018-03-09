package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

import java.util.Date;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.ChatMessage;

/**
 * Created by Elias on 23.02.2018.
 */

public class IncomingChatMessageListenerImpl implements IncomingChatMessageListener {

    private XmppHandler handler;
    private DataStore ds;

    public IncomingChatMessageListenerImpl(XmppHandler handler) {
        this.handler = handler;
        this.ds = DataStore.getInstance();
    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        processMessage(message);
    }

    private void processMessage(final Message message) {
        if (message.getType() == Message.Type.chat && message.getBody() != null) {
            final ChatMessage chatMessage = new ChatMessage(
                    message.getFrom().getLocalpartOrNull().toString(),
                    message.getTo().toString(),
                    message.getBody(),
                    message.getStanzaId(),
                    false);
            chatMessage.setDateTime(new Date());        //now

            processMessage(chatMessage);
        }
    }

    private void processMessage(final ChatMessage chatMessage) {

        chatMessage.setMine(false);
        ds.addChatMessage(chatMessage, chatMessage.getReceiver());

        if (!XmppHandler.isActivityVisible()) {
            handler.showNewMessageNotification(chatMessage);
        }
    }
}
