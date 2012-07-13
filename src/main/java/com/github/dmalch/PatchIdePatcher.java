package com.github.dmalch;

public interface PatchIdePatcher {
    void applyPatch();

    boolean checkFilesArePatched();

    boolean applyRollback();
}
