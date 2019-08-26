package com.mycompany.app;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private File file1 = new File("./src/test/resources/MKI_Alajärvi.zip");
    private File file2 = new File("./src/test/resources/MKI_Espoo.zip");
    private File file3 = new File("./src/test/resources/MKI_Eura.zip");
    private List<File> files = Arrays.asList(file1, file2, file3);
    private Map<File, Long> expectedSize = new LinkedHashMap<>();
    {
        // sized for the files inside the zip files
        // checked manually with unzip binary in MacOS/linux
        expectedSize.put(file1, 23621632L);
        expectedSize.put(file2, 2097152L);
        expectedSize.put(file3, 13195264L);
    }

    @Test
    public void testFilesExist() {
        for(File file: files) {
            assertTrue( file.exists());
        }
    }

    @Test
    public void testFile1() throws IOException {
        testFile(file1, 1024);
    }

    @Test
    public void testFile2() throws IOException {
        testFile(file2, 1024);
    }

    @Test
    public void testFile3() throws IOException {
        testFile(file3, 1024);
    }

    private void testFile(File file, int bufferSize) throws IOException {
        File root = new File("./target");
        root.mkdirs();
        System.out.println("Extracting file " + file.getAbsolutePath());
        File targetDir = Files.createTempDirectory(root.toPath(), "unzip").toFile();
        unzip(file, targetDir, expectedSize.get(file), bufferSize);
    }

    private File unzip(File file, File targetDir, long expectedSize, int bufferSize) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration<ZipArchiveEntry> entryEnum = zipFile.getEntries();
        int entryCount = 0;
        File resultFile = null;
        while(entryEnum.hasMoreElements()) {
            entryCount ++;
            assertTrue(entryCount == 1);

            ZipArchiveEntry entry = entryEnum.nextElement();
            assertEquals(expectedSize, entry.getSize());
            File targetFile = new File(targetDir, entry.getName());
            assertFalse(targetFile.exists());

            try(FileOutputStream fos = new FileOutputStream(targetFile);
                InputStream is = zipFile.getInputStream(entry)) {
                System.out.println("InputStream impl:" + is.getClass().getCanonicalName());
                copy(is, fos, bufferSize);
            }
            assertEquals(expectedSize, targetFile.length());
            resultFile = targetFile;
        }
        return resultFile;
    }

    private void copy(InputStream is, OutputStream os, int bufferSize) throws IOException {
        byte[] buf = new byte[bufferSize];
        while(true) {
            int size = is.read(buf);

            /**
             * From javadoc:
             * Reads some number of bytes from the input stream and stores them into the buffer array b. The number of bytes actually read is returned as an integer. This method blocks until input data is available, end of file is detected, or an exception is thrown.
             * If the length of b is zero, then no bytes are read and 0 is returned; otherwise, there is an attempt to read at least one byte. If no byte is available because the stream is at the end of the file, the value -1 is returned; otherwise, at least one byte is read and stored into b.
             */
            Assertions.assertTrue(bufferSize > 0 && size != 0, "read can return 0 only when length of buf is 0");
            if (size < 0) {
                break;
            }
            os.write(buf, 0, size);
        }
    }
}
