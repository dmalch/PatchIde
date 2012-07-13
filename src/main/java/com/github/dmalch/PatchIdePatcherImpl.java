package com.github.dmalch;

public class PatchIdePatcherImpl implements PatchIdePatcher {
    @Override
    public void applyPatch() {
    }

    @Override
    public boolean checkFilesArePatched() {
        return true;
    }

    @Override
    public boolean applyRollback() {
        return false;
    }
}
