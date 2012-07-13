package com.github.dmalch;

import java.io.File;

public class PatchIdePatcherImpl implements PatchIdePatcher {
    @Override
    public void applyPatch() {
        JarUtils.putIntoJar(new File("src/test/resources/file.txt"), new File("out/test/file_to_patch.zip"), "");
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
