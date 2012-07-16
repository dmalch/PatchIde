package com.github.dmalch;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.text.MessageFormat.format;

public class PatchIdePatcherImpl implements PatchIdePatcher {
    private final Map<String, PatchTarget> filesToPatch = newHashMap();

    @Override
    public void applyPatch() {
        for (final String patchFilePath : filesToPatch.keySet()) {

            final PatchTarget patchTarget = filesToPatch.get(patchFilePath);

            final File jarFile = new File(patchTarget.getPathToArchive());
            final String targetFileName = extractFileNameFromPath(patchFilePath);

            JarUtils.extractFromJar(jarFile, patchTarget.getInnerDir(), targetFileName, bakFileName(patchFilePath, jarFile));
            JarUtils.putIntoJar(patchFilePath, jarFile, patchTarget.getInnerDir(), targetFileName);
        }
    }

    @Override
    public boolean checkFilesArePatched() {
        return true;
    }

    @Override
    public boolean applyRollback() {
        for (final String patchFilePath : filesToPatch.keySet()) {

            final PatchTarget patchTarget = filesToPatch.get(patchFilePath);

            final File jarFile = new File(patchTarget.getPathToArchive());

            JarUtils.putIntoJar(bakFileName(patchFilePath, jarFile), jarFile, patchTarget.getInnerDir(), extractFileNameFromPath(patchFilePath));
        }

        return false;
    }

    private String bakFileName(final String patchFilePath, final File jarFile) {
        return format("{0}/{1}.bak", jarFile.getParent(), extractFileNameFromPath(patchFilePath));
    }

    private String extractFileNameFromPath(final String patchFileName) {
        return new File(patchFileName).getName();
    }

    public void setFilesToPatch(final ImmutableMap<String, PatchTarget> filesToPatch) {
        this.filesToPatch.putAll(filesToPatch);
    }
}
