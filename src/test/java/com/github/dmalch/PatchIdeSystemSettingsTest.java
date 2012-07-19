package com.github.dmalch;

import com.intellij.openapi.options.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.github.dmalch.PatchIdeApplicationComponent.USER_ACCEPTED_PATCHING;
import static com.intellij.util.ui.ThreeStateCheckBox.State.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatchIdeSystemSettingsTest {

    @InjectMocks
    private PatchIdeSystemSettings patchIdeSystemSettings;

    @Mock
    private PatchIdeApplicationComponent patchIdeApplicationComponent;

    @Mock
    private PersistenceManager persistenceManager;

    @Mock
    private PatchIdePatcher patcher;

    @Mock
    private ApplicationRestarter restarter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(patchIdeApplicationComponent.getPatcher()).thenReturn(patcher);
    }

    @Test
    public void testLoadSettingsWhenUserRejectedPatchAndNoPatchedFilesFound() throws Exception {
        givenUserRejectedPatch();
        givenNoPatchedFilesFound();

        whenResetSettings();

        thenCheckBoxIsNotSelected();
    }

    @Test
    public void testLoadSettingsWhenUserAcceptedPatchAndNoPatchedFilesFound() throws Exception {
        givenUserAcceptedPatch();
        givenNoPatchedFilesFound();

        whenResetSettings();

        thenCheckBoxIsHalfSelected();
    }

    @Test
    public void testLoadSettingsWhenUserRejectedPatchAndPatchedFilesFound() throws Exception {
        givenUserRejectedPatch();
        givenPatchedFilesFound();

        whenResetSettings();

        thenCheckBoxIsHalfSelected();
    }

    @Test
    public void testLoadSettingsWhenUserAcceptedPatchAndPatchedFilesFound() throws Exception {
        givenUserAcceptedPatch();
        givenPatchedFilesFound();

        whenResetSettings();

        thenCheckBoxIsSelected();
    }

    @Test
    public void testWhenUserAppliesPatchingThenPatchingIsPerformed() throws Exception {
        givenUserRejectedPatch();
        givenNoPatchedFilesFound();

        whenUserAppliesPatch();

        thenPatchingIsPerformed();
    }

    @Test
    public void testWhenUserRollsBackPatchThenPatchingIsRolledBack() throws Exception {
        givenUserAcceptedPatch();
        givenPatchedFilesFound();

        whenUserAppliesRollback();

        thenRollBackIsPerformed();
    }

    private void thenRollBackIsPerformed() {
        verify(patcher).applyRollback();
        verify(persistenceManager).setBoolean(USER_ACCEPTED_PATCHING, false);
        verify(restarter).askToRestart();
    }

    private void thenPatchingIsPerformed() {
        verify(patcher).applyPatch();
        verify(persistenceManager).setBoolean(USER_ACCEPTED_PATCHING, true);
        verify(restarter).askToRestart();
    }

    private void whenUserAppliesRollback() throws ConfigurationException {
        patchIdeSystemSettings.reset();
        patchIdeSystemSettings.getShouldPatchIdea().setState(NOT_SELECTED);
        patchIdeSystemSettings.apply();
    }

    private void whenUserAppliesPatch() throws ConfigurationException {
        patchIdeSystemSettings.reset();
        patchIdeSystemSettings.getShouldPatchIdea().setState(SELECTED);
        assertThat(patchIdeSystemSettings.isModified(), is(true));
        patchIdeSystemSettings.apply();
    }

    private void givenPatchedFilesFound() {
        when(patcher.checkFilesArePatched()).thenReturn(true);
    }

    private void givenNoPatchedFilesFound() {
        when(patcher.checkFilesArePatched()).thenReturn(false);
    }

    private void givenUserAcceptedPatch() {
        when(persistenceManager.getBoolean(eq(USER_ACCEPTED_PATCHING), anyBoolean())).thenReturn(true);
    }

    private void givenUserRejectedPatch() {
        when(persistenceManager.getBoolean(eq(USER_ACCEPTED_PATCHING), anyBoolean())).thenReturn(false);
    }

    private void thenCheckBoxIsSelected() {
        assertThat(patchIdeSystemSettings.getShouldPatchIdea().getState(), equalTo(SELECTED));
    }

    private void thenCheckBoxIsHalfSelected() {
        assertThat(patchIdeSystemSettings.getShouldPatchIdea().getState(), equalTo(DONT_CARE));
    }

    private void thenCheckBoxIsNotSelected() {
        assertThat(patchIdeSystemSettings.getShouldPatchIdea().getState(), equalTo(NOT_SELECTED));
    }

    private void whenResetSettings() {
        patchIdeSystemSettings.reset();
    }
}
