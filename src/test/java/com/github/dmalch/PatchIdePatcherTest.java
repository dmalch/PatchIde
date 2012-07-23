package com.github.dmalch;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import de.schlichtherle.truezip.file.TFile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static com.google.common.collect.Collections2.transform;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatchIdePatcherTest extends AbstractPatchTest {

    @Spy
    @InjectMocks
    private PatchIdePatcher idePatcher = new PatchIdePatcherImpl();

    @Mock
    private RevisionManager revisionManager;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void testApplyPatchAtTheSpecifiedDirectory() throws Exception {
        final String expectedDir = givenExpectedDir();
        final File expectedFile = givenPatchFile(expectedDir);
        final TFile zipFileToPatch = givenZipFileToPatch(expectedDir);
        final PatchIdePatcher patcher = givenPatcherFor(expectedFile, ImmutableList.of(zipFileToPatch), expectedDir, "", "");

        whenApplyPatch(patcher);

        thenPatchIsApplied(zipFileToPatch, expectedFile);
    }

    @Test
    public void testPatchIsNotAppliedWhenIdeRevisionIsLowerThanRequired() throws Exception {
        final File expectedFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final String fileMinRevision = "5";
        final PatchIdePatcher patcher = givenPatcherFor(expectedFile, ImmutableList.of(zipFileToPatch), "", fileMinRevision, "");
        when(revisionManager.isCurrentVersionGreaterThen(fileMinRevision)).thenReturn(false);

        whenApplyPatch(patcher);

        thenPatchIsNotApplied(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testPatchIsNotAppliedWhenIdeRevisionIsHigherThanMaximux() throws Exception {
        final File expectedFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final String fileMaxRevision = "5";
        final PatchIdePatcher patcher = givenPatcherFor(expectedFile, ImmutableList.of(zipFileToPatch), "", "", fileMaxRevision);
        when(revisionManager.isCurrentVersionLowerThen(fileMaxRevision)).thenReturn(false);

        whenApplyPatch(patcher);

        thenPatchIsNotApplied(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testPatchIsAppliedWhenSeveralJarFilesAreProvidedForPatch() throws Exception {
        final File expectedFile = givenPatchFile();
        final TFile zipFileToPatch1 = givenZipFileToPatch();
        final TFile zipFileToPatch2 = givenZipFileToPatch();

        final PatchIdePatcher patcher = givenPatcherFor(expectedFile, zipFileToPatch1, zipFileToPatch2);

        whenApplyPatch(patcher);

        thenPatchIsApplied(zipFileToPatch1, expectedFile);
        thenPatchIsApplied(zipFileToPatch2, expectedFile);
    }

    @Test
    public void testPatchIsNotAppliedWhenJarFileDoesNotExist() throws Exception {
        final File expectedFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();

        final PatchIdePatcher patcher = givenPatcherFor(expectedFile, zipFileToPatch);

        whenZipFileDoesNotExist(zipFileToPatch);
        whenApplyPatch(patcher);

        nothingHappens();
    }

    @Test
    public void testRollbackFileIsCreatedWhenPatchIsApplied() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch);

        whenApplyPatch(patcher);

        thenRollbackFileIsCreated(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testRollbackFileIsNotOverwrittenWhenPatchIsAppliedTwice() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch);

        whenApplyPatch(patcher);
        whenApplyPatch(patcher);

        thenRollbackFileIsCreated(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testApplyRollbackWhenFilesWerePatched() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch);

        whenApplyPatch(patcher);
        final boolean result = whenApplyRollback(patcher);

        thenFilesWereActuallyRolledBack(result);
        thenRollbackIsApplied(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testApplyRollbackWhenNoPatchWasApplied() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch);

        final boolean result = whenApplyRollback(patcher);

        thenFilesWereNotRolledBack(result);
        thenRollbackIsApplied(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testApplyRollbackWhenPatchedFilesWereChangedToOriginal() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();
        final byte[] expectedFileValue = readFileContent(zipFileToPatch);

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch);

        whenApplyPatch(patcher);
        whenApplyRollback(patcher);
        final boolean result = whenApplyRollback(patcher);

        thenFilesWereNotRolledBack(result);
        thenRollbackIsApplied(zipFileToPatch, expectedFileValue);
    }

    @Test
    public void testCheckModificationsWhenNoFilesChanged() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch);

        final boolean result = whenCheckFilesArePatched(patcher);

        thenFilesWereNotPatched(result);
    }

    @Test
    public void testCheckModificationsWhenFilesWereActuallyChanged() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch);

        whenApplyPatch(patcher);

        final boolean result = whenCheckFilesArePatched(patcher);

        thenFilesWerePatched(result);
    }

    @Test
    public void testCheckModificationsDoesNotCheckFilesWithMinRevisionHigherThanRequired() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();

        final String fileMinRevision = "5";
        final PatchIdePatcher patcher = givenPatcherFor(patchFile, ImmutableList.of(zipFileToPatch), "", fileMinRevision, "");
        when(revisionManager.isCurrentVersionGreaterThen(fileMinRevision)).thenReturn(false);

        final boolean result = whenCheckFilesArePatched(patcher);

        thenFilesWerePatched(result);
    }

    @Test
    public void testCheckModificationsDoesNotCheckFilesWithMaxRevisionLowerThanRequired() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = givenZipFileToPatch();

        final String fileMaxRevision = "5";
        final PatchIdePatcher patcher = givenPatcherFor(patchFile, ImmutableList.of(zipFileToPatch), "", "", fileMaxRevision);
        when(revisionManager.isCurrentVersionLowerThen(fileMaxRevision)).thenReturn(false);

        final boolean result = whenCheckFilesArePatched(patcher);

        thenFilesWerePatched(result);
    }

    @Test
    public void testCheckModificationsDoesNotCheckNotExistingArchives() throws Exception {
        final File patchFile = givenPatchFile();
        final TFile zipFileToPatch = randomPatchFileName();

        final PatchIdePatcher patcher = givenPatcherFor(patchFile, zipFileToPatch);

        final boolean result = whenCheckFilesArePatched(patcher);

        thenFilesWerePatched(result);
    }

    private TFile givenZipFileToPatch() {
        return givenZipFileToPatch("");
    }

    private File givenPatchFile() {
        return givenPatchFile("");
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
        thenPatchIsNotApplied(zipFileToPatch, expectedFileValue);
    }

    private void thenPatchIsNotApplied(final TFile zipFileToPatch, final byte[] expectedFileValue) {
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

    private PatchIdePatcher givenPatcherFor(final File patchFile, final TFile zipFileToPatch) {
        return givenPatcherFor(patchFile, ImmutableList.of(zipFileToPatch), "", "", "");
    }

    private PatchIdePatcher givenPatcherFor(final File patchFile, final TFile zipFileToPatch1, final TFile zipFileToPatch2) {
        return givenPatcherFor(patchFile, ImmutableList.of(zipFileToPatch1, zipFileToPatch2), "", "", "");
    }

    private TFile givenZipFileToPatch(final String expectedDir) {
        final File originalFile = new File("src/test/resources/file.zip");
        final File fileToPatch = randomPatchFileName();
        try {
            Files.copy(originalFile, fileToPatch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new TFile(fileToPatch, format("{0}/file.txt", expectedDir));
    }

    private TFile randomPatchFileName() {
        return new TFile(format("out/test/file_to_patch{0}.zip", randomAlphanumeric(5)));
    }

    private PatchIdePatcher givenPatcherFor(final File expectedFile, final Collection<TFile> zipFilesToPatch, final String innerDirectory, final String minimalRevision, final String maximumRevision) {
        idePatcher.setFilesToPatch(ImmutableMap.of(expectedFile.getAbsolutePath(), patchTarget(innerDirectory, transform(zipFilesToPatch, extractArchivePath()), minimalRevision, maximumRevision)));
        new File("out/test/file.txt.bak").delete();

        when(revisionManager.isCurrentVersionGreaterThen(minimalRevision)).thenReturn(true);
        when(revisionManager.isCurrentVersionLowerThen(maximumRevision)).thenReturn(true);
        return idePatcher;
    }

    private Function<TFile, String> extractArchivePath() {
        return new Function<TFile, String>() {
            @Override
            public String apply(@Nullable final TFile zipFilesToPatch) {
                return zipFilesToPatch.getInnerArchive().getAbsolutePath();
            }
        };
    }

    private PatchTarget patchTarget(final String innerDir, final String pathToArchive, final String minimalRevision, final String maximumRevision) {
        return new PatchTarget(innerDir, pathToArchive, minimalRevision, maximumRevision);
    }


    private PatchTarget patchTarget(final String innerDir, final Collection<String> pathToArchives, final String minimalRevision, final String maximumRevision) {
        return new PatchTarget(innerDir, pathToArchives, minimalRevision, maximumRevision);
    }

    private void whenApplyPatch(final PatchIdePatcher patcher) {
        patcher.applyPatch();
    }

    private void nothingHappens() {
    }

    private void whenZipFileDoesNotExist(final TFile zipFileToPatch) throws IOException {
        zipFileToPatch.rm();
    }

}

