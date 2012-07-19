package com.github.dmalch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

public class RevisionManagerTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private RevisionManagerImpl revisionManager;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void testIsCurrentRevisionGreaterThenMinimal() throws Exception {
        givenCurrentRevision("5");
        thenCurrentRevisionIsGreaterOrEqualThan("4");

        givenCurrentRevision("5.2");
        thenCurrentRevisionIsGreaterOrEqualThan("5.1");

        givenCurrentRevision("5.2");
        thenCurrentRevisionIsGreaterOrEqualThan("4.1");

        givenCurrentRevision("PS-5.2");
        thenCurrentRevisionIsGreaterOrEqualThan("4.1");

        givenCurrentRevision("5.2");
        thenCurrentRevisionIsGreaterOrEqualThan("");

        givenCurrentRevision("5.2");
        thenCurrentRevisionIsGreaterOrEqualThan(null);
    }

    @Test
    public void testIsCurrentRevisionEqiualThenMinimal() throws Exception {
        givenCurrentRevision("5");
        thenCurrentRevisionIsGreaterOrEqualThan("5");

        givenCurrentRevision("5.2");
        thenCurrentRevisionIsGreaterOrEqualThan("5.2");

        givenCurrentRevision("PS-5.2");
        thenCurrentRevisionIsGreaterOrEqualThan("5.2");
    }

    @Test
    public void testIsCurrentRevisionLowerThenMinimal() throws Exception {
        givenCurrentRevision("5");
        thenCurrentRevisionIsLowerThan("6");

        givenCurrentRevision("5.1");
        thenCurrentRevisionIsLowerThan("5.2");

        givenCurrentRevision("5.1");
        thenCurrentRevisionIsLowerThan("6.2");

        givenCurrentRevision("PS-5.1");
        thenCurrentRevisionIsLowerThan("6.2");
    }

    private void thenCurrentRevisionIsLowerThan(final String minimalVersion) {
        final Boolean currentVersionGreaterThen = revisionManager.isCurrentVersionGreaterThen(minimalVersion);

        assertThat(currentVersionGreaterThen, is(false));
    }

    private void thenCurrentRevisionIsGreaterOrEqualThan(final String minimalVersion) {
        final Boolean currentVersionGreaterThen = revisionManager.isCurrentVersionGreaterThen(minimalVersion);

        assertThat(currentVersionGreaterThen, is(true));
    }

    private void givenCurrentRevision(final String currentRevision) {
        doReturn(currentRevision).when(revisionManager).getCurrentVersion();
    }
}
