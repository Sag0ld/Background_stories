package com.sag0ld.background_stories;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SearchView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        // Variable
        final ListView directories = (ListView) findViewById(R.id.ListDirectory);
        final TextView txtCurrentDirectoryName = (TextView) findViewById(R.id.txtCurrentDirectory);
        final Button btnPreviousDirectory = (Button) findViewById(R.id.btnPreviousDirectory);
        final Button btnChoose = (Button) findViewById(R.id.btnChoose);
        final Directory dir;

        // Initialize interface
        txtCurrentDirectoryName.setText(Environment.getExternalStorageDirectory().getName());
        btnPreviousDirectory.setText(Environment.getExternalStorageDirectory().getParent());
        btnPreviousDirectory.setEnabled(false);
        dir = initialiseListView(directories, Environment.getExternalStorageDirectory().getAbsolutePath());

        // Listener for ItemClick of the ListView
        ListView.OnItemClickListener directoryOnItemClick = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                File directory = (File)directories.getItemAtPosition(position);
                String[] parents = directory.getParent().split("/");
                btnPreviousDirectory.setText(parents[parents.length - 1]);
                txtCurrentDirectoryName.setText(directory.getName());
                btnPreviousDirectory.setEnabled(true);

                // Initialize listView with the new parent
                Directory dirTemp = initialiseListView(directories, directory.getPath());
                dir.setChildDirectories(dirTemp.getFolders());
                dir.setChildFiles(dirTemp.getFiles());
                dir.setPath(directory.getPath());
            }
        };

        Button.OnClickListener previousOnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDirectoryName = btnPreviousDirectory.getText().toString();

                // Get the previous directory path
                String[] pathElements = dir.getPath().split("/");
                StringBuilder previousPath = new StringBuilder() ;
                for( int i = 0 ; i <= pathElements.length - 2; i++) {
                    previousPath.append(pathElements[i] + "/");
                }

                // update the interface
                btnPreviousDirectory.setText(pathElements[pathElements.length - 3]);
                txtCurrentDirectoryName.setText(newDirectoryName);
                Directory dirTemp = initialiseListView(directories, previousPath.toString());

                // update directory
                dir.setChildDirectories(dirTemp.getFolders());
                dir.setChildFiles(dirTemp.getFiles());
                dir.setPath(dirTemp.getPath());

                // Prevent the user to go on the root directory
                if(newDirectoryName.equalsIgnoreCase("0")) {
                    btnPreviousDirectory.setEnabled(false);
                }

            }
        };

        Button.OnClickListener chooseOnClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(SearchView.this, MainActivity.class);
                i.putExtra("path", dir.getPath());
                startActivity(i);
            }
        };

        // Initialize Listener
        directories.setOnItemClickListener(directoryOnItemClick);
        btnPreviousDirectory.setOnClickListener(previousOnClickListener);
        btnChoose.setOnClickListener(chooseOnClick);
    }

    private Directory initialiseListView(ListView p_list, String p_path) {
        Directory currentDirectory = getChildrenDirectory(p_path);

        DirectoryArrayAdapter adapter = new DirectoryArrayAdapter (
              this, R.layout.item_list, currentDirectory.getAllChildren());
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
