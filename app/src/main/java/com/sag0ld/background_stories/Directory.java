package com.sag0ld.background_stories;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


/**
 * Created by Sagold on 2017-01-08.
 */

public class Directory {

    private File m_currentDirectory;
    private File m_previousDirectory;
    private Stack<File> m_history;
    private Boolean isAccessible;

    public Directory () {
        m_history = new Stack<>();
        //if the storage device is writable and readable, set the current directory to the external storage location.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            m_currentDirectory = Environment.getExternalStorageDirectory();
            isAccessible = true;
        }
        else
            isAccessible = false;

    }
    public Boolean isAccessible () {
        return isAccessible;
    }
    public Boolean hasPreviousDirectory (){
        return m_history.isEmpty();
    }
    public ArrayList<File> getFiles() {

        ArrayList<File> directories = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        for (File inFile : m_currentDirectory.listFiles()) {
            if (inFile.isDirectory()) {
                directories.add(inFile);
            }
            else {
                files.add(inFile);
            }
        }

        //Sort
        Collections.sort(directories);
        Collections.sort(files);

        directories.addAll(files);
        return directories;
    }

    public File getCurrentDirectory() {
        return m_currentDirectory;
    }

    public File getPreviousDirectory (){
        return m_history.pop();
    }

    public void setCurrentDirectory(File p_folder) {
        m_currentDirectory = p_folder;
    }

    public void setPreviousDirectory (File p_previousDirectory) {
        m_history.add(p_previousDirectory);
        m_previousDirectory = p_previousDirectory;
    }
}