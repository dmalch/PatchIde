package com.github.dmalch;

import com.intellij.openapi.ui.Messages;

public class AcceptPatchingDialog {

    public int showDialog() {
        return Messages.showYesNoDialog("Patch IDE plugin wants to patch the instance of your IDE", "Confirm Patch",
                "Patch", "Cancel",
                Messages.getQuestionIcon());
    }
}
