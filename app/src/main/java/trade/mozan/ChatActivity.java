package trade.mozan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import trade.mozan.adapter.ChatAdapter;
import trade.mozan.util.ApiHelper;
import trade.mozan.util.ChatManager;
import trade.mozan.util.GlobalVar;
import trade.mozan.util.GroupChatManagerImpl;
import trade.mozan.util.PrivateChatManagerImpl;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends Activity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_DIALOG = "dialog";
    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    private EditText messageEditText;
    private ListView messagesContainer;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    private Mode mode = Mode.PRIVATE;
    private ChatManager chat;
    private ChatAdapter adapter;
    private QBDialog dialog = null;
    private ArrayList<QBChatMessage> messages;
    private ArrayList<QBChatMessage> history;
    private Integer opponentID;

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        // Get chat dialog
        //
        dialog = (QBDialog)intent.getSerializableExtra(EXTRA_DIALOG);
        mode = (Mode) intent.getSerializableExtra(EXTRA_MODE);
        if(dialog != null)
        {
          initViews();
        }
        else
        {
            HttpAsyncTask task = new HttpAsyncTask();
            task.execute("https://api.quickblox.com/chat/Dialog.json");
        }
    }

    @Override
    public void onBackPressed() {
        try {
            chat.release();
        } catch (XMPPException e) {
            Log.e(TAG, "failed to release chat", e);
        }
        super.onBackPressed();
    }

    private void initViews() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageEditText = (EditText) findViewById(R.id.messageEdit);
        sendButton = (ImageButton) findViewById(R.id.chatSendButton);

        //TextView meLabel = (TextView) findViewById(R.id.meLabel);
        TextView companionLabel = (TextView) findViewById(R.id.companionLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        switch (mode) {
            case GROUP:
                chat = new GroupChatManagerImpl(this);
                //container.removeView(meLabel);
                container.removeView(companionLabel);

                // Join group chat
                //
                progressBar.setVisibility(View.VISIBLE);
                //
                ((GroupChatManagerImpl) chat).joinGroupChat(dialog, new QBEntityCallbackImpl() {
                    @Override
                    public void onSuccess() {

                        // Load Chat history
                        //
                        loadChatHistory();
                    }

                    @Override
                    public void onError(List list) {
                        Toast.makeText(ChatActivity.this, list.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case PRIVATE:
                try
                {
                    if(GlobalVar._Post != null)
                    {
                        opponentID = Integer.parseInt(GlobalVar._Post.getQuickbloxId());
                        // TODO: Set 'Displayed_name' or phone number.
                        companionLabel.setText(GlobalVar._Post.getUsername());
                    }
                    else
                    {
                        opponentID = ((AppController)getApplication()).getOpponentIDForPrivateDialog(dialog);
                        companionLabel.setText(((AppController)getApplication()).getDialogsUsers().get(opponentID).getLogin());
                    }

                    chat = new PrivateChatManagerImpl(this, opponentID);
                    // Load CHat history
                    //
                    loadChatHistory();
                    break;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                // Send chat message
                //
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(messageText);
                chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                chatMessage.setDateSent(new Date().getTime()/1000);

                try
                {
                    GlobalVar.quickbloxID = opponentID.toString();
                    chat.sendMessage(chatMessage);
                }
                catch (XMPPException e) {
                    Log.e(TAG, "failed to send a message", e);
                } catch (SmackException sme){
                    Log.e(TAG, "failed to send a message", sme);
                }

                messageEditText.setText("");

                if(mode == Mode.PRIVATE) {
                    showMessage(chatMessage);
                }
            }
        });
    }

    private void loadChatHistory(){

        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.sortDesc("date_sent");
        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                history = messages;
                adapter = new ChatAdapter(ChatActivity.this, new ArrayList<QBChatMessage>());
                messagesContainer.setAdapter(adapter);

                for (int i = messages.size() - 1; i >= 0; --i) {
                    QBChatMessage msg = messages.get(i);
                    showMessage(msg);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(ChatActivity.this, errors.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showMessage(QBChatMessage message) {
        adapter.add(message);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                scrollDown();
            }
        });
    }

    private void scrollDown() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    public static enum Mode {PRIVATE, GROUP}

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 3);
                jsonObject.put("name", "test");
                if(GlobalVar._Post != null)
                jsonObject.put("occupants_ids", GlobalVar._Post.getQuickbloxId());
                else jsonObject.put("occupants_ids", GlobalVar.quickbloxID);

                ApiHelper api = new ApiHelper();
                JSONObject result = api.createDialog(urls[0], jsonObject, GlobalVar.quickbloxToken);
                Log.d("dialog result", result.toString());

                dialog = new QBDialog();
                dialog.setDialogId(result.getString("_id"));
                dialog.setLastMessage(result.getString("last_message"));
                dialog.setLastMessageUserId(result.getInt("last_message_user_id"));
                dialog.setName(result.getString("name"));
                dialog.setPhoto(result.getString("photo"));

                ArrayList<Integer> ids = new ArrayList<Integer>();
                try
                {
                    for (int i = 0; i < result.getJSONArray("occupants_ids").length(); i++)
                    {
                        ids.add(result.getJSONArray("occupants_ids").getInt(i));
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                dialog.setOccupantsIds(ids);
                dialog.setType(QBDialogType.PRIVATE);
                dialog.setRoomJid(result.getString("xmpp_room_jid"));
                dialog.setUnreadMessageCount(result.getInt("unread_messages_count"));
                dialog.setUserId(result.getInt("user_id"));

            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            // adding dialog to list
            if(dialog != null)
            GlobalVar.quickbloxDialogs.add(dialog);

            initViews();
        }
    }
}
