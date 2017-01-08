package com.sag0ld.background_stories;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Variable
        Button buttonFindFolder = (Button) findViewById(R.id.buttonFindFolder);
        EditText path = (EditText) findViewById(R.id.pathFolder);

        // Listener
        View.OnClickListener findFolderListener = new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchView.class));
            }
        };

        buttonFindFolder.setOnClickListener(findFolderListener);

    }
}
