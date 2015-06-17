package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vieira.rodrigo.itgcmanager.R;


public class ConfirmationDialogActivity extends Activity {

    public static final String KEY_MESSAGE = "message";

    TextView messageView;
    Button yesButton;
    Button noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_dialog);

        Intent intent = getIntent();
        String messageText = intent.getStringExtra(KEY_MESSAGE);

        messageView = (TextView) findViewById(R.id.confirmation_dialog_text);
        messageView.setText(messageText);

        yesButton = (Button) findViewById(R.id.confirmation_dialog_yes_button);
        noButton = (Button) findViewById(R.id.confirmation_dialog_no_button);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }
}
