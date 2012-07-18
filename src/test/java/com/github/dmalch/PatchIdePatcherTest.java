package com.github.dmalch;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import de.schlichtherle.truezip.file.TFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PatchIdePatcherTest extends AbstractPatchTest {
    @Test
    public void testApplyPatchAtTheSpecifiedDirectory() throws Exception {
        final String expectedDir = givenExpectedDir();
        final File expectedFile = givenPatchFile(expectedDir);
        final TFile zipFileToPatch = givenZipFileToPatch(expectedDir);
        final PatchIdePatcher patcher = givenPatcherFor(expectedFile, zipFileToPatch, expectedDir);

        whenApplyPatch(patcher);

        thenPatchIsApplied(zipFileToPatch, expectedFile);
    }

    @Test
    public void testRollbackFileIsCreatedWhenPatchIsApplied() throws Exception {
        final File patchFile = givenPatchFile("");
        final TFile zipFileToPatch = givenZipFileToPatch("");
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch, "");

        whenApplyPatch(patcher);

        thenRollbackFileIsCreated(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testApplyRollbackWhenFilesWerePatched() throws Exception {
        final File patchFile = givenPatchFile("");
        final TFile zipFileToPatch = givenZipFileToPatch("");
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch, "");

        whenApplyPatch(patcher);
        final boolean result = whenApplyRollback(patcher);

        thenFilesWereActuallyRolledBack(result);
        thenRollbackIsApplied(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testApplyRollbackWhenNoPatchWasApplied() throws Exception {
        final File patchFile = givenPatchFile("");
        final TFile zipFileToPatch = givenZipFileToPatch("");
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch, "");

        final boolean result = whenApplyRollback(patcher);

        thenFilesWereNotRolledBack(result);
        thenRollbackIsApplied(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testApplyRollbackWhenPatchedFilesWereChangedToOriginal() throws Exception {
        final File patchFile = givenPatchFile("");
        final TFile zipFileToPatch = givenZipFileToPatch("");
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch, "");

        whenApplyPatch(patcher);
        whenApplyRollback(patcher);
        final boolean result = whenApplyRollback(patcher);

        thenFilesWereNotRolledBack(result);
        thenRollbackIsApplied(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testCheckModificationsWhenNoFilesChanged() throws Exception {
        final File patchFile = givenPatchFile("");
        final TFile zipFileToPatch = givenZipFileToPatch("");

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch, "");

        final boolean result = whenCheckFilesArePatched(patcher);

        thenFilesWereNotPatched(result);
    }

    @Test
    public void testCheckModificationsWhenFilesWereActuallyChanged() throws Exception {
        final File patchFile = givenPatchFile("");
        final TFile zipFileToPatch = givenZipFileToPatch("");

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch, "");

        whenApplyPatch(patcher);

        final boolean result = whenCheckFilesArePatched(patcher);

        thenFilesWerePatched(result);
    }

    private void thenFilesWerePatched(final boolean result) {
        assertThat(result, is(true));
    }

    private void thenFilesWereNotPatched(final boolean result) {
        assertThat(result, is(false));
    }

    private boolean whenCheckFilesArePatched(final PatchIdePatcher patcher) {
        return patcher.checkFilesArePatched();
    }

    private void thenFilesWereNotRolledBack(final boolean result) {
        assertThat(result, is(false));
    }

    private void thenFilesWereActuallyRolledBack(final boolean result) {
        assertThat(result, is(true));
    }

    private void thenRollbackFileIsCreated(final TFile zipFileToPatch, final byte[] expectedFileValue) {
        final String absolutePath = format("{0}/{1}.bak", zipFileToPatch.getInnerArchive().getParent(), zipFileToPatch.getEnclEntryName());
        final byte[] backupFileContent = readFileContent(new File(absolutePath));

        assertThat(backupFileContent, equalTo(expectedFileValue));
    }

    private void thenRollbackIsApplied(final TFile zipFileToPatch, final byte[] expectedFileValue) {
        final byte[] fileToPatchValue = readFileContent(zipFileToPatch);

        assertThat(fileToPatchValue, equalTo(expectedFileValue));
    }

    private boolean whenApplyRollback(final PatchIdePatcher patcher) {
        return patcher.applyRollback();
    }

    private String givenExpectedDir() {
        return randomAlphanumeric(5);
    }

    private void thenPatchIsApplied(final File zipFileToPatch, final File expectedFile) {

        final byte[] expectedFileValue = readFileContent(expectedFile);
        final byte[] fileToPatchValue = readFileContent(zipFileToPatch);

        assertThat(fileToPatchValue, equalTo(expectedFileValue));
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
        new File("out/test/file.txt.bak").delete();
        return patchIdePatcher;
    }

    private PatchTarget patchTarget(final String pathToArchive, final String innerDir) {
        return new PatchTarget(innerDir, pathToArchive);
    }

    private void whenApplyPatch(final PatchIdePatcher patcher) {
        patcher.applyPatch();
    }
}

