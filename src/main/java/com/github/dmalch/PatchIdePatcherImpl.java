package com.github.dmalch;

import java.io.File;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class PatchIdePatcherImpl implements PatchIdePatcher {
    private final Map<String, String> filesToPatch = newHashMap();

    @Override
    public void applyPatch() {
        for (final String newFile : filesToPatch.keySet()) {
            JarUtils.putIntoJar(new File(newFile), new File(filesToPatch.get(newFile)), "");
        }
    }

    @Override
    public boolean checkFilesArePatched() {
        return true;
    }

    @Override
    public boolean applyRollback() {
        return false;
    }

    public void setFilesToPatch(final Map<String, String> filesToPatch) {
        this.filesToPatch.putAll(filesToPatch);
    }
}
