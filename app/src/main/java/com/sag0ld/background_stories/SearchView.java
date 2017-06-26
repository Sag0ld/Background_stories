package com.sag0ld.background_stories;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class SearchView extends Activity {

    private Directory m_directory = new Directory();
    private DirectoryArrayAdapter m_DirectoryArrayAdapter;
    private SharedPreferences settings;
    private Button btnPreviousDirectory;
    private TextView txtCurrentDirectoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        // Variable
        final ListView ListDirectoriesFiles = (ListView) findViewById(R.id.ListDirectory);
        btnPreviousDirectory = (Button) findViewById(R.id.btnPreviousDirectory);
        final Button btnChoose = (Button) findViewById(R.id.btnChoose);
        txtCurrentDirectoryName = (TextView) findViewById(R.id.txtCurrentDirectory);

        //If you browse for a directory or a file
        final String searchFor = getIntent().getStringExtra("BrowseType");

        // Init
        if(!m_directory.isAccessible()) {
            Toast.makeText(this, "You need to enable your internal file.", Toast.LENGTH_LONG);
            this.finish();
        } else {
            // If preferrenceSetting does exist, restore them else its going to be created
            settings = getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE);

            File directory = new File(settings.getString(getString(R.string.saved_path_directory),
                    getString(R.string.saved_path_directory_default)));

            if (!directory.getName().equals(""))
                m_directory.setHistory(directory);
            }

        m_DirectoryArrayAdapter = new DirectoryArrayAdapter(this,
                R.layout.item_list, m_directory.getFiles());
        ListDirectoriesFiles.setAdapter(m_DirectoryArrayAdapter);
        updateView();

        // Listener for ItemClick of the ListView
        ListView.OnItemClickListener FileOnItemClick = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                System.out.println("Onclick Item call");

                // The selected file
                File fileClicked = m_DirectoryArrayAdapter.getItem(position);

                if (fileClicked.isDirectory()) {
                    m_directory.setPreviousDirectory(m_directory.getCurrentDirectory());
                    m_directory.setCurrentDirectory(fileClicked);

                    updateListView(m_directory);
                    updateView();

                } else if (searchFor.equals(MainActivity.BrowseType.Folder.toString())) {
                    // Pop Up error message
                    CharSequence text = "You have to click on a directory.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(SearchView.this, text, duration);
                    toast.show();
                }
                // Here we search for file
                else {
                    Intent i = new Intent();
                    i.putExtra(SearchView.this.getString(R.string.intent_extra_wallpaper),
                            fileClicked.getPath());
                    SearchView.this.setResult(Activity.RESULT_OK, i);
                    SearchView.this.finish();
                }
            }
        };

        Button.OnClickListener previousOnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If we have a previous dir to go back to, do it.
                if (m_directory.hasPreviousDirectory()) {
                    File previous = m_directory.getPreviousDirectory();
                    m_directory.setCurrentDirectory(previous);


                    // Our content has changed, so we need a new load.
                    updateListView(m_directory);
                    updateView();
                }
            }
        };

        Button.OnClickListener chooseOnClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchFor.equals(MainActivity.BrowseType.Folder.toString())) {
                    Intent i = new Intent();
                    i.putExtra(SearchView.this.getString(R.string.intent_extra_directory),
                            m_directory.getCurrentDirectory().getAbsolutePath());
                    SearchView.this.setResult(Activity.RESULT_OK, i);
                    SearchView.this.finish();
                }
                else  {
                    CharSequence text = "You can't choose a directory.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(SearchView.this, text, duration);
                    toast.show();
                }
            }
        };

        // Initialize Listener
        ListDirectoriesFiles.setOnItemClickListener(FileOnItemClick);
        btnPreviousDirectory.setOnClickListener(previousOnClickListener);
        btnChoose.setOnClickListener(chooseOnClick);
    }

    private void updateListView(Directory p_directory) {
        System.out.println("update view");
        //clear the old data.
        m_DirectoryArrayAdapter.clear();
        //add the new data.
        m_DirectoryArrayAdapter.addAll(p_directory.getFiles());
    }

    private void updateView() {
        if (!m_directory.hasPreviousDirectory()) {
            btnPreviousDirectory.setEnabled(false);
            btnPreviousDirectory.setText(".");
        }
        else {
            btnPreviousDirectory.setText(m_directory.getPreviousDirectoryName());
            btnPreviousDirectory.setEnabled(true);
        }

        txtCurrentDirectoryName.setText(m_directory.getCurrentDirectory().getName());
    }
}
