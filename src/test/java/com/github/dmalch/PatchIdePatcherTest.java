package com.github.dmalch;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import static com.google.common.io.Closeables.closeQuietly;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PatchIdePatcherTest {
    @Test
    public void testApplyPatchAtTheSpecifiedDirectory() throws Exception {
        final String expectedDir = givenExpectedDir();
        final File expectedFile = givenExpectedFile(expectedDir);
        final TFile zipFileToPatch = givenZipFileToPatch(expectedDir);
        final PatchIdePatcher patcher = givenPatcherFor(expectedFile, zipFileToPatch, expectedDir);

        whenApplyPatch(patcher);

        thenPatchIsApplied(zipFileToPatch, expectedFile);
    }

    private String givenExpectedDir() {
        return randomAlphanumeric(5);
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

    private File givenExpectedFile(final String copiedDirName) {
        final File originalFile = new File("src/test/resources/file.txt");
        final File copiedDir = new File(format("out/test/{0}", copiedDirName));
        final File copiedFile = new File(copiedDir, format("file.txt", randomAlphanumeric(5)));
        try {
            final boolean mkdirs = copiedDir.mkdirs();
            assertThat(mkdirs, is(true));
            Files.copy(originalFile, copiedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return copiedFile;
    }

    private TFile givenZipFileToPatch(final String expectedDir) {
        final File originalFile = new File("src/test/resources/file.zip");
        final File fileToPatch = new File(format("out/test/file_to_patch{0}.zip", randomAlphanumeric(5)));
        try {
            Files.copy(originalFile, fileToPatch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new TFile(fileToPatch, format("{0}/file.txt", expectedDir));
    }

    private PatchIdePatcher givenPatcherFor(final File expectedFile, final TFile zipFileToPatch, final String innerDirectory) {
        final PatchIdePatcherImpl patchIdePatcher = new PatchIdePatcherImpl();
        patchIdePatcher.setFilesToPatch(ImmutableMap.of(expectedFile.getAbsolutePath(), patchTarget(zipFileToPatch.getInnerArchive().getAbsolutePath(), innerDirectory)));
        return patchIdePatcher;
    }

    private PatchTarget patchTarget(final String pathToArchive, final String innerDir) {
        return new PatchTarget(innerDir, pathToArchive);
    }

    private void whenApplyPatch(final PatchIdePatcher patcher) {
        patcher.applyPatch();
    }
}
