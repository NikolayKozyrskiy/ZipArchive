package com.netcracker.edu.kozyrskiy.arch;


import java.io.File;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class UpdateArchive {
    private String[] files;
    private String comment;
    private ZipOutputStream zos;

    UpdateArchive (ZipOutputStream zos, String... files){
        int sizeOfArray = files.length;
        this.files = new String[sizeOfArray];
        System.arraycopy(files, 0, this.files, 0, sizeOfArray);
        this.zos = zos;
    }

    UpdateArchive (ZipOutputStream zos, String comment){
        this.comment = comment;
        this.zos = zos;
    }

    void update (ZipFile zipArchiveFile, ZipArchive archive){
        zos.setComment(zipArchiveFile.getComment());
        for (String sFile : files) {
            File file = new File(sFile);
            if (file.exists())
                archive.addElement(file, zos);
            else
                System.out.println("The file " + file.toString() + " does not exist. Check the correctness of input");
        }
    }

    void update (){
        zos.setComment(comment);
    }
}