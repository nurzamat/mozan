package com.mozan;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mozan.adapter.DialogsAdapter;
import com.mozan.util.GlobalVar;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import java.util.ArrayList;
import java.util.List;

public class MyMessagesFragment extends Fragment {

    private ListView dialogsListView;
    private ProgressBar progressBar;
    private Activity context;

	public MyMessagesFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.dialogs_activity, container, false);

        dialogsListView = (ListView) rootView.findViewById(R.id.roomsList);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        context = getActivity();
        // get dialogs
        //
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);

        QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args) {

                GlobalVar.quickbloxDialogs.addAll(dialogs);
                // collect all occupants ids
                //
                List<Integer> usersIDs = new ArrayList<Integer>();
                for (QBDialog dialog : dialogs) {
                    usersIDs.addAll(dialog.getOccupants());
                }

                // Get all occupants info
                //
                QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
                requestBuilder.setPage(1);
                requestBuilder.setPerPage(usersIDs.size());
                //
                QBUsers.getUsersByIDs(usersIDs, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {

                        // Save users
                        //
                        ((AppController) context.getApplication()).setDialogsUsers(users);

                        // build list view
                        //
                        buildListView(dialogs);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        Toast.makeText(context, errors.toString(), Toast.LENGTH_SHORT).show();
                    }

                });
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(context, errors.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    void buildListView(List<QBDialog> dialogs){
        final DialogsAdapter adapter = new DialogsAdapter(dialogs, context);
        dialogsListView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);

        // choose dialog
        //
        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog)adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, (QBDialog)adapter.getItem(position));

                // group
                if(selectedDialog.getType().equals(QBDialogType.GROUP)){
                    bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);

                    // private
                } else {
                    bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
                }

                // Open chat activity
                //
                GlobalVar._Post = null;
                ChatActivity.start(context, bundle);
            }
        });
    }
}
