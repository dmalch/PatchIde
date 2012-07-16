package com.github.dmalch;

import com.google.common.io.Files;
import de.schlichtherle.truezip.file.TFileReader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import static com.google.common.io.Closeables.closeQuietly;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;

public class AbstractPatchTest {
    protected File givenPatchFile(final String copiedDirName) {
        final File originalFile = new File("src/test/resources/file.txt");
        final File copiedDir = new File(format("out/test/{0}", copiedDirName));
        final File copiedFile = new File(copiedDir, format("file.txt", randomAlphanumeric(5)));
        try {
            copiedDir.mkdirs();
            Files.copy(originalFile, copiedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return copiedFile;
    }

    protected String readFileContent(final File file) {
        final String fileContent;
        Reader reader = null;
        try {
            reader = new TFileReader(file);
            fileContent = IOUtils.toString(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(reader);
        }
        return fileContent;
    }
}
