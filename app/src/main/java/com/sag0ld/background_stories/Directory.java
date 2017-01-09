package com.sag0ld.background_stories;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Sagold on 2017-01-08.
 */

public class Directory {

    private ArrayList<File> m_childDirectories = new ArrayList<>();
    private ArrayList<File> m_childFiles = new ArrayList<>();
    private String m_path;

    public Directory (ArrayList<File> p_directories, ArrayList<File> p_files, String p_path) {
        m_childDirectories.addAll(p_directories);
        m_childFiles.addAll(p_files);
        m_path = p_path;
    }

    public ArrayList<File> getFolders() {
        return m_childDirectories;
    }

    public ArrayList<File> getFiles() {
        return m_childFiles;
    }

    public int getNbChild() {
        return m_childFiles.size() + m_childDirectories.size();
    }

    public String getPath() {
        return m_path;
    }

    public File findDirectory(String p_name) {
        File fina = null;
        for(File f : m_childDirectories) {
            if(f.getName().equals(p_name))
                fina = f;
        }
        return fina;
    }

    public ArrayList<String> getItemNames () {
        ArrayList<String> names = new ArrayList<String>();
        for(File folder : m_childDirectories)
            names.add(folder.getName());
        for(File folder : m_childFiles)
            names.add(folder.getName());
        return names;
    }

    public void setChildDirectories (ArrayList<File> p_directories) {
        m_childDirectories = p_directories;
    }

    public void setChildFiles (ArrayList<File> p_files) {
        m_childFiles = p_files;
    }

    public void setPath (String p_path) {
        m_path = p_path;
    }
}
