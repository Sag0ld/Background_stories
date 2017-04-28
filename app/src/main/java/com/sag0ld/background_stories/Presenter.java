package com.sag0ld.background_stories;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagold on 2017-03-16.
 */

public class Presenter implements LoaderManager.LoaderCallbacks<List<File>> {
    private SearchView mView;
    private Directory m_directory;
    private DirectoryArrayAdapter m_DirectoryArrayAdapter;
    private List<File> m_data; //The list of all files for a specific dir.
    private AsyncTaskLoader<List<File>> mFileLoader; /*Loads the list of files from the model in
    a background thread.*/
    private Button btnPreviousDirectory;
    TextView txtCurrentDirectoryName ;

    public Presenter(SearchView p_View) {
        this.mView = p_View;
        m_directory = new Directory();
        m_data = new ArrayList<>();
        init();
        btnPreviousDirectory = (Button) mView.findViewById(R.id.btnPreviousDirectory);
        txtCurrentDirectoryName = (TextView) mView.findViewById(R.id.txtCurrentDirectory);

        txtCurrentDirectoryName.setText(m_directory.getCurrentDirectory().getName());
        if(m_directory.hasPreviousDirectory()) {
            btnPreviousDirectory.setText(m_directory.getPreviousDirectory().getName());
            btnPreviousDirectory.setEnabled(true);
        }
        else {
            btnPreviousDirectory.setEnabled(false);
            btnPreviousDirectory.setText("/");
        }
    }

    private void init() {
        ArrayList list  = new ArrayList();
        list.addAll(m_data);
        //Instantiate and configure the file adapter with an empty list that our loader will update..
        m_DirectoryArrayAdapter = new DirectoryArrayAdapter(mView,
                R.layout.item_list, list);

        ListView view = (ListView) mView.findViewById(R.id.ListDirectory);
        view.setAdapter(m_DirectoryArrayAdapter);

        /*Start the AsyncTaskLoader that will update the adapter for
        the ListView. We update the adapter in the onLoadFinished() callback.
        */
        mView.getLoaderManager().initLoader(0, null, this);

        //Grab our first list of results from our loader.  onFinishLoad() will call updataAdapter().
        mFileLoader.forceLoad();
    }

    /*Called to update the Adapter with a new list of files when mCurrentDir changes.*/
    private void updateAdapter(List<File> data) {
        //clear the old data.
        m_DirectoryArrayAdapter.clear();
        //add the new data.
        m_DirectoryArrayAdapter.addAll(data);
        //inform the ListView to refrest itself with the new data.
        m_DirectoryArrayAdapter.notifyDataSetChanged();
    }
    public void listItemClicked(ListView l, int position, String searchFor) {
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
            //Let the loader know that our content has changed and we need a new load.
            if (mFileLoader.isStarted()) {
                mFileLoader.onContentChanged();
            }
        } else if (searchFor.equalsIgnoreCase(MainActivity.BrowseType.Folder.toString())){
            // Pop Up error message
            CharSequence text = "You have to click on a directory.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(mView, text, duration);
            toast.show();
        }
        else {
            Intent i = new Intent();
            i.putExtra("pathPicture", fileClicked.getPath());
            mView.setResult(Activity.RESULT_OK, i);
            mView.finish();
        }
    }

    public void previousBtnPressed() {
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
            mFileLoader.onContentChanged();
        }
        else {
            
            btnPreviousDirectory.setEnabled(false);
            btnPreviousDirectory.setText("/");
        }
    }

    public void chooseBtnPressed() {
        Intent i = new Intent();
        i.putExtra("pathFolder", m_directory.getCurrentDirectory().getAbsolutePath());
        mView.setResult(Activity.RESULT_OK, i);
        mView.finish();
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        mFileLoader = new AsyncTaskLoader<List<File>>(mView) {

            //Get our new data load.
            @Override
            public List<File> loadInBackground() {
                return m_directory.getFiles();
            }
        };

        return mFileLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        this.m_data = data;

        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);

    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {

    }
}
