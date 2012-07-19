package com.github.dmalch;

public class PatchTarget {
    private final String innerDir;
    private final String pathToArchive;
    private final String minRevision;

    public PatchTarget(final String innerDir, final String pathToArchive, final String minRevision) {
        this.innerDir = innerDir;
        this.pathToArchive = pathToArchive;
        this.minRevision = minRevision;
    }

    public String getPathToArchive() {
        return pathToArchive;
    }

    public String getInnerDir() {
        return innerDir;
    }

    public String getMinRevision() {
        return minRevision;
    }
}
