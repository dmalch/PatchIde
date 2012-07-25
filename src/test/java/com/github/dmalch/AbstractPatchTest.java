package com.github.dmalch;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;

import java.io.File;
import java.io.IOException;

import static com.google.common.io.Closeables.closeQuietly;
import static com.intellij.openapi.application.PathManager.getLibPath;
import static java.text.MessageFormat.format;

public class AbstractPatchTest {
    protected TFile givenPatchFile(final String copiedDirName) {
        final File originalFile = new File("src/test/resources/file.txt");
        final File copiedDir = new File(format("{0}/{1}", getLibPath(), copiedDirName));
        final TFile copiedFile = new TFile(copiedDir, "file.txt");
        try {
            copiedDir.mkdirs();
            Files.copy(originalFile, copiedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return copiedFile;
    }

    protected byte[] readFileContent(final File file) {
        final byte[] fileContent;
        TFileInputStream inputStream = null;
        try {
            inputStream = new TFileInputStream(file);
            fileContent = ByteStreams.toByteArray(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(inputStream);
        }
        return fileContent;
    }
}
