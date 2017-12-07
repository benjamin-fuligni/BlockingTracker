package io.github.benjamin_fuligni.blockingtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.PinView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ScriptActivity extends AppCompatActivity {

    public static final String TEXT_SELECTED = "io.github.benjamin_fuligni.TEXTSELECTED";
    public static final String NUMBER_INSERT = "io.github.benjamin_fuligni.NUMBERINSERT";
    public static final String DATABASE_INSERT = "io.github.benjamin_fuligni.DATABASEINSERT";
    public static final String STRING_HELP_TEXT = "Please select a script by clicking on the three dots in the top right corner and selecting a .txt file from your device.";

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_TEXT_GET = 3;
    private String footnoteNum;
    private DBManager dbManager;
    private boolean scriptSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_script);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbManager = new DBManager(getApplicationContext());
        dbManager.open();

        TextView tv = (TextView)findViewById(R.id.script);
        String scriptText = dbManager.get("script");
        if (scriptText == null) {
            dbManager.insert("script", STRING_HELP_TEXT);
            scriptText = dbManager.get("script");
        } else if (!scriptText.equals(STRING_HELP_TEXT)) {
            scriptSelected = true;
        }
        tv.setText(scriptText);

        footnoteNum = dbManager.get("footnoteNum");
        if (footnoteNum == null) {
            dbManager.insert("footnoteNum", "0");
            footnoteNum = dbManager.get("footnoteNum");
        }

        Button fab = (Button) findViewById(R.id.addPin);
        if (!scriptSelected) {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pulledNum = 0;
                TextView tv = (TextView) findViewById(R.id.script);
                int start, end;
                CharSequence selection = "";
                if (tv.isFocused()) {
                    int selStart = tv.getSelectionStart();
                    int selEnd = tv.getSelectionEnd();
                    start = Math.max(0, Math.min(selEnd, selStart));
                    end = Math.max(0, Math.max(selEnd, selStart));
                    selection = tv.getText().subSequence(start, end);
                    if (selection.length() >= 3 && selection.charAt(0) == '[' && selection.charAt(2) == ']') {
                        pulledNum = (int)selection.charAt(1) - 48;
                    } else {
                        footnoteNum = ((Integer)(Integer.parseInt(footnoteNum)+1)).toString();
                        String newText = new StringBuilder().append(tv.getText().subSequence(0, start))
                                .append(selection).append(" [" + footnoteNum + "] ").append(tv.getText().subSequence(end, tv.getText().length())).toString();
                        tv.setText(newText);
                        dbManager.update("script", newText);
                        dbManager.update("footnoteNum", footnoteNum);
                    }
                }

                Intent intent = new Intent(ScriptActivity.this, SetActivity.class);
                intent.putExtra(TEXT_SELECTED, selection.toString());
                intent.putExtra(NUMBER_INSERT, footnoteNum);
                intent.putExtra(DATABASE_INSERT, pulledNum);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_script, menu);
        menu.findItem(R.id.changeFloorplan).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.changeScript:
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Explain to the user why we need to read the contacts
                    }
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant that should be quite unique
                }
                Intent openDocumentIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                openDocumentIntent.setType("text/plain");
                startActivityForResult(openDocumentIntent, REQUEST_TEXT_GET);
                return true;
            case R.id.about:
                Intent settingsIntent = new Intent(ScriptActivity.this, AboutActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEXT_GET && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String fileContent = readTextFile(uri);
            TextView script = (TextView) findViewById(R.id.script);
            script.setText(fileContent);
            Button fab = (Button) findViewById(R.id.addPin);
            fab.setVisibility(View.VISIBLE);
            scriptSelected = true;
            dbManager.update("script", fileContent);
            dbManager.update("footnoteNum", "0");
            footnoteNum = "0";
        }
    }

    private String readTextFile(Uri uri){

        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}
