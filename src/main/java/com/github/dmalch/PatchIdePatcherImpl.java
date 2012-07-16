package com.github.dmalch;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class PatchIdePatcherImpl implements PatchIdePatcher {
    private final Map<String, PatchTarget> filesToPatch = newHashMap();

    @Override
    public void applyPatch() {
        for (final String newFile : filesToPatch.keySet()) {
            final PatchTarget patchTarget = filesToPatch.get(newFile);
            JarUtils.putIntoJar(new File(newFile), new File(patchTarget.getPathToArchive()), patchTarget.getInnerDir());
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

    public void setFilesToPatch(final ImmutableMap<String, PatchTarget> filesToPatch) {
        this.filesToPatch.putAll(filesToPatch);
    }
}
