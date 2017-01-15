package com.sag0ld.background_stories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SearchView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        String searchFor = "";

        if (getIntent().hasExtra("BrowseType")) {
           searchFor = getIntent().getStringExtra("BrowseType");
        }

        // Variable
        final ListView directories = (ListView) findViewById(R.id.ListDirectory);
        final TextView txtCurrentDirectoryName = (TextView) findViewById(R.id.txtCurrentDirectory);
        final Button btnPreviousDirectory = (Button) findViewById(R.id.btnPreviousDirectory);
        final Button btnChoose = (Button) findViewById(R.id.btnChoose);
        final Directory dir;
        final Context context = this;

        // Initialize interface
        txtCurrentDirectoryName.setText(Environment.getExternalStorageDirectory().getName());
        btnPreviousDirectory.setText(Environment.getExternalStorageDirectory().getParent());
        btnPreviousDirectory.setEnabled(false);
        dir = initialiseListView(directories, Environment.getExternalStorageDirectory().getAbsolutePath());

        // Listener for ItemClick of the ListView
        ListView.OnItemClickListener directoryOnItemClick = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                File directory = (File) directories.getItemAtPosition(position);

                if (directory.isDirectory()) {
                    String[] parents = directory.getParent().split("/");
                    btnPreviousDirectory.setText(parents[parents.length - 1]);
                    txtCurrentDirectoryName.setText(directory.getName());
                    btnPreviousDirectory.setEnabled(true);

                    // Initialize listView with the new parent
                    Directory dirTemp = initialiseListView(directories, directory.getPath());
                    dir.setChildDirectories(dirTemp.getFolders());
                    dir.setChildFiles(dirTemp.getFiles());
                    dir.setPath(directory.getPath());
                } else {
                    // Pop Up error message
                    CharSequence text = "You have to click on a directory.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        };

        ListView.OnItemClickListener pictureOnItemClick = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                File directory = (File) directories.getItemAtPosition(position);

                if (directory.isDirectory()) {
                    String[] parents = directory.getParent().split("/");
                    btnPreviousDirectory.setText(parents[parents.length - 1]);
                    txtCurrentDirectoryName.setText(directory.getName());
                    btnPreviousDirectory.setEnabled(true);

                    // Initialize listView with the new parent
                    Directory dirTemp = initialiseListView(directories, directory.getPath());
                    dir.setChildDirectories(dirTemp.getFolders());
                    dir.setChildFiles(dirTemp.getFiles());
                    dir.setPath(directory.getPath());
                } else {
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
                Intent i = new Intent();
                i.putExtra("pathFolder", dir.getPath());
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        };

        // Initialize Listener
        if (searchFor.equalsIgnoreCase(MainActivity.BrowseType.Folder.toString()))
            directories.setOnItemClickListener(directoryOnItemClick);
        else
            directories.setOnItemClickListener(pictureOnItemClick);
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

    // Create a Directory object that contain all file and subFolder  (Not recursif)
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
