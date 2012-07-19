package com.github.dmalch;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.util.text.StringUtil;

public class RevisionManagerImpl implements RevisionManager {
    @Override
    public Boolean isCurrentVersionGreaterThen(final String minimalVersion) {
        final String currentVersion = getCurrentVersion();
        return StringUtil.compareVersionNumbers(currentVersion, minimalVersion) >= 0;
    }

    @Override
    public String getCurrentVersion() {
        return ApplicationInfo.getInstance().getBuild().asString();
    }
}
