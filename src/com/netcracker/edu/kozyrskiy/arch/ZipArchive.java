package com.netcracker.edu.kozyrskiy.arch;

import java.io.*;
import java.util.Enumeration;
import java.util.StringJoiner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipException;


public class ZipArchive implements Archive {
    private final int zipLevel = 5;

    public void createZipArchive(final String zipArchiveName, final String comment, final String... fileName) {
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipArchiveName));
            zos.setLevel(zipLevel);
            zos.setComment(comment);
            for (String sFile : fileName) {
                addElement(new File(sFile), zos);
            }
            zos.close();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    @Override
    public void addFilesToArchive(final String zipArchiveName, final String... files) throws Exception {
        makeNewArchiveWithParameters(new Special() {
            @Override
            public void doSpecial(ZipOutputStream zos, ZipFile zipArchiveFile, String... args) {
                zos.setComment(zipArchiveFile.getComment());
                for (String sFile : files) {
                    File file = new File(sFile);
                    if (file.exists())
                        addElement(file, zos);
                    else
                        System.out.println("The file " + file.toString() + " does not exist. Check the correctness of input");
                }
            }
        }, zipArchiveName, files);
    }

    @Override
    public void setCommentToArchive(final String zipArchiveName, final String comment) {
        makeNewArchiveWithParameters(new Special() {
            @Override
            public void doSpecial(ZipOutputStream zos, ZipFile zf, String... args) {
                zos.setComment(args[0]);
            }
        }, zipArchiveName, comment);
    }


    //This interface is used in methods where the existing archive has to be updated
    interface Special{
        void doSpecial(ZipOutputStream zos, ZipFile zipArchiveFile, final String... args);
    }

    private void makeNewArchiveWithParameters(Special special, final String zipArchiveName, final String... args) throws IllegalArgumentException {
        try {
            String newArchiveName = "newArchive.zip";
            File zipArchive = new File(zipArchiveName);
            ZipFile zipArchiveFile = getZipArchiveFile(zipArchive);
            File newArchive = new File(newArchiveName);
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(newArchive));
            zos.setLevel(zipLevel);

            writeOldFilesIntoNewArchive(zos, zipArchiveFile);
            special.doSpecial(zos, zipArchiveFile, args);
            close(zos, zipArchiveFile);

            if(!zipArchive.delete())
                System.out.println("Old zip archive was not deleted now");
            if(!newArchive.renameTo(zipArchive))
                System.out.println("Zip archive was not renamed now");
        } catch (FileNotFoundException fnf){
            fnf.printStackTrace();
        }
    }

    private ZipFile getZipArchiveFile (File zipArchive){
        try {
            return new ZipFile(zipArchive);

        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }

    private ZipFile getZipArchiveFile (String zipArchiveName){
        try {
            return new ZipFile(zipArchiveName);

        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }

    private void close (ZipOutputStream zos, ZipFile zipArchiveFile){
        try {
            zipArchiveFile.close();
            zos.close();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void close (ZipFile zipArchiveFile){
        try{
            zipArchiveFile.close();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }


    @Override
    public void extractFromZipArchive(final String zipArchiveName, final String directory) {
        File outputDirectory = new File(directory);
        extract(zipArchiveName, outputDirectory);
    }

    @Override
    public void extractFromZipArchive(final String zipArchiveName) {
        File outputDirectory = new File(zipArchiveName.replace(".zip", ""));
        extract(zipArchiveName, outputDirectory);
    }

    private void extract(final String zipArchiveName, File outputDirectory) {
        if (!outputDirectory.mkdir())
            System.out.println("Output directory was not made now or it had been made earlier");

        ZipFile zipFile = getZipArchiveFile(zipArchiveName);
        extractFromZipFileToDirectory(zipFile, outputDirectory);
        close(zipFile);
    }

    @Override
    public String readCommentFromArchive(final String zipArchiveName) {
        try {
            ZipFile zipFile = new ZipFile(zipArchiveName);
            String comment = zipFile.getComment();
            zipFile.close();
            return comment;
        } catch (IOException io){
            System.out.println("Cannot read zip archive. Exception: " + io.toString());
            return null;
        } catch (Exception e){
            System.out.println("Cannot perform action. Exception: " + e.toString());
            return null;
        }
    }

    private void addElement(File file, ZipOutputStream zos) {
        try {
            // Checking whether the file exists
            // without throwing FileNotFoundException when method writeIntoArchive(f, zos) is called
            // and not to interrupt adding elements
            if (file.exists()) {
                //pack directories recursively
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        if (f.isDirectory())
                            addElement(f, zos);
                        else
                            writeElementIntoArchive(f, zos);
                    }
                } else
                    writeElementIntoArchive(file, zos);
            } else {
                System.out.println("The file " + file.toString() + " does not exist. It was not added to archive");
            }
        } catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }


    private void writeElementIntoArchive(File file, ZipOutputStream zos) {
        try {
            ZipEntry ze = new ZipEntry(file.getPath());
            zos.putNextEntry(ze);
            FileInputStream fis = new FileInputStream(file);
            writeFromInputToOutput(fis, zos);
            zos.closeEntry();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void writeOldFilesIntoNewArchive(ZipOutputStream zos, ZipFile zipArchiveFile) {
        try {
            Enumeration elements = zipArchiveFile.entries();
            while (elements.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) elements.nextElement();
                zos.putNextEntry(ze);
                writeFromInputToOutput(zipArchiveFile.getInputStream(ze), zos);
                zos.closeEntry();
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void extractFromZipFileToDirectory(ZipFile zipFile, File outputDirectory) {
        try {
            Enumeration elements = zipFile.entries();
            while (elements.hasMoreElements()) {
                ZipEntry nextEntry = (ZipEntry) elements.nextElement();
                File nextElement = new File(outputDirectory, nextEntry.getName());

                // to build the hierarchy of directories
                if (!nextElement.getParentFile().mkdirs())
                    System.out.println("Directories weren't made or have been made earlier");

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(nextElement));
                writeFromInputToOutput(zipFile.getInputStream(nextEntry), bos);
                bos.close();
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void writeFromInputToOutput(InputStream inputStream, OutputStream outputStream) {
        try {
            byte[] buf = new byte[8000];
            int length;
            while (true) {
                length = inputStream.read(buf);
                if (length < 0)
                    break;
                outputStream.write(buf, 0, length);
            }
            inputStream.close();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}