package com.github.dmalch;

import com.intellij.openapi.options.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.intellij.util.ui.ThreeStateCheckBox.State.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatchIdeSystemSettingsTest {

    @InjectMocks
    private PatchIdeSystemSettings patchIdeSystemSettings;

    @Mock
    private PatchIdeApplicationComponent patchIdeApplicationComponent;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
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
        verify(patchIdeApplicationComponent).performRollback();
        ;
    }

    private void thenPatchingIsPerformed() {
        verify(patchIdeApplicationComponent).performPatching();
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
        when(patchIdeApplicationComponent.filesAreNotPatched()).thenReturn(false);
    }

    private void givenNoPatchedFilesFound() {
        when(patchIdeApplicationComponent.filesAreNotPatched()).thenReturn(true);
    }

    private void givenUserAcceptedPatch() {
        when(patchIdeApplicationComponent.isUserHasAcceptedPatching()).thenReturn(true);
    }

    private void givenUserRejectedPatch() {
        when(patchIdeApplicationComponent.isUserHasAcceptedPatching()).thenReturn(false);
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
