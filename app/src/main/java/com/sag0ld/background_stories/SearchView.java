package com.sag0ld.background_stories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class SearchView extends Activity {

    final private Directory dir = new Directory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        final String searchFor = "";

        if (getIntent().hasExtra("BrowseType")) {
           searchFor.concat(getIntent().getStringExtra("BrowseType"));
        }

        // Variable
        final ListView directories = (ListView) findViewById(R.id.ListDirectory);
        final TextView txtCurrentDirectoryName = (TextView) findViewById(R.id.txtCurrentDirectory);
        final Button btnPreviousDirectory = (Button) findViewById(R.id.btnPreviousDirectory);
        final Button btnChoose = (Button) findViewById(R.id.btnChoose);

        final Context context = this;

        // Initialize interface
        txtCurrentDirectoryName.setText(dir.getCurrentDirectory().getName());
        if(dir.hasPreviousDirectory())
            btnPreviousDirectory.setText(dir.getPreviousDirectory().getName());
        else {
            btnPreviousDirectory.setEnabled(false);
            btnPreviousDirectory.setText("/");
        }

        // Listener for ItemClick of the ListView
        ListView.OnItemClickListener directoryOnItemClick = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                File directory = (File) directories.getItemAtPosition(position);
                if (directory.isDirectory()) {
                    dir.setPreviousDirectory(dir.getCurrentDirectory());
                    dir.setCurrentDirectory(directory);
                    btnPreviousDirectory.setText(dir.getPreviousDirectory().getName());
                    txtCurrentDirectoryName.setText(dir.getCurrentDirectory().getName());

                    // Initialize listView with the new parent
                    DirectoryArrayAdapter adapter =
                            new DirectoryArrayAdapter (SearchView.this, R.layout.item_list,
                                                       dir.getFiles());
                    directories.setAdapter(adapter);
                } else if (searchFor.equalsIgnoreCase(MainActivity.BrowseType.Folder.toString())){
                    // Pop Up error message
                    CharSequence text = "You have to click on a directory.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    Intent i = new Intent();
                    i.putExtra("pathPicture", directory.getPath());
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        };

        Button.OnClickListener previousOnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dir.setCurrentDirectory(dir.getPreviousDirectory());

                // update the interface
                if(dir.hasPreviousDirectory())
                    btnPreviousDirectory.setText(dir.getPreviousDirectory().getName());
                else {
                    btnPreviousDirectory.setEnabled(false);
                    btnPreviousDirectory.setText("/");
                }
                txtCurrentDirectoryName.setText(dir.getCurrentDirectory().getName());
                DirectoryArrayAdapter adapter =
                        new DirectoryArrayAdapter (SearchView.this, R.layout.item_list,
                                                   dir.getFiles());
                directories.setAdapter(adapter);
            }
        };

        Button.OnClickListener chooseOnClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("pathFolder", dir.getCurrentDirectory().getAbsolutePath());
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        };

        // Initialize Listener
        directories.setOnItemClickListener(directoryOnItemClick);

        btnPreviousDirectory.setOnClickListener(previousOnClickListener);
        btnChoose.setOnClickListener(chooseOnClick);
    }
}
