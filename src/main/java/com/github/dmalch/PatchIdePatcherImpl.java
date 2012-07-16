package com.github.dmalch;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import de.schlichtherle.truezip.file.TFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.zip.CRC32;

import static com.google.common.collect.Maps.newHashMap;
import static java.text.MessageFormat.format;

public class PatchIdePatcherImpl implements PatchIdePatcher {
    private final Map<String, PatchTarget> filesToPatch = newHashMap();

    @Override
    public void applyPatch() {
        for (final String patchFilePath : filesToPatch.keySet()) {

            final PatchTarget patchTarget = filesToPatch.get(patchFilePath);
            final File patchFile = new File(patchFilePath);

            final String targetFileName = patchFile.getName();

            final File jarFile = new File(patchTarget.getPathToArchive());
            final TFile jarEntry = JarUtils.jarFile(jarFile, patchTarget.getInnerDir(), targetFileName);

            JarUtils.extractFromJar(bakFile(patchFile, jarFile), jarEntry);
            JarUtils.putIntoJar(patchFile, jarEntry);
        }
    }

    @Override
    public boolean checkFilesArePatched() {
        boolean filesArePatched = true;

        for (final String patchFilePath : filesToPatch.keySet()) {

            final PatchTarget patchTarget = filesToPatch.get(patchFilePath);
            final File patchFile = new File(patchFilePath);

            final String targetFileName = patchFile.getName();

            final File jarFile = new File(patchTarget.getPathToArchive());
            final TFile jarEntry = JarUtils.jarFile(jarFile, patchTarget.getInnerDir(), targetFileName);

            if (filesAreDifferent(patchFile, jarEntry)) {
                filesArePatched = false;
            }
        }

        return filesArePatched;
    }

    @Override
    public boolean applyRollback() {
        boolean jarWasModified = false;

        for (final String patchFilePath : filesToPatch.keySet()) {

            final PatchTarget patchTarget = filesToPatch.get(patchFilePath);
            final File patchFile = new File(patchFilePath);

            final String targetFileName = patchFile.getName();

            final File jarFile = new File(patchTarget.getPathToArchive());
            final TFile jarEntry = JarUtils.jarFile(jarFile, patchTarget.getInnerDir(), targetFileName);

            final File bakFile = bakFile(patchFile, jarFile);

            if (filesAreDifferent(bakFile, jarEntry)) {
                JarUtils.putIntoJar(bakFile, jarEntry);
                jarWasModified = true;
            }
        }

        return jarWasModified;
    }

    private boolean filesAreDifferent(final File origFile, final File jarEntry) {
        try {
            final long orig = calcFileChecksum(origFile);
            final long fromJar = calcFileChecksum(jarEntry);
            return orig != fromJar;
        } catch (Exception e) {
            return true;
        }
    }

    protected long calcFileChecksum(final File file) {
        try {
            return Files.getChecksum(file, new CRC32());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File bakFile(final File patchFile, final File jarFile) {
        return new File(format("{0}/{1}.bak", jarFile.getParent(), patchFile.getName()));
    }

    public void setFilesToPatch(final ImmutableMap<String, PatchTarget> filesToPatch) {
        this.filesToPatch.putAll(filesToPatch);
    }
}
