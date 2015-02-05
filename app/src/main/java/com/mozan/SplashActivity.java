package com.mozan;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {
/*
    private static final String APP_ID = "92";
    private static final String AUTH_KEY = "wJHdOcQSxXQGWx5";
    private static final String AUTH_SECRET = "BTFsj7Rtt27DAmT";
    //
    private static final String USER_LOGIN = "bobbobbob";
    private static final String USER_PASSWORD = "bobbobbob";


    '{"session":{"_id":"54d23b65535c12de130022e2","application_id":18797,"created_at":"2015-02-04T15:31:49Z","device_id":0,"nonce":1324688332,"token":"6610226296f5f62f395e1abcfb61ac941dca4d75","ts":1423063905,"updated_at":"2015-02-04T15:31:49Z","user_id":2273055,"id":34183}}'

  */
    //
    private static final String APP_ID = "18797";
    private static final String AUTH_KEY = "r94hby9Rp-MHUO8";
    private static final String AUTH_SECRET = "AbgGep9pUV9JH8P";
    //
    private static final String USER_LOGIN = "996772143126";
    private static final String USER_PASSWORD = "4cce8f6b5d4ff3b069c9a0404d111314d919abc7";

    //private static final String USER_LOGIN = "996550559996";
    //private static final String USER_PASSWORD = "a44c955de727e45a22dc97a10db77b06e8c89c35";
    //

    static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;
    private QBChatService chatService;
    //private QBDialog dialog;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        // Init Chat
        //
        QBChatService.setDebugEnabled(true);
        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
        if (!QBChatService.isInitialized()) {
            QBChatService.init(this);
        }
        chatService = QBChatService.getInstance();


        // create QB user
        //
        final QBUser user = new QBUser();
        user.setLogin(USER_LOGIN);
        user.setPassword(USER_PASSWORD);

        QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle args) {
                // save current user
                //
                user.setId(session.getUserId());
                token = session.getToken();
                GlobalVar.quickbloxToken = token;
                ((AppController) getApplication()).setCurrentUser(user);

                // login to Chat
                //
                loginToChat(user);
            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setMessage("create session errors: " + errors).create().show();
            }
        });
    }

    private void loginToChat(final QBUser user){

        chatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                // Start sending presences
                //
                try {

                    GlobalVar.quickbloxLogin = true;
                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", 3);
                    jsonObject.put("name", "test");
                    jsonObject.put("occupants_ids", "2273049");

                    ApiHelper api = new ApiHelper();
                    JSONObject result = api.createDialog("https://api.quickblox.com/chat/Dialog.json", jsonObject, token);
                    Log.d("dialog result", result.toString());

                    GlobalVar.quickbloxDialog = new QBDialog();
                    GlobalVar.quickbloxDialog.setDialogId(result.getString("_id"));
                    GlobalVar.quickbloxDialog.setLastMessage(result.getString("last_message"));
                    GlobalVar.quickbloxDialog.setLastMessageUserId(result.getInt("last_message_user_id"));
                    GlobalVar.quickbloxDialog.setName(result.getString("name"));
                    GlobalVar.quickbloxDialog.setPhoto(result.getString("photo"));

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

                    GlobalVar.quickbloxDialog.setOccupantsIds(ids);
                    GlobalVar.quickbloxDialog.setType(QBDialogType.PRIVATE);
                    GlobalVar.quickbloxDialog.setRoomJid(result.getString("xmpp_room_jid"));
                    GlobalVar.quickbloxDialog.setUnreadMessageCount(result.getInt("unread_messages_count"));
                    GlobalVar.quickbloxDialog.setUserId(result.getInt("user_id"));

                    //GlobalVar.quickbloxDialog = dialog;

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, GlobalVar.quickbloxDialog);


                ChatActivity.start(SplashActivity.this, bundle);
                // go to Dialogs screen
                //
                // Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
                //startActivity(intent);


                /*
                QBUser user1 = new QBUser();
                user1.setId(2273055);

                QBUser user2 = new QBUser();
                user1.setId(2273049);
                ArrayList<QBUser> users = new ArrayList<QBUser>();
                users.add(user);
                users.add(user);

                ((AppController)getApplication()).addDialogsUsers(users);

                // Create new group dialog
                //
                ArrayList<Integer> ids = new ArrayList<Integer>();
                ids.add(2273055);
                ids.add(2273049);

                QBDialog dialogToCreate = new QBDialog();
                dialogToCreate.setName("Test name");
                dialogToCreate.setType(QBDialogType.PRIVATE);
                dialogToCreate.setOccupantsIds(ids);
                QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {
                        Log.d("splash activity", "success");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
                        bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);


                        ChatActivity.start(SplashActivity.this, bundle);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                        dialog.setMessage("dialog creation errors: " + errors).create().show();
                    }
                });
*/
                finish();
            }

            @Override
            public void onError(List errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setMessage("chat login errors: " + errors).create().show();
            }
        });
    }
}