package com.nasty.receiptly;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SpendingFragment.OnFragmentInteractionListener, ReceiptsFragment.OnFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //member vars to store numerical individual receipt data for the db
    private String mMerchantName = "";
    private String mDollarsSpent = "";
    private String mTotalTax = "";

    public void onNotificationsEnabled(View v)
    {
        Log.d("!!!!", "Notifications Enabled Checkbox Functioning..");
    }

    //method to generate a unique name to save our pictures with to avoid
    //replacing files unintentionally
    private String getPictureFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        return "Receiptly-My-Receipt-@-" + timeStamp + ".jpg";
    }

    protected void getReceiptData()
    {
        //create alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Receipt Data");

        //create a layout to contain the multiple children
        LinearLayout alertDLayout = new LinearLayout(this);
        alertDLayout.setPadding(16, 16, 16, 16);
        alertDLayout.setOrientation(LinearLayout.VERTICAL);

        //set up the input, create the individual edit text view object
        final EditText merchantNameInput = new EditText(this);
        merchantNameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        merchantNameInput.setHint(R.string.view_merchant_name);
        merchantNameInput.setMaxLines(1);
        //assign it to the layout
        alertDLayout.addView(merchantNameInput);

        final TextView dollarsSpentTextView = new TextView(this);
        dollarsSpentTextView.setText(R.string.view_dollars_spent);
        dollarsSpentTextView.setPadding(0, 24, 0, 0);
        alertDLayout.addView(dollarsSpentTextView);

        final EditText dollarsSpentInput = new EditText(this);
        dollarsSpentInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dollarsSpentInput.setHint(R.string.view_dollars_spent);
        dollarsSpentInput.setText(R.string.view_defvalue_spent);
        alertDLayout.addView(dollarsSpentInput);

        final TextView totalTaxTextView = new TextView(this);
        totalTaxTextView.setText(R.string.view_total_tax);
        alertDLayout.addView(totalTaxTextView);

        final EditText totalTaxInput = new EditText(this);
        totalTaxInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        totalTaxInput.setHint("Total Tax Paid on this Transaction");
        totalTaxInput.setText(R.string.view_defvalue_spent);
        alertDLayout.addView(totalTaxInput);

        //set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //process the data inputted into member vars for db use later
                mMerchantName = merchantNameInput.getText().toString();
                mDollarsSpent = dollarsSpentInput.getText().toString();
                mTotalTax = totalTaxInput.getText().toString();

                Log.d("!!!!", "mMerchantName = " + mMerchantName);
                Log.d("!!!!", "mDollarsSpent = " + mDollarsSpent);
                Log.d("!!!!", "mTotalTax = " + mTotalTax);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //put some logic here to delete the corresponding photo
                dialog.cancel();
            }
        });

        //set the dialog builder to inflate the programmatic layout
        builder.setView(alertDLayout);
        //build the dialog into a alert dialog object
        final AlertDialog receiptDataDialog = builder.show();
        receiptDataDialog.setCanceledOnTouchOutside(false);
        //get the ok button
        final Button okButton = receiptDataDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        //disable the button until proper data is entered
        okButton.setEnabled(false);
        //set a TextChanged listener to handle the checks for proper data
        merchantNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //input validation every time the text changes at all
                //(not null fields)
                if (!merchantNameInput.getText().toString().isEmpty()) {

                    okButton.setEnabled(true);
                }
                else
                {
                    //show an error
                    okButton.setEnabled(false);
                }

                if (!dollarsSpentInput.getText().toString().isEmpty()) {

                    okButton.setEnabled(true);
                }
                else
                {
                    //show an error
                    okButton.setEnabled(false);
                }

                if (!totalTaxInput.getText().toString().isEmpty()) {

                    okButton.setEnabled(true);
                }
                else {
                    //show an error
                    okButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create our implicit intent to the camera app of the users choice
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //get a reference to the sd public root dir and then the framework pictures folder
                File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //generate our unique file name
                String fileName = getPictureFileName();
                //create a file object at the /Pictures directory with a unique name
                File imageFile = new File(imageDirectory, fileName);
                //swap it to an android friendly Uri
                Uri imageUri = Uri.fromFile(imageFile);
                //package the uri with our implicit intent and let the framework do the rest
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

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

        //if user accepted photo
        if (resultCode == RESULT_OK)
        {
            //get receipt data here from the user in a dialog
            getReceiptData();
            //put code to show thumbnails in listview
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
        // Handle navigation view item clicks here.
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
