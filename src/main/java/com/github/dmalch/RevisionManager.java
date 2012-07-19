package com.github.dmalch;

public interface RevisionManager {
    Boolean isCurrentVersionGreaterThen(String minimalRevision);

    String getCurrentVersion();
}
