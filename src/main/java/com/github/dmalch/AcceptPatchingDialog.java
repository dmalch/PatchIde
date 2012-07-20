package com.github.dmalch;

import com.intellij.openapi.ui.Messages;

public class AcceptPatchingDialog {

    public int showDialog() {
        return Messages.showYesNoDialog("Patch IDE plugin wants to patch the instance of your IDE. You can revert or apply patch at settings/Patch IDE menu.", "Confirm Patch",
                "Patch", "Cancel",
                Messages.getQuestionIcon());
    }
}
