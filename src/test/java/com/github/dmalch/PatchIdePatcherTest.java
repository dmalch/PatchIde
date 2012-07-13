package com.github.dmalch;

import com.google.common.io.Files;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import static com.google.common.io.Closeables.closeQuietly;
import static org.hamcrest.CoreMatchers.equalTo;
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
        return new File("src/test/resources/file.txt");
    }

    private TFile givenZipFileToPatch() {
        final File originalFile = new File("src/test/resources/file.zip");
        final File fileToPatch = new File("out/test/file_to_patch.zip");
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
