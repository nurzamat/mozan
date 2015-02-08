package trade.mozan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import trade.mozan.util.ApiHelper;
import trade.mozan.util.GlobalVar;
import trade.mozan.util.Constants;
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

public class SplashActivity extends Activity
{
    //

    private QBChatService chatService;
    private QBDialog dialog;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        // Init Chat
        //
        QBChatService.setDebugEnabled(true);
        QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
        if (!QBChatService.isInitialized()) {
            QBChatService.init(this);
        }
        chatService = QBChatService.getInstance();


        // create QB user
        //
        final QBUser user = new QBUser();
        user.setLogin(GlobalVar.Phone);
        user.setPassword(GlobalVar.Token);

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

                // Push Notification
                Intent intent = new Intent(SplashActivity.this, MessagesActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(SplashActivity.this, errors.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginToChat(final QBUser user)
    {

        chatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                // Start sending presences
                //
                try {

                    GlobalVar.quickbloxLogin = true;
                    chatService.startAutoSendPresence(Constants.AUTO_PRESENCE_INTERVAL_IN_SECONDS);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", 3);
                    jsonObject.put("name", "test");
                    jsonObject.put("occupants_ids", GlobalVar._Post.getQuickbloxId());//????

                    ApiHelper api = new ApiHelper();
                    JSONObject result = api.createDialog("https://api.quickblox.com/chat/Dialog.json", jsonObject, token);
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

                    //GlobalVar.quickbloxDialog = dialog;

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

                ChatActivity.start(SplashActivity.this, bundle);
                // go to Dialogs screen
                //
                // Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
                //startActivity(intent);

                finish();
            }

            @Override
            public void onError(List errors) {
                Toast.makeText(SplashActivity.this, errors.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}