package com.github.dmalch;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

public class PatchTarget {
    private final String innerDir;
    private final Collection<String> pathToArchive;
    private final String minRevision;

    public PatchTarget(final String innerDir, final String pathToArchive, final String minRevision) {
        this(innerDir, ImmutableList.of(pathToArchive), minRevision);
    }

    public PatchTarget(final String innerDir, final Collection<String> pathToArchive, final String minRevision) {
        this.innerDir = innerDir;
        this.pathToArchive = pathToArchive;
        this.minRevision = minRevision;
    }

    public Collection<String> getPathToArchives() {
        return pathToArchive;
    }

    public String getInnerDir() {
        return innerDir;
    }

    public String getMinRevision() {
        return minRevision;
    }
}
