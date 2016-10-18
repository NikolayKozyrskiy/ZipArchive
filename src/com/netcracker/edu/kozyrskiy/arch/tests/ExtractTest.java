package com.netcracker.edu.kozyrskiy.arch.tests;


import com.netcracker.edu.kozyrskiy.arch.Archive;
import com.netcracker.edu.kozyrskiy.arch.ZipArchive;

import java.io.IOException;

/**
 * The arguments are:
 *                  args[0]: name of zip archive
 *                  args[1]: name of directory to extract
 */

public class ExtractTest {

    public static void main(String[] args) {
        Archive arc = new ZipArchive();


        try {
            //trying to extract files from archive to the given directory
            arc.extractFromZipArchive(args[0], args[1]);

            //trying to extract files from archive to the default directory
            arc.extractFromZipArchive(args[0]);
        } catch (IOException ioe) {
            System.out.println("Cannot read file. Exception: " + ioe.getMessage());
        }
    }
}