package com.sag0ld.background_stories;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class SearchView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        // Variable
        final ListView directories = (ListView) findViewById(R.id.ListDirectory);


        // List of directory
        File folderSource = new File("/");
        File[] folders = folderSource.listFiles();
        ArrayList<String> folderNames = new ArrayList<>();
        for (File inFile : folders) {
            if (inFile.isDirectory()) {
                folderNames.add(inFile.getName());
            }
        }
        // Initialize list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, folderNames );
        directories.setAdapter(adapter);

        // Listener
        AdapterView.OnItemClickListener directoryonClick = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Object o = directories.getItemAtPosition(position);

            }
        };

    }
}
