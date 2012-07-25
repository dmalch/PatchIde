package com.github.dmalch;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;

public class PatchIdeApplicationComponentImpl implements ApplicationComponent, PatchIdeApplicationComponent {

    public static final String SHOW_PATCH_DIALOG = "colorIde.showPatchDialog";
    public static final String USER_ACCEPTED_PATCHING = "colorIde.userAcceptedPatching";

    private PersistenceManager persistenceManager = new PersistenceManagerImpl();

    private AcceptPatchingDialog acceptPatchingDialog = new AcceptPatchingDialog();

    private ApplicationRestarter restarter = new ApplicationRestarterImpl();

    private PatchIdePatcher patcher = new PatchIdePatcherImpl();

    public PatchIdeApplicationComponentImpl() {
        final ImmutableMap.Builder<String, PatchTarget> builder = new ImmutableMap.Builder<String, PatchTarget>();
        builder.put("com/intellij/ui/treeStructure/SimpleNode.class", new PatchTarget("com/intellij/ui/treeStructure", "../lib/openapi.jar", "", "120.171"));
        builder.put("com/intellij/ide/util/treeView/PresentableNodeDescriptor.class", new PatchTarget("com/intellij/ide/util/treeView", "../lib/openapi.jar", "", "120.171"));
        builder.put("com/intellij/ide/projectView/impl/ProjectViewTree.class", new PatchTarget("com/intellij/ide/projectView/impl", ImmutableList.of("../lib/webide.jar", "../lib/idea.jar", "../lib/rubymine.jar", "../lib/pycharm.jar"), "118.308", ""));
        patcher.setFilesToPatch(builder.build());
    }

    @Override
    public void initComponent() {
        if (shouldShowPatchDialog()) {
            doNotShowPatchDialogAnyMore();
            if (userWantsToPatchClasses()) {
                performPatching();
            } else {
                performRollback();
            }
        }
    }

    @Override
    public void performRollback() {
        userHasRejectedPatching();
        if (patcher.applyRollback()) {
            restarter.restart();
        }
    }

    @Override
    public void performPatching() {
        userHasAcceptedPatching();
        patcher.applyPatch();
        restarter.restart();
    }

    private void userHasRejectedPatching() {
        persistenceManager.setBoolean(USER_ACCEPTED_PATCHING, false);
    }

    private void userHasAcceptedPatching() {
        persistenceManager.setBoolean(USER_ACCEPTED_PATCHING, true);
    }

    @Override
    public boolean isUserHasAcceptedPatching() {
        return persistenceManager.getBoolean(USER_ACCEPTED_PATCHING, true);
    }

    private boolean userWantsToPatchClasses() {
        return Objects.equal(OK_EXIT_CODE, acceptPatchingDialog.showDialog());
    }

    private void doNotShowPatchDialogAnyMore() {
        persistenceManager.setBoolean(SHOW_PATCH_DIALOG, false);
    }

    private boolean shouldShowPatchDialog() {
        return isUserHasAcceptedPatching() && (checkShowPatchDialogProperty() || filesAreNotPatched());
    }

    @Override
    public boolean filesAreNotPatched() {
        return !patcher.checkFilesArePatched();
    }

    private boolean checkShowPatchDialogProperty() {
        return persistenceManager.getBoolean(SHOW_PATCH_DIALOG, true);
    }

    public AcceptPatchingDialog getAcceptPatchingDialog() {
        return acceptPatchingDialog;
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "PatchIdeApplicationComponent";
    }

    public void setAcceptPatchingDialog(final AcceptPatchingDialog acceptPatchingDialog) {
        this.acceptPatchingDialog = acceptPatchingDialog;
    }

    public ApplicationRestarter getRestarter() {
        return restarter;
    }

    public void setRestarter(final ApplicationRestarter restarter) {
        this.restarter = restarter;
    }

    public PatchIdePatcher getPatcher() {
        return patcher;
    }

    public void setPatcher(final PatchIdePatcher patcher) {
        this.patcher = patcher;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public void setPersistenceManager(final PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
