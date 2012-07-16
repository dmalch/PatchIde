package com.github.dmalch;

import com.google.common.io.Closeables;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;
import de.schlichtherle.truezip.file.TFileWriter;
import de.schlichtherle.truezip.file.TVFS;
import org.apache.commons.io.IOUtils;

import java.io.*;

import static java.text.MessageFormat.format;

public class JarUtils {

    public static void putIntoJar(final String patchFilePath, final File jarFile, final String dirInJar, final String targetFileName) {
        removeObsoleteFile(jarFile, dirInJar, targetFileName);
        createNewFile(patchFilePath, jarFile, dirInJar, targetFileName);
    }

    public static void extractFromJar(final File jarFile, final String dirInJar, final String targetFileName, final String fileNewName) {
        Writer writer = null;
        TFileReader reader = null;
        try {
            final TFile jar = new TFile(format("{0}/{1}/{2}", jarFile.getAbsolutePath(), dirInJar, targetFileName));
            reader = new TFileReader(jar);
            writer = new FileWriter(fileNewName);

            IOUtils.copy(reader, writer);
        } catch (Exception ignored) {
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(reader);
        }
    }

    private static void createNewFile(final String patchFilePath, final File jarFile, final String dirInJar, final String targetFileName) {
        Writer writer = null;
        try {
            final TFile jar = new TFile(format("{0}/{1}/{2}", jarFile.getAbsolutePath(), dirInJar, targetFileName));
            writer = new TFileWriter(jar);
            final FileReader input = new FileReader(patchFilePath);

            IOUtils.copy(input, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(writer);
        }
    }

    private static void removeObsoleteFile(final File jarFile, final String dirInJar, final String targetFileName) {
        try {
            final TFile jar = new TFile(format("{0}/{1}/{2}", jarFile.getAbsolutePath(), dirInJar, targetFileName));
            jar.rm();
            TVFS.umount();
        } catch (IOException ignored) {
        }
    }
}
