package com.github.dmalch;

public class PatchTarget {
    private final String innerDir;
    private final String pathToArchive;

    public PatchTarget(final String innerDir, final String pathToArchive) {
        this.innerDir = innerDir;
        this.pathToArchive = pathToArchive;
    }

    public String getPathToArchive() {
        return pathToArchive;
    }

    public String getInnerDir() {
        return innerDir;
    }
}
