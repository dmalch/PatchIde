package com.github.dmalch;

import com.google.common.base.Objects;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NonNls;

import static com.intellij.openapi.ui.Messages.OK;

public class ApplicationRestarterImpl implements ApplicationRestarter {
    @NonNls
    private static final String POSTPONE = "&Postpone";

    @Override
    public void restart(final Boolean askBeforeRestart) {
        final ApplicationEx app = ApplicationManagerEx.getApplicationEx();
        if (app.isRestartCapable()) {
            if (!askBeforeRestart || (userWantsToRestart())) {
                app.restart();
            }
        } else {
            if (!askBeforeRestart || (userWantsToShutdown())) {
                app.exit(true);
            }
        }
    }

    private boolean userWantsToRestart() {
        return Objects.equal(showRestartDialog(), OK);
    }

    private boolean userWantsToShutdown() {
        return Objects.equal(showShutdownDialog(), OK);
    }

    private int showRestartDialog() {
        String message = IdeBundle.message("message.idea.restart.required", ApplicationNamesInfo.getInstance().getProductName());
        return Messages.showYesNoDialog(message, "Restart", "Restart", POSTPONE, Messages.getQuestionIcon());
    }

    private int showShutdownDialog() {
        String message = IdeBundle.message("message.idea.shutdown.required", ApplicationNamesInfo.getInstance().getProductName());
        return Messages.showYesNoDialog(message, "Restart", "Shut Down", POSTPONE, Messages.getQuestionIcon());
    }
}
