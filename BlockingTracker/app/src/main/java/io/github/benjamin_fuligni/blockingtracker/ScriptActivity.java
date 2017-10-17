package io.github.benjamin_fuligni.blockingtracker;

import android.content.Intent;
import android.os.Bundle;
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

                Log.d("**** In MainActivity", "Going to Set View");
                Intent intent = new Intent(ScriptActivity.this, SetActivity.class);
                Log.d("**** In MainActivity", selection.toString());
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
