package com.nasty.receiptly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nasty.receiptly.Utility.deleteImage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SpendingFragment.OnFragmentInteractionListener, ReceiptsFragment.OnFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final Context c = this;
    protected static File imageFile;

    public void onNotificationsEnabled(View v)
    {
        Log.d("!!!!", "Notifications Enabled Checkbox Functioning..");
    }

    // Method to generate a unique name to save our pictures with to avoid
    // replacing files unintentionally
    private String getPictureFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        return "Receiptly-My-Receipt-@-" + timeStamp + ".jpg";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make our init frag global within the onCreate scope
        Fragment fragment = null;

        // Attempt to create a new instance of said fragment
        try {
            fragment = (Fragment) ReceiptsFragment.newInstance(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the fragment within the view container
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_activity_container, fragment).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Disable the file uri exposure check to avoid exception and ensure compatibility with other apps on android N and <
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create our implicit intent to the camera app of the users choice
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Get a reference to the sd public root dir and then the framework pictures folder /Receiptly
                File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                // Generate our unique file name
                String fileName = getPictureFileName();
                // Create a file object at the /Pictures directory with a unique name
                imageFile = new File(imageDirectory, fileName);
                // Swap it to an android friendly Uri
                Uri imageUri = Uri.fromFile(imageFile);
                // Package the uri with our implicit intent and let the framework do the rest
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                } else {
                    Toast noCameraToast = Toast.makeText(c, "Error: No working camera app installed on this device! Please visit the play store and download a suitable camera app to continue.", Toast.LENGTH_LONG);
                    noCameraToast.show();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If user accepted photo
        if (resultCode == RESULT_OK)
        {
            // Get receipt data here from the user in a dialog
            Utility.getReceiptData(this);
            //TODO: Somehow refresh data after it's put in db
        }
        else
        {
            deleteImage(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Navigation drawer view item clicks are handled here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_spending_limits) {
            Log.d("!!!!", "onItemSelected @ spending frag for drawer working..");
            fragmentClass = SpendingFragment.class;
        } else if (id == R.id.nav_generate_report) {
            return true;
        } else if (id == R.id.nav_export_photo) {
            return true;
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.nav_my_receipts) {
            Log.d("!!!!", "onItemSelected @ receipts frag for drawer working..");
            fragmentClass = ReceiptsFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_activity_container, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

}
