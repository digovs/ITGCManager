package com.vieira.rodrigo.itgcmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ErrorMessageDialogActivity extends Activity {

    public static final String KEY_MESSAGE_TEXT = "message_text";
    public static final String KEY_BUTTON_TEXT = "button_text";

    private TextView messageView;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_message_dialog);

        Intent intent = getIntent();
        String messageText = intent.getStringExtra(KEY_MESSAGE_TEXT);
        String okButtonText = intent.getStringExtra(KEY_BUTTON_TEXT);

        messageView = (TextView) findViewById(R.id.error_message_dialog_text);
        messageView.setText(messageText);

        okButton = (Button) findViewById(R.id.error_message_dialog_button);
        okButton.setText(okButtonText);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
