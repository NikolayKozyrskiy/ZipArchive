package com.netcracker.edu.kozyrskiy.arch;


import java.io.IOException;

public interface Archive {
    /**
     * Method creates a new zip archive and fills it with files
     * If a file does not exist, it will not be added and a message will be printed
     *
     * @param zipArchiveName Name of created zip archive
     * @param fileName       Name of added files or directories
     * @throws Exception
     */
    void createZipArchive(String zipArchiveName, String... fileName) throws Exception;

    /**
     * Method creates a new zip archive with comment and fills it with files
     * If a file does not exist, it will not be added and a message will be printed
     *
     * @param zipArchiveName Name of created zip archive
     * @param comment        Comment to archive
     * @param fileName       Name of added files or directories
     * @throws Exception
     */
    void createZipArchiveWithComment(String zipArchiveName, String comment, String... fileName) throws Exception;

    /**
     * Method adds files into the existing zip archive
     * If a file does not exist, it will be ignored and a message will be printed
     *
     * @param zipArchiveName Name of zip archive the {@param files} are added to
     * @param files          Files to add
     */
    void addFilesToArchive(String zipArchiveName, String... files);

    /**
     * Method sets comment to existing zip archive
     *
     * @param zipArchiveName Name of the zip archive the {@param comment}  is added to
     * @param comment Comment to add
     */
    void setCommentToArchive(String zipArchiveName, String comment);

    /**
     * Method writes all elements from zip archive into the directory.
     * The directory is created if it does't exist
     *
     * @param zipArchiveName Name of the zip archive the files are taken from
     * @param directory Name of the directory the files will be written into.
     *                  Created if does not exist
     * @throws IOException
     */
    void extractFromZipArchive(String zipArchiveName, String directory) throws IOException;

    /**
     * Method writes all elements from the zip archive into the directory named as the zip archive
     *
     * @param zipArchiveName Name of the zip archive the files are taken from
     * @throws IOException
     */
    void extractFromZipArchive(String zipArchiveName) throws IOException;

    /**
     * Method reads a comment from the zip archive
     *
     * @param zipArchiveName  Name of archive the comment is read from
     * @return Comment (null if no comment is set)
     */
    String readCommentFromArchive(String zipArchiveName);
}