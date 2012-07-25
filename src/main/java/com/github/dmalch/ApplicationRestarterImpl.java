package com.github.dmalch;

import com.google.common.base.Objects;
import com.intellij.openapi.application.ex.ApplicationManagerEx;

import static com.intellij.openapi.ui.Messages.*;

public class ApplicationRestarterImpl implements ApplicationRestarter {
    @Override
    public void restart(final Boolean askBeforeRestart) {
        if (!askBeforeRestart || (userWantsToRestart())) {
            ApplicationManagerEx.getApplicationEx().restart();
        }
    }

    private boolean userWantsToRestart() {
        return Objects.equal(showDialog(), OK);
    }

    private int showDialog() {
        return showYesNoDialog("Restart now?", "Restart", "Restart", "Postpone", getQuestionIcon());
    }
}
