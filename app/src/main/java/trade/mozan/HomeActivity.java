package trade.mozan;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import trade.mozan.adapter.NavDrawerListAdapter;
import trade.mozan.model.NavDrawerItem;
import trade.mozan.util.Constants;
import trade.mozan.util.GlobalVar;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity
{
    private QBChatService chatService;
    //
    //
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
    private Context context;
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
    FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

        if(!GlobalVar.quickbloxLogin && !GlobalVar.Phone.equals("") && !GlobalVar.Token.equals(""))
        {
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
                    GlobalVar.quickbloxToken = session.getToken();
                    ((AppController) getApplication()).setCurrentUser(user);

                    // login to Chat
                    //
                    loginToChat(user);
                }

                @Override
                public void onError(List<String> errors) {
                    Toast.makeText(HomeActivity.this, errors.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        //end messages
        Intent i = getIntent();
        context = getApplicationContext();
		mTitle = mDrawerTitle = getTitle();
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
      //navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(context,
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
        int _case = i.getIntExtra("case", 0);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			if(_case == 6)
            {
                displayView(6);                   //add or edit post
                GlobalVar.adv_position = false;
            }
            else if(_case == 7)
            {
                displayView(7);       //search
                GlobalVar.query = "";
            }
            else if(_case == 1)
            {
                displayView(1);  //my posts
            }
            else if(_case == 2)
            {
                displayView(2);  //my profile
                GlobalVar.profile_position = false;
            }
            else if(_case == 3)
            {
                displayView(3);  //my messages
                GlobalVar.messages_position = false;
            }
            else if(_case == 8)
            {
                displayView(8);  //categories
            }
            else displayView(0); // home
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        // Assumes current activity is the searchable activity
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(context, SearchResultsActivity.class)));
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	public void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
             {
                 fragment = new HomeFragment();
                //Bundle args = new Bundle();
                // args.putInt("position", 0);
                // fragment.setArguments(args);
                 break;
             }
		case 1:
             {
                 if (!GlobalVar.Phone.equals("") && !GlobalVar.Token.equals(""))
                 {
                     fragment = new MyPostsFragment();
                 }
                 else
                 {
                     GlobalVar.adv_position = true;
                     GlobalVar.profile_position = false;
                     GlobalVar.messages_position = false;

                     Intent in;
                     if(GlobalVar.isCodeSent)
                         in = new Intent(context, RegisterActivity.class);
                     else in = new Intent(context, CodeActivity.class);
                     startActivity(in);
                 }
                 break;
             }
		case 2:
             {
                 if (!GlobalVar.Phone.equals("") && !GlobalVar.Token.equals(""))
                 {
                     fragment = new MyProfileFragment();
                 }
                 else
                 {
                     GlobalVar.profile_position = true;
                     GlobalVar.adv_position = false;
                     GlobalVar.messages_position = false;

                     Intent in;
                     if(GlobalVar.isCodeSent)
                         in = new Intent(context, RegisterActivity.class);
                     else in = new Intent(context, CodeActivity.class);
                     startActivity(in);
                 }
                 break;
             }
		case 3:
             {
                 if (!GlobalVar.Phone.equals("") && !GlobalVar.Token.equals(""))
                 {
                     fragment = new MyMessagesFragment();
                 }
                 else
                 {
                     GlobalVar.messages_position = true;
                     GlobalVar.adv_position = false;
                     GlobalVar.profile_position = false;

                     Intent in;
                     if(GlobalVar.isCodeSent)
                         in = new Intent(context, RegisterActivity.class);
                     else in = new Intent(context, CodeActivity.class);
                     startActivity(in);
                 }
                 break;
             }

		case 4:
             {
                 fragment = new SettingsFragment();
                 break;
             }

        case 5:
            {
                fragment = new MyClientsFragment();
                break;
            }

      // conditional fragments
        case 6:
            {
                fragment = new AddPostFragment();
                break;
            }
        case 7:
            {
                fragment = new SearchResultsFragment();
                break;
            }
        case 8:
            {
                fragment = new CategoriesFragment();
                break;
            }
		default:
			break;
		}

		if (fragment != null) {
            if(position == 0) {
                GlobalVar.isHomeFragment = true;
            }
            else {
                GlobalVar.isHomeFragment = false;
            }
		 	fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("HomeActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

    @Override
    public void onBackPressed() {
        if(!GlobalVar.isHomeFragment)
        {
            displayView(0);
        }
        else
        {
            //super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
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

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(List errors) {
                Toast.makeText(HomeActivity.this, errors.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
