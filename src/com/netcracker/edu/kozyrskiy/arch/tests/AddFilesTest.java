package com.netcracker.edu.kozyrskiy.arch.tests;


import com.netcracker.edu.kozyrskiy.arch.Archive;
import com.netcracker.edu.kozyrskiy.arch.ZipArchive;

/**
 * The arguments are:
 *                  args[0]: name of zip archive
 *                  args[i]: name of files the archive does not contain (i = 1, 2)
 */

public class AddFilesTest {
    public static void main(String[] args) {
        Archive arc = new ZipArchive();

        //adding files to the existing archive
        arc.addFilesToArchive(args[0], args[1], args[2]);

        //adding the file which is already in archive
        arc.addFilesToArchive(args[0], args[1]);
    }
}