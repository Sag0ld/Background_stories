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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;

import static com.sag0ld.background_stories.R.id.btnPreviousDirectory;

public class SearchView extends Activity {

    final private Directory m_directory = new Directory();
    private DirectoryArrayAdapter m_DirectoryArrayAdapter;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        // Variable
        final ListView ListDirectoriesFiles = (ListView) findViewById(R.id.ListDirectory);
        final Button btnPreviousDirectory = (Button) findViewById(R.id.btnPreviousDirectory);
        final Button btnChoose = (Button) findViewById(R.id.btnChoose);
        final TextView txtCurrentDirectoryName = (TextView) findViewById(R.id.txtCurrentDirectory);

        // Init
        if(!m_directory.isAccessible()) {
            Toast.makeText(this, "You need to enable your internal file.", Toast.LENGTH_LONG);
            this.finish();
        } else {
            // If preferrenceSetting does exist, restore them else its going to be created
            settings = getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE);
            File file = new File(settings.getString(getString(R.string.saved_path_directory),
                    getString(R.string.saved_path_directory_default)));
            if (!file.getName().equals(""))
                m_directory.setCurrentDirectory(file);
        }

        m_DirectoryArrayAdapter = new DirectoryArrayAdapter(this,
                R.layout.item_list, m_directory.getFiles());
        ListDirectoriesFiles.setAdapter(m_DirectoryArrayAdapter);

        // Init View
        if(!m_directory.hasPreviousDirectory())
            btnPreviousDirectory.setClickable(false);

        txtCurrentDirectoryName.setText(m_directory.getCurrentDirectory().getName());
        /*ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this).
                build();
        ImageLoader.getInstance().init(config);*/

        // Listener for ItemClick of the ListView
        ListView.OnItemClickListener directoryOnItemClick = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                System.out.append("Onclick Item call");
                String searchFor = "";
                if(getIntent().hasExtra("browseType"))
                    searchFor = getIntent().getStringExtra("BrowseType");

                //The file we clicked based on row position where we clicked.  I could probably word that better. :)
                File fileClicked = m_DirectoryArrayAdapter.getItem(position);

                if (fileClicked.isDirectory()) {
                    //we are changing dirs, so save the previous dir as the one we are currently in.
                    btnPreviousDirectory.setText(m_directory.getCurrentDirectory().getName());
                    m_directory.setPreviousDirectory(m_directory.getCurrentDirectory());
                    //set the current dir to the dir we clicked in the listview.
                    m_directory.setCurrentDirectory(fileClicked);

                    //update interface
                    txtCurrentDirectoryName.setText(m_directory.getCurrentDirectory().getName());
                    btnPreviousDirectory.setEnabled(true);
                    DirectoryArrayAdapter adapter =
                            new DirectoryArrayAdapter (SearchView.this, R.layout.item_list,
                                    m_directory.getFiles());

                } else if (searchFor.equalsIgnoreCase(MainActivity.BrowseType.Folder.toString())){
                    // Pop Up error message
                    CharSequence text = "You have to click on a directory.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(SearchView.this, text, duration);
                    toast.show();
                }
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
                System.out.append("Onclick previous button call");
                //If we have a previous dir to go back to, do it.
                if (m_directory.hasPreviousDirectory()) {
                    File previous = m_directory.getPreviousDirectory();
                    m_directory.setCurrentDirectory(previous);

                    if(!m_directory.hasPreviousDirectory()) {
                        btnPreviousDirectory.setEnabled(false);
                        btnPreviousDirectory.setText("/");
                    } else {
                        btnPreviousDirectory.setText(m_directory.getPreviousDirectory().getName());
                        btnPreviousDirectory.setEnabled(true);
                    }
                    txtCurrentDirectoryName.setText(m_directory.getCurrentDirectory().getName());

                    //Our content has changed, so we need a new load.
                    updateListView();
                }
                else {

                    btnPreviousDirectory.setEnabled(false);
                    btnPreviousDirectory.setText("/");
                };
            }
        };

        Button.OnClickListener chooseOnClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.append("Onclick choose call");
                Intent i = new Intent();
                i.putExtra(SearchView.this.getString(R.string.intent_extra_directory),
                        m_directory.getCurrentDirectory().getAbsolutePath());
                SearchView.this.setResult(Activity.RESULT_OK, i);
                SearchView.this.finish();
            }
        };

        // Initialize Listener
        ListDirectoriesFiles.setOnItemClickListener(directoryOnItemClick);
        btnPreviousDirectory.setOnClickListener(previousOnClickListener);
        btnChoose.setOnClickListener(chooseOnClick);
    }

    private void updateListView() {
        //clear the old data.
        m_DirectoryArrayAdapter.clear();
        //add the new data.
        m_DirectoryArrayAdapter.addAll(m_directory.getPreviousDirectory().listFiles());

    }
}
