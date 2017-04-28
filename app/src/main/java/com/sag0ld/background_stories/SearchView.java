package com.sag0ld.background_stories;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class SearchView extends Activity {

    private Presenter presenter;

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
        final Button btnPreviousDirectory = (Button) findViewById(R.id.btnPreviousDirectory);
        final Button btnChoose = (Button) findViewById(R.id.btnChoose);

        final Context context = this;
        setPresenter(new Presenter(this));

        // Listener for ItemClick of the ListView
        ListView.OnItemClickListener directoryOnItemClick = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                presenter.listItemClicked(directories, position, searchFor);
            }
        };

        Button.OnClickListener previousOnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.previousBtnPressed();
            }
        };

        Button.OnClickListener chooseOnClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
               presenter.chooseBtnPressed();
            }
        };

        // Initialize Listener
        directories.setOnItemClickListener(directoryOnItemClick);
        btnPreviousDirectory.setOnClickListener(previousOnClickListener);
        btnChoose.setOnClickListener(chooseOnClick);
    }

    public void setPresenter(Presenter p) {
        presenter = p;
    }
}
