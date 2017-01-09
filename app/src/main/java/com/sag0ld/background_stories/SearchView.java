package com.sag0ld.background_stories;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SearchView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        // Variable
        final ListView directories = (ListView) findViewById(R.id.ListDirectory);
        final TextView currentDirectory = (TextView) findViewById(R.id.txtCurrentDirectory);
        final Button previousDirectory = (Button) findViewById(R.id.btnPreviousDirectory);
        final Directory dir;
        final Directory oldDir;
        final String previousPreviousDirectory = "None";

        // Initialize list
        currentDirectory.setText(Environment.getRootDirectory().getAbsolutePath());
        previousDirectory.setText(previousPreviousDirectory);
        previousDirectory.setEnabled(false);
        dir = initialiseListView(directories,currentDirectory.getText().toString());
        // Initialize the old directory
        oldDir = dir;

        // Listener for ItemClick of the ListView
        ListView.OnItemClickListener directoryOnItemClick = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // Initialize oldDirectory
                oldDir.setChildDirectories(dir.getFolders());
                oldDir.setChildFiles(dir.getFiles());
                oldDir.setPath(dir.getPath());

                String directoryName = (String)directories.getItemAtPosition(position);

                // Change text of final variable
                previousPreviousDirectory.replaceAll(".*", previousDirectory.getText().toString());

                previousDirectory.setEnabled(true);
                previousDirectory.setText(currentDirectory.getText());
                currentDirectory.setText(directoryName);

                // Initialize listView with the new parent
                File folder = dir.findDirectory(directoryName);
                Directory dirTemp = initialiseListView(directories, folder.getPath());
                dir.setChildDirectories(dirTemp.getFolders());
                dir.setChildFiles(dirTemp.getFiles());
                dir.setPath(folder.getPath());
            }
        };

        Button.OnClickListener previousOnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String directoryName = previousDirectory.getText().toString();

                if (!previousDirectory.getText().toString().equalsIgnoreCase("None")) {
                    File tmpFolder = oldDir.findDirectory(directoryName);
                    if (tmpFolder != null) {
                        // Initialize listView with the oldParent
                        Directory dirTemp = initialiseListView(directories, oldDir.getPath());
                        // Initialize final variable directory
                        dir.setChildDirectories(dirTemp.getFolders());
                        dir.setChildFiles(dirTemp.getFiles());
                        dir.setPath(oldDir.getPath());
                    }
                } else {
                    previousDirectory.setEnabled(false);
                }

                previousDirectory.setText(previousPreviousDirectory);
                currentDirectory.setText(directoryName);
            }
        };

        // Initialize Listener
        directories.setOnItemClickListener(directoryOnItemClick);
        previousDirectory.setOnClickListener(previousOnClickListener);
    }

    private Directory initialiseListView(ListView p_list, String p_path) {
        Directory currentDirectory = getChildrenDirectory(p_path);

        //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
          //      android.R.id.text1, currentDirectory.getItemNames());
        DirectoryArrayAdapter adapter = new DirectoryArrayAdapter (
              this, R.layout.item_list, currentDirectory.getFolders());
        p_list.setAdapter(adapter);

        return currentDirectory;
    }

    private Directory getChildrenDirectory (String directoryPath) {
        File folderSource = new File(directoryPath);

        File[] folders = folderSource.listFiles();
        ArrayList<File> foldersList = new ArrayList<>();
        ArrayList<File> filesList = new ArrayList<>();
        for (File inFile : folders) {
            if (inFile.isDirectory()) {
                foldersList.add(inFile);
            }
            else {
                filesList.add(inFile);
            }
        }
        Directory dir = new Directory(foldersList, filesList, directoryPath);
        return dir;
    }
}
