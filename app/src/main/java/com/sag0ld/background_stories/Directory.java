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

    public String getPath() {
        return m_path;
    }

    public ArrayList<File> getAllChildren() {
        ArrayList<File> allChildren = new ArrayList<>();
        allChildren.addAll(m_childDirectories);
        allChildren.addAll(m_childFiles);
        return allChildren;
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
