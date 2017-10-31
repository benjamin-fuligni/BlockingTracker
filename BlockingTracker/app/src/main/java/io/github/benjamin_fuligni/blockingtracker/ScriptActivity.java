package io.github.benjamin_fuligni.blockingtracker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ScriptActivity extends AppCompatActivity {
    public static final String TEXT_SELECTED = "io.github.benjamin_fuligni.TEXTSELECTED";

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView) findViewById(R.id.textView);
                int start = 0;
                int end = tv.getText().length();
                CharSequence selection = "";
                if (tv.isFocused()) {
                    int selStart = tv.getSelectionStart();
                    int selEnd = tv.getSelectionEnd();
                    start = Math.max(0, Math.min(selEnd, selStart));
                    end = Math.max(0, Math.max(selEnd, selStart));
                    selection = tv.getText().subSequence(start, end);
                    String newText = new StringBuilder().append(tv.getText().subSequence(0, start))
                            .append(selection).append("HELLO").append(tv.getText().subSequence(end, tv.getText().length())).toString();
                    tv.setText(newText);
                }
                Snackbar.make(view, selection, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(ScriptActivity.this, SetActivity.class);
                intent.putExtra(TEXT_SELECTED, selection.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_script, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.changeFloorplan:
                Log.d("*** In Main Activity", "changing floorPlan");
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Log.d("*** In Main Activity", "new intent created");
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
        }
    }
}
