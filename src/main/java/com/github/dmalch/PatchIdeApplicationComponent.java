package com.github.dmalch;

public interface PatchIdeApplicationComponent {
    void performRollback();

    void performPatching();

    boolean isUserHasAcceptedPatching();

    boolean filesAreNotPatched();
}
