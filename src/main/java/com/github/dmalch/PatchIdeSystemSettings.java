package com.github.dmalch;

import com.google.common.base.Objects;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ui.ThreeStateCheckBox;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

import static com.github.dmalch.PatchIdeApplicationComponent.USER_ACCEPTED_PATCHING;
import static com.intellij.util.ui.ThreeStateCheckBox.State.*;

public class PatchIdeSystemSettings implements Configurable {
    private JPanel myPanel;
    private JLabel headerText;
    private ThreeStateCheckBox shouldPatchIdea;

    private PatchIdeApplicationComponent patchIdeApplicationComponent;

    private PersistenceManager persistenceManager = new PersistenceManagerImpl();

    private ApplicationRestarter restarter = new ApplicationRestarterImpl();

    private ThreeStateCheckBox.State initialState;

    @Nls
    @Override
    public String getDisplayName() {
        return "Patch IDE";
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        return myPanel;
    }

    private boolean isUserHasAcceptedPatching() {
        return persistenceManager.getBoolean(USER_ACCEPTED_PATCHING, true);
    }

    private boolean filesArePatched() {
        final PatchIdePatcher patcher = getPatcher();
        return patcher.checkFilesArePatched();
    }

    private PatchIdePatcher getPatcher() {
        return getPatchIdeApplicationComponent().getPatcher();
    }

    @Override
    public boolean isModified() {
        return !Objects.equal(shouldPatchIdea.getState(), initialState);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (Objects.equal(shouldPatchIdea.getState(), SELECTED)) {
            userHasAcceptedPatching();
            getPatcher().applyPatch();
            restarter.askToRestart();
        } else if (Objects.equal(shouldPatchIdea.getState(), NOT_SELECTED)) {
            userHasRejectedPatching();
            getPatcher().applyRollback();
            restarter.askToRestart();
        }
    }

    @Override
    public void reset() {
        final boolean userHasAcceptedPatching = isUserHasAcceptedPatching();
        final boolean filesArePatched = filesArePatched();

        if (userHasAcceptedPatching && filesArePatched) {
            initialState = SELECTED;
        } else if (userHasAcceptedPatching || filesArePatched) {
            initialState = DONT_CARE;
        } else {
            initialState = NOT_SELECTED;
        }

        shouldPatchIdea.setState(initialState);
    }

    @Override
    public void disposeUIResources() {
    }

    private void userHasAcceptedPatching() {
        persistenceManager.setBoolean(USER_ACCEPTED_PATCHING, true);
    }

    private void userHasRejectedPatching() {
        persistenceManager.setBoolean(USER_ACCEPTED_PATCHING, false);
    }

    public ThreeStateCheckBox getShouldPatchIdea() {
        return shouldPatchIdea;
    }

    public PatchIdeApplicationComponent getPatchIdeApplicationComponent() {
        if (patchIdeApplicationComponent == null) {
            patchIdeApplicationComponent = (PatchIdeApplicationComponent) ApplicationManager.getApplication().getComponent("com.github.dmalch.PatchIdeApplicationComponent");
        }
        return patchIdeApplicationComponent;
    }

    public void setPersistenceManager(final PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public void setRestarter(final ApplicationRestarter restarter) {
        this.restarter = restarter;
    }

    public void setPatchIdeApplicationComponent(final PatchIdeApplicationComponent patchIdeApplicationComponent) {
        this.patchIdeApplicationComponent = patchIdeApplicationComponent;
    }
}
