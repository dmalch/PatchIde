package com.github.dmalch;

import com.google.common.base.Objects;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ui.ThreeStateCheckBox;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

import static com.github.dmalch.ColorIdeApplicationComponent.USER_ACCEPTED_PATCHING;
import static com.intellij.util.ui.ThreeStateCheckBox.State.*;

public class ColorIdeSystemSettings implements Configurable {
    private JPanel myPanel;
    private JLabel headerText;
    private ThreeStateCheckBox shouldPatchIdea;

    private PersistenceManager persistenceManager = new PersistenceManagerImpl();

    private ColorIdePatcher patcher = new ColorIdePatcherImpl();

    private ApplicationRestarter restarter = new ApplicationRestarterImpl();

    private ThreeStateCheckBox.State initialState;

    @Nls
    @Override
    public String getDisplayName() {
        return "Color IDE";
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
        return patcher.checkFilesArePatched();
    }

    @Override
    public boolean isModified() {
        return Objects.equal(shouldPatchIdea.getState(), initialState);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (Objects.equal(shouldPatchIdea.getState(), SELECTED)) {
            userHasAcceptedPatching();
            patcher.applyPatch();
            restarter.askToRestart();
        } else if (Objects.equal(shouldPatchIdea.getState(), NOT_SELECTED)) {
            userHasRejectedPatching();
            patcher.applyRollback();
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

    public void setPatcher(final ColorIdePatcher patcher) {
        this.patcher = patcher;
    }

    public void setPersistenceManager(final PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public void setRestarter(final ApplicationRestarter restarter) {
        this.restarter = restarter;
    }
}
