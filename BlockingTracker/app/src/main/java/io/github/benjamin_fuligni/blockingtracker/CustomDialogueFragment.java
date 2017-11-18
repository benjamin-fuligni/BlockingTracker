package io.github.benjamin_fuligni.blockingtracker;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.PinView;

/**
 * Created by bibli on 11/15/2017.
 */

public class CustomDialogueFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText mEditText;


    public interface UserNameListener {
        void onFinishUserDialog(String user);
    }

    // Empty constructor required for DialogFragment
    public CustomDialogueFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prompt, container);
        mEditText = (EditText) view.findViewById(R.id.name);

        // set this instance as callback for editor action
        mEditText.setOnEditorActionListener(this);
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle("Enter Character Name");

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // Return input text to activity
        Activity activity = getActivity();
        this.dismiss();
        ((PinView)activity.findViewById(R.id.imageView)).newPin(mEditText.getText().toString(),
                new PointF(100f, 100f));
        return true;
    }
}
