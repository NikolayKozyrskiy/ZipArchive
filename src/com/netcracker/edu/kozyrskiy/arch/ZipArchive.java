package com.netcracker.edu.kozyrskiy.arch;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;


public class ZipArchive implements Archive {

    @Override
    public void createZipArchive(String zipArchiveName, String... fileName) throws Exception {
        createZipArchive(zipArchiveName, null, fileName);
    }

    @Override
    public void createZipArchiveWithComment(String zipArchiveName, String comment, String... fileName) throws Exception {
        createZipArchive(zipArchiveName, comment, fileName);
    }

    private void createZipArchive(String zipArchiveName, String comment, String... fileName) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipArchiveName));
        zos.setLevel(5);
        zos.setComment(comment);
        for (String sFile : fileName) {
            addElement(new File(sFile), zos);
        }
        zos.close();
    }

    @Override
    public void addFilesToArchive(String zipArchiveName, String... files) {
        try {
            makeNewArchiveWithParameters(new Special() {
                @Override
                public void doSpecial(ZipOutputStream zos, ZipFile zipArchiveFile, String... args) {
                    zos.setComment(zipArchiveFile.getComment());
                    for (String sFile: files) {
                        File file = new File(sFile);
                        if (file.exists())
                            addElement(file, zos);
                        else
                            System.out.println("The file " + file.toString() + " does not exist. Check the correctness of input");
                    }
                }
            }, zipArchiveName, files);

        } catch (IOException ioe){
            System.out.println("Cannot find archive. Exception: " + ioe.toString());
        } catch (Exception e){
            System.out.println("Cannot perform action. Exception: " + e.toString());
        }
    }

    @Override
    public void setCommentToArchive(String zipArchiveName, String comment) {
        try {
            makeNewArchiveWithParameters(new Special() {
                @Override
                public void doSpecial(ZipOutputStream zos, ZipFile zf, String... args) {
                    zos.setComment(args[0]);
                }
            }, zipArchiveName, comment);
        } catch (IOException ioe){
            System.out.println("Cannot find archive. Exception: " + ioe.toString());
        } catch (Exception e){
            System.out.println("Cannot perform action. Exception: " + e.toString());
        }
    }


    //This interface is used in methods where the existing archive has to be updated
    interface Special{
        void doSpecial(ZipOutputStream zos, ZipFile zipArchiveFile, String... args);
    }

    private void makeNewArchiveWithParameters(Special special, String zipArchiveName, String... args) throws Exception{
        File zipArchive = new File(zipArchiveName);
        ZipFile zipArchiveFile = new ZipFile(zipArchive);
        File newArchive = new File("newArchive.zip");
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(newArchive));
        zos.setLevel(5);

        writeOldFilesIntoNewArchive(zipArchiveFile, zos);
        special.doSpecial(zos, zipArchiveFile, args);

        zipArchiveFile.close();
        zos.close();
        zipArchive.delete();
        newArchive.renameTo(zipArchive);
    }


    @Override
    public void extractFromZipArchive(String zipArchiveName, String directory) throws IOException {
        File outputDirectory = new File(directory);
        extract(zipArchiveName, outputDirectory);
    }

    @Override
    public void extractFromZipArchive(String zipArchiveName) throws IOException {
        File outputDirectory = new File(zipArchiveName.replace(".zip", ""));
        extract(zipArchiveName, outputDirectory);
    }

    private void extract(String zipArchiveName, File outputDirectory) throws IOException{
        outputDirectory.mkdir();
        ZipFile zipFile = new ZipFile(zipArchiveName);

        extractFromZipFileToDirectory(zipFile, outputDirectory);

        zipFile.close();
    }

    @Override
    public String readCommentFromArchive(String zipArchiveName) {
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
            if (file.exists()){
                //pack directories recursively
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        if (f.isDirectory())
                            addElement(f, zos);
                        else
                            writeElementIntoArchive(f, zos);
                    }
                }
                else
                    writeElementIntoArchive(file, zos);
            }
            else {
                System.out.println("The file " + file.toString() + " does not exist. It was not added to archive");
            }

        } catch (ZipException ze) {
            System.out.println("The file " + file.toString() + " is already in archive. It will not be added");
        } catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }


    private void writeElementIntoArchive(File file, ZipOutputStream zos) throws IOException {
        ZipEntry ze = new ZipEntry(file.getPath());
        zos.putNextEntry(ze);
        FileInputStream fis = new FileInputStream(file);
        writeFromInputToOutput(fis, zos);
        zos.closeEntry();
    }

    private void writeOldFilesIntoNewArchive(ZipFile zipArchiveFile, ZipOutputStream zos) throws IOException{
        Enumeration elements = zipArchiveFile.entries();
        while (elements.hasMoreElements()){
            ZipEntry ze = (ZipEntry) elements.nextElement();
            zos.putNextEntry(ze);
            writeFromInputToOutput(zipArchiveFile.getInputStream(ze), zos);
            zos.closeEntry();
        }
    }

    private void extractFromZipFileToDirectory(ZipFile zipFile, File outputDirectory) throws IOException {
        Enumeration elements = zipFile.entries();
        while (elements.hasMoreElements()) {
            ZipEntry nextEntry = (ZipEntry) elements.nextElement();
            File nextElement = new File(outputDirectory, nextEntry.getName());

            // to build the hierarchy of directories
            nextElement.getParentFile().mkdirs();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(nextElement));
            writeFromInputToOutput(zipFile.getInputStream(nextEntry), bos);
            bos.close();
        }
    }

    private void writeFromInputToOutput(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buf = new byte[8000];
        int length;
        while (true){
            length = inputStream.read(buf);
            if (length < 0)
                break;
            outputStream.write(buf, 0, length);
        }
        inputStream.close();
    }
}