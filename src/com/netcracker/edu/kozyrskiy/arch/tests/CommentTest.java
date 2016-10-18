package com.netcracker.edu.kozyrskiy.arch.tests;


import com.netcracker.edu.kozyrskiy.arch.Archive;
import com.netcracker.edu.kozyrskiy.arch.ZipArchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The arguments are:
 *                  args[0]: name of zip archive
 */


public class CommentTest {
    public static void main(String[] args) {
        Archive arc = new ZipArchive();

        //reading the comment from archive
        //prints null if no comment set
        System.out.println(arc.readCommentFromArchive(args[0]));

        //setting a comment to archive
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the comment you would like to set: ");
        try{
            arc.setCommentToArchive(args[0], reader.readLine());
            System.out.println("Comment set: " + arc.readCommentFromArchive(args[0]));
        } catch (IOException e){
            System.out.println("Something wrong with reader: " + e.toString());
        }
    }
}