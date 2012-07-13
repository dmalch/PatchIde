package com.github.dmalch;

import com.google.common.io.Files;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import static com.google.common.io.Closeables.closeQuietly;
import static java.text.MessageFormat.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PatchIdePatcherTest {
    @Test
    public void testApplyPatch() throws Exception {
        final File zipFileToPatch = givenZipFileToPatch();
        final File expectedFile = givenExpectedFile();
        final PatchIdePatcher patcher = givenPatcher();

        whenApplyPatch(patcher);

        thenPatchIsApplied(zipFileToPatch, expectedFile);
    }

    private void thenPatchIsApplied(final File zipFileToPatch, final File expectedFile) {

        final String expectedFileValue = readFileContent(expectedFile);
        final String fileToPatchValue = readFileContent(zipFileToPatch);

        assertThat(fileToPatchValue, equalTo(expectedFileValue));
    }

    private String readFileContent(final File file) {
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

    private File givenExpectedFile() {
        final File originalFile = new File("src/test/resources/file.txt");
        final File copiedDir = new File(format("out/test/{0}", RandomStringUtils.randomAlphanumeric(5)));
        final File copiedFile = new File(copiedDir, format("file.txt", RandomStringUtils.randomAlphanumeric(5)));
        try {
            final boolean mkdirs = copiedDir.mkdirs();
            assertThat(mkdirs, is(true));
            Files.copy(originalFile, copiedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return copiedFile;
    }

    private TFile givenZipFileToPatch() {
        final File originalFile = new File("src/test/resources/file.zip");
        final File fileToPatch = new File(format("out/test/file_to_patch{0}.zip", RandomStringUtils.randomAlphanumeric(5)));
        try {
            Files.copy(originalFile, fileToPatch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new TFile(fileToPatch, "file.txt");
    }

    private PatchIdePatcher givenPatcher() {
        return new PatchIdePatcherImpl();
    }

    private void whenApplyPatch(final PatchIdePatcher patcher) {
        patcher.applyPatch();
    }
}
