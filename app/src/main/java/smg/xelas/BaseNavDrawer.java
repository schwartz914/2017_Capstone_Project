package smg.xelas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by Peter on 11/08/2017.
 */

public class BaseNavDrawer extends AppCompatActivity {
    protected FrameLayout frameLayout;
    protected ListView mDrawerList;

    protected String[] listArray = { "Home", "User Account", "Devices", "Reports", "Share Data" ,"Recommendations", "Logout"};
    protected static int position;
    private static boolean isLaunch = true;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    protected SharedPreferences userSession;
    protected CharSequence mActivityTitle;
    protected IpDetails ipDetails = new IpDetails();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.navigation_drawer_base_layout);
        mActivityTitle = "Xelas";
        userSession = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, listArray));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                openActivity(position);
            }
        });

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().hide();


        // ActionBarDrawerToggle ties together the the proper interactions between the sliding drawer and the action bar app icon
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {

                getSupportActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                actionBarDrawerToggle.syncState();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(listArray[position]);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                actionBarDrawerToggle.syncState();
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };



        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        if(isLaunch){
            isLaunch = false;
            openActivity(0);
        }


        actionBarDrawerToggle.syncState();

    }



    protected void openActivity(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        BaseNavDrawer.position = position; //Setting currently selected position in this field so that it will be available in our child activities.

        switch (position) {
            case 0:
                startActivity(new Intent(this, HomePage.class));
                break;
            case 1:
                startActivity(new Intent(this, EditAccount.class));
                break;
            case 2: //Devices
                startActivity(new Intent(BaseNavDrawer.this, Devices.class));
                break;
            case 3: //reports
                startActivity(new Intent(BaseNavDrawer.this, Reports.class));
                break;
            case 4: //Share Data
                startActivity(new Intent(BaseNavDrawer.this, ShareData.class));
                break;
            case 5:
                //startActivity(new Intent(this, Recommendations.class));
                break;
            case 6:
                userSession.edit().clear().apply();
                //PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply(); // clear user details
                startActivity(new Intent(this, Login.class));
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        return super.onPrepareOptionsMenu(menu);
    }

    /* We can override onBackPressed method to toggle navigation drawer*/
    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.closeDrawer(mDrawerList);
        }else {
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }

}
