package com.netcracker.edu.kozyrskiy.arch;


import java.io.File;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

class UpdateArchive {
    private String[] files;
    private String comment;
    private ZipOutputStream zos;

    UpdateArchive (ZipOutputStream zos, String... files){
        int sizeOfArray = files.length;
        if (sizeOfArray > 1) {
            this.files = new String[sizeOfArray];
            System.arraycopy(files, 0, this.files, 0, sizeOfArray);
        }
        else
            comment = files[0];
        this.zos = zos;
    }

    void addFiles (ZipFile zipArchiveFile, ZipArchive archive){
        zos.setComment(zipArchiveFile.getComment());
        for (String sFile : files) {
            File file = new File(sFile);
            if (file.exists())
                archive.addElement(file, zos);
            else
                System.out.println("The file " + file.toString() + " does not exist. Check the correctness of input");
        }
    }

    void updateComment (){
        zos.setComment(comment);
    }
}