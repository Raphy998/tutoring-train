package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.persistance;

import android.provider.BaseColumns;

/**
 * Created by Elias on 27.01.2018.
 */

public final class DBNames {

    private DBNames() {}

    /* Inner class that defines the table contents */
    public static class ChatMessage implements BaseColumns {
        public static final String TABLE_NAME = "chatmsgs";
        public static final String COLUMN_NAME_ID = "msgid";
        public static final String COLUMN_NAME_SENDER = "sender";
        public static final String COLUMN_NAME_RECEIVER = "receiver";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}