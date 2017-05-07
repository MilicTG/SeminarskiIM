package com.ivan_milic.www.seminarskiim.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ivan_milic.www.seminarskiim.R;


public class SettingsActivity extends AppCompatActivity {

    public static final String MY_GLOBAL_PREFS = "my_global_prefs";

    Switch cSwitch;
    Switch fSwitch;
    Switch kmhSwitch;
    Switch knotSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.Settings);
        setContentView(R.layout.activity_settings);

        //Setting toolbar to be invisible
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences(MY_GLOBAL_PREFS, MODE_PRIVATE);

        setViews();

        //Getting the values for temperature and wind speed
        String prefTemperature = preferences.getString("TEMP", "");
        if (prefTemperature.contains(" °C")) {
            cSwitch.setChecked(true);
        } else if (prefTemperature.contains(" °F")) {
            fSwitch.setChecked(true);
        }

        cSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SharedPreferences.Editor editor = getSharedPreferences(MY_GLOBAL_PREFS, MODE_PRIVATE).edit();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cSwitch.isChecked()) {
                    editor.putString("TEMP", " °C");
                    editor.apply();
                    fSwitch.setChecked(false);
                } else {
                    editor.putString("TEMP", " °F");
                    editor.apply();
                    fSwitch.setChecked(true);
                }
            }
        });
        fSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SharedPreferences.Editor editor = getSharedPreferences(MY_GLOBAL_PREFS, MODE_PRIVATE).edit();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fSwitch.isChecked()) {
                    editor.putString("TEMP", " °F");
                    editor.apply();
                    cSwitch.setChecked(false);
                } else {
                    editor.putString("TEMP", " °C");
                    editor.apply();
                    cSwitch.setChecked(true);
                }
            }
        });


        String prefWind = preferences.getString("WIND", "");
        if (prefWind.contains(" km/h")) {
            kmhSwitch.setChecked(true);
        } else if (prefWind.contains(" KNOTS")) {
            knotSwitch.setChecked(true);
        }

        kmhSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_GLOBAL_PREFS, MODE_PRIVATE).edit();
                if (kmhSwitch.isChecked()) {
                    editor.putString("WIND", " km/h");
                    editor.apply();
                    knotSwitch.setChecked(false);
                } else {
                    editor.putString("WIND", " KNOTS");
                    editor.apply();
                    knotSwitch.setChecked(true);
                }
            }
        });

        knotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_GLOBAL_PREFS, MODE_PRIVATE).edit();
                if (knotSwitch.isChecked()) {
                    editor.putString("WIND", " KNOTS");
                    editor.apply();
                    kmhSwitch.setChecked(false);
                } else {
                    editor.putString("WIND", " km/h");
                    editor.apply();
                    kmhSwitch.setChecked(true);
                }
            }
        });
    }

    private void setViews() {
        cSwitch = (Switch) findViewById(R.id.cSwitch);
        fSwitch = (Switch) findViewById(R.id.fSwitch);
        kmhSwitch = (Switch) findViewById(R.id.kmhSwitch);
        knotSwitch = (Switch) findViewById(R.id.knotSwitch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.AboutBtn:
                AlertDialog.Builder aboutDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                aboutDialogBuilder
                        .setTitle(R.string.About_menu)
                        .setMessage("Author: Ivan Milic \nApp details: Algebra Seminarski" +
                                " \nAssets by: Ivan Milic \nDesign by: Ivan Milic")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog aboutDialog = aboutDialogBuilder.create();
                aboutDialog.show();
                return true;
            default:
                return false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
