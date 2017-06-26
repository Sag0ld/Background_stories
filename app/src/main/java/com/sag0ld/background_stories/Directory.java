package com.sag0ld.background_stories;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public Boolean hasPreviousDirectory () {
        return !(m_history.isEmpty());
    }

    public File getCurrentDirectory() {
        return m_currentDirectory;
    }

    public File getPreviousDirectory (){
        return m_history.pop();
    }

    public String getPreviousDirectoryName() {
        return m_previousDirectory.getName();
    }

    public void setHistory (File p_directory) {
        // If the new file is not the root Directory we need all his previous directory
        if (!p_directory.equals(Environment.getExternalStorageDirectory())) {
            String tmpCurrentDirectoryPath = p_directory.getPath();
            List<File> previousDirectory = new ArrayList<>();
            do {
                previousDirectory.add(new File(tmpCurrentDirectoryPath));
                tmpCurrentDirectoryPath = tmpCurrentDirectoryPath.substring(0,
                        tmpCurrentDirectoryPath.lastIndexOf('/'));
            }
            while (!tmpCurrentDirectoryPath.equals(Environment.getExternalStorageDirectory().getAbsolutePath()));
            Collections.reverse(previousDirectory);

            for (File directory : previousDirectory) {
                setPreviousDirectory(m_currentDirectory);
                setCurrentDirectory(directory);
            }
        }
    }

    public void setCurrentDirectory(File p_folder) {
        m_currentDirectory = p_folder;
    }

    public void setPreviousDirectory (File p_previousDirectory) {
        m_history.add(p_previousDirectory);
        m_previousDirectory = p_previousDirectory;
    }
    public List<File> getFiles() {

        List<File> directories = new ArrayList<>();
        List<File> files = new ArrayList<>();
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
}