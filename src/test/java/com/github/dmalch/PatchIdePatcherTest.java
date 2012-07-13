package com.github.dmalch;

import org.junit.Test;

public class PatchIdePatcherTest {
    @Test
    public void testApplyPatch() throws Exception {
        PatchIdePatcher patcher = givenPatcher();

        whenApplyPatch(patcher);

        thenPatchIsApplied();
    }

    private void thenPatchIsApplied() {

    }

    private PatchIdePatcher givenPatcher() {
        return new PatchIdePatcherImpl();
    }

    private void whenApplyPatch(final PatchIdePatcher patcher) {
        patcher.applyPatch();
    }
}
