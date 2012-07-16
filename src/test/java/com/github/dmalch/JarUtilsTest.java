package com.github.dmalch;

import com.google.common.io.Closeables;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileWriter;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

import static com.github.dmalch.JarUtils.putIntoJar;
import static java.text.MessageFormat.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class JarUtilsTest extends AbstractPatchTest {

    public static final String ARCHIVE_JAR = "out/test/archive.jar";
    public static final String DIR = "dir";

    @Test
    public void testReplaceFileInJar() throws Exception {
        final File file = givenPatchFile("");
        final File jarFile = givenEmptyJarFile(file.getName(), ARCHIVE_JAR, DIR);

        whenPutFileIntoJarFile(file, jarFile, DIR);

        thenFileIsInJarFile(file, jarFile, DIR);
    }

    private File givenEmptyJarFile(final String fileTxt, final String archiveZip, final String dir) {
        Writer writer = null;
        try {
            final File entry = new TFile(format("{0}/{1}/{2}", archiveZip, dir, fileTxt));
            writer = new TFileWriter(entry);
            writer.write("Hello world!\n");
        } catch (FileNotFoundException e) {
            throw new NotImplementedException();
        } catch (IOException e) {
            throw new NotImplementedException();
        } finally {
            Closeables.closeQuietly(writer);
        }

        return new File(ARCHIVE_JAR);
    }

    private void whenPutFileIntoJarFile(final File file, final File jarFile, final String dirInJar) {
        putIntoJar(file, JarUtils.jarFile(jarFile, dirInJar, file.getName()));
    }

    private void thenFileIsInJarFile(final File expectedFile, final File jarFile, final String dir) {
        final String actual = readFileContent(new TFile(format("{0}/{1}/{2}", jarFile.getAbsolutePath(), dir, expectedFile.getName())));
        final String expected = readFileContent(new File(expectedFile.getAbsolutePath()));

        assertThat(actual, equalTo(expected));
    }
}
