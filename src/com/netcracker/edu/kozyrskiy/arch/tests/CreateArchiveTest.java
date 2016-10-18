package com.netcracker.edu.kozyrskiy.arch.tests;

import com.netcracker.edu.kozyrskiy.arch.Archive;
import com.netcracker.edu.kozyrskiy.arch.ZipArchive;

import java.io.IOException;

/**
 * The arguments are:
 *                  args[0]: name of zip archive
 *                  args[1]: initial comment to archive
 *                  args[i]: names of files to add in archive (i = 2, 3, 4, 5)
 */

public class CreateArchiveTest {
    public static void main(String[] args) {
        Archive arc = new ZipArchive();

        //trying to create a new archive without comment
        try {
            arc.createZipArchive(args[0], args[2], args[3], args[4], args[5], args[6]);
            System.out.println("The archive without comment has been created successfully");

            String comment = String.valueOf(args[1]);
            //trying to create a new archive with comment
            arc.createZipArchiveWithComment(args[0], comment, args[2], args[3], args[4], args[5], args[6]);
            System.out.println("The archive with a comment has been created successfully");

        } catch (IOException ioe) {
            System.out.println("Cannot read file. Exception: " + ioe.toString());
        } catch (Exception e){
            System.out.println("Cannot create archive. Exception: " + e.toString());
        }
    }
}