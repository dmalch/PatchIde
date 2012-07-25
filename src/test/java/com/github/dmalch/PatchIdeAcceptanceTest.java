package com.github.dmalch;

import com.intellij.openapi.ui.Messages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.github.dmalch.PatchIdeApplicationComponentImpl.SHOW_PATCH_DIALOG;
import static com.github.dmalch.PatchIdeApplicationComponentImpl.USER_ACCEPTED_PATCHING;
import static com.intellij.openapi.ui.Messages.OK;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatchIdeAcceptanceTest {

    @Mock
    private AcceptPatchingDialog acceptPatchingDialog;

    @Mock
    private ApplicationRestarter applicationRestarter;

    @Mock(answer = Answers.RETURNS_MOCKS)
    private PersistenceManager persistenceManager;

    @Mock
    private PatchIdePatcher patcher;

    @InjectMocks
    private final PatchIdeApplicationComponentImpl patchIdeApplicationComponent = new PatchIdeApplicationComponentImpl();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void testPatchDialogIsShownAtFirstStart() throws Exception {
        givenPatchIdeIsRunFirstTime();

        whenStartPatchIde();

        thenDialogIsShown();
    }

    @Test
    public void testWhenUserAcceptsPatchingThenPatchIsAppliedAndIdeIsRebooted() {
        givenPatchIdeIsRunFirstTime();

        whenAcceptPatching();

        thenPatchIsAppliedAndRebootDialogIsShown();
    }

    @Test
    public void testWhenUserRejectsPatchingThenPatchIsRolledBackAndIfNoChangesIdeIsNotRebooted() {
        givenPatchIdeIsRunFirstTime();

        whenDiscardPatching();

        thenPatchIsNotAppliedAndRebootIsNotPerformed();
    }

    @Test
    public void testWhenUserRejectsPatchingThenPatchIsRolledBackAndIfThereAreChangesIdeIsRebooted() {
        givenPatchIdeIsRunFirstTime();
        givenSeveralFilesArePatched();

        whenDiscardPatching();

        thenPatchIsNotAppliedAndRebootIsPerformed();
    }

    @Test
    public void testPatchDialogIsNotShownAfterFirstRun() throws Exception {
        givenColorIdeIsRunAfterFirstTime();

        whenStartPatchIde();

        thenDialogIsNotShown();
    }

    @Test
    public void testPatchDialogIsShownAfterFirstRunWhenPatchedFilesWereChanged() throws Exception {
        givenColorIdeIsRunAfterFirstTime();
        givenNotAllFilesWerePatched();

        whenStartPatchIde();

        thenDialogIsShown();
    }

    @Test
    public void testPatchDialogIsNotShownAfterFirstRunWhenUserRejectedPatchEvenIfPatchedFilesWereChanged() throws Exception {
        givenColorIdeIsRunAfterFirstTime();
        givenUserRejectedPatching();
        givenNotAllFilesWerePatched();

        whenStartPatchIde();

        thenDialogIsNotShown();
    }

    private void givenSeveralFilesArePatched() {
        when(patcher.applyRollback()).thenReturn(true);
    }

    private void givenUserRejectedPatching() {
        when(persistenceManager.getBoolean(eq(USER_ACCEPTED_PATCHING), anyBoolean())).thenReturn(false);
    }

    private void givenUserAcceptedPatching() {
        when(persistenceManager.getBoolean(eq(USER_ACCEPTED_PATCHING), anyBoolean())).thenReturn(true);
    }

    private void givenNotAllFilesWerePatched() {
        when(patcher.checkFilesArePatched()).thenReturn(false);
    }

    private void givenFilesWerePatched() {
        when(patcher.checkFilesArePatched()).thenReturn(true);
    }

    private void thenDialogIsNotShown() {
        verify(acceptPatchingDialog, never()).showDialog();
    }

    private void thenPatchIsNotAppliedAndRebootIsNotPerformed() {
        thenPatchIsNotApplied();
        verify(applicationRestarter, never()).restart();
    }

    private void thenPatchIsNotAppliedAndRebootIsPerformed() {
        thenPatchIsNotApplied();
        verify(applicationRestarter).restart();
    }

    private void thenPatchIsNotApplied() {
        verify(patcher).applyRollback();
        verify(persistenceManager).setBoolean(SHOW_PATCH_DIALOG, false);
        verify(patcher, never()).applyPatch();
        verify(persistenceManager).setBoolean(USER_ACCEPTED_PATCHING, false);
    }

    private void whenDiscardPatching() {
        when(acceptPatchingDialog.showDialog()).thenReturn(Messages.CANCEL);
        whenStartPatchIde();
    }

    private void thenPatchIsAppliedAndRebootDialogIsShown() {
        verify(patcher).applyPatch();
        verify(applicationRestarter).restart();
        verify(persistenceManager).setBoolean(SHOW_PATCH_DIALOG, false);
        verify(persistenceManager).setBoolean(USER_ACCEPTED_PATCHING, true);
    }

    private void whenAcceptPatching() {
        when(acceptPatchingDialog.showDialog()).thenReturn(OK);
        whenStartPatchIde();
    }

    private void thenDialogIsShown() {
        verify(acceptPatchingDialog).showDialog();
    }

    private void whenStartPatchIde() {
        patchIdeApplicationComponent.initComponent();
    }

    private void givenPatchIdeIsRunFirstTime() {
        when(persistenceManager.getBoolean(eq(SHOW_PATCH_DIALOG), anyBoolean())).thenReturn(true);
        givenNotAllFilesWerePatched();
        givenUserAcceptedPatching();
    }

    private void givenColorIdeIsRunAfterFirstTime() {
        when(persistenceManager.getBoolean(eq(SHOW_PATCH_DIALOG), anyBoolean())).thenReturn(false);
        givenFilesWerePatched();
        givenUserAcceptedPatching();
    }
}
