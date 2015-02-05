package com.mozan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.mozan.adapter.ChatAdapter;
import com.mozan.util.ChatManager;
import com.mozan.util.GroupChatManagerImpl;
import com.mozan.util.PrivateChatManagerImpl;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
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
    private QBDialog dialog;
    private ArrayList<QBChatMessage> messages;

    private ArrayList<QBChatMessage> history;

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
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

        Intent intent = getIntent();

        // Get chat dialog
        //
        dialog = (QBDialog)intent.getSerializableExtra(EXTRA_DIALOG);
        mode = (Mode) intent.getSerializableExtra(EXTRA_MODE);
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
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                        dialog.setMessage("error when join group chat: " + list.toString()).create().show();
                    }
                });

                break;
            case PRIVATE:
                //Integer opponentID = ((AppController)getApplication()).getOpponentIDForPrivateDialog(dialog);
                Integer opponentID = 2273049;
                chat = new PrivateChatManagerImpl(this, opponentID);

                //companionLabel.setText(((AppController)getApplication()).getDialogsUsers().get(opponentID).getLogin());
                companionLabel.setText("test");
                // Load CHat history
                //
                loadChatHistory();
                break;
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

                try {
                    chat.sendMessage(chatMessage);
                } catch (XMPPException e) {
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                dialog.setMessage("load chat history errors: " + errors).create().show();
            }
        });
                    /*
            messages = new ArrayList<QBChatMessage>();
            String url = "https://api.quickblox.com/chat/Message.json?sort_desc=date_sent&limit=100&chat_dialog_id="+dialog.getDialogId();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG + "messages", response.toString());

                    try {
                        JSONArray jarray =  response.getJSONArray("items");
                        for (int i = 0; i < jarray.length(); i++) {
                            try {

                                JSONObject obj = jarray.getJSONObject(i);
                                QBChatMessage message = new QBChatMessage();
                                message.setId(obj.getString("_id"));
                                message.setDialogId(obj.getString("chat_dialog_id"));
                                message.setBody(obj.getString("message"));
                                message.setDateSent(obj.getInt("date_sent"));
                                message.setRecipientId(obj.getInt("recipient_id"));
                                messages.add(message);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        history = messages;
                        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<QBChatMessage>());
                        messagesContainer.setAdapter(adapter);

                        for (int i = messages.size() - 1; i >= 0; --i) {
                            QBChatMessage msg = messages.get(i);
                            showMessage(msg);
                        }

                        progressBar.setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            // Adding request to request queue

            AppController.getInstance().addToRequestQueue(jsonObjReq);
            */
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
}
