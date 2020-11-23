package controller;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class HTMLFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        boolean answer = false;
        if(f != null) {
            if(f.isDirectory()){
                answer = true;
            }
            else {
                String fileName = f.getName();
                int dotIndex = fileName.lastIndexOf(".");
                String endFileName = fileName.substring(dotIndex + 1);
                if(endFileName.equalsIgnoreCase("html") || endFileName.equalsIgnoreCase("htm")) {
                    answer = true;
                }
            }
        }
        return answer;
    }

    @Override
    public String getDescription() {
        return "HTML и HTM файлы";
    }
}
