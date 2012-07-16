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

    public static void putIntoJar(final File patchFile, final TFile jarEntry) {
        removeObsoleteFile(jarEntry);
        createNewFile(patchFile, jarEntry);
    }

    public static void extractFromJar(final File fileNew, final TFile jarEntry) {
        Writer writer = null;
        TFileReader reader = null;
        try {
            reader = new TFileReader(jarEntry);
            writer = new FileWriter(fileNew);

            IOUtils.copy(reader, writer);
        } catch (Exception ignored) {
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(reader);
        }
    }

    public static TFile jarFile(final File jarFile, final String dirInJar, final String targetFileName) {
        return new TFile(format("{0}/{1}/{2}", jarFile.getAbsolutePath(), dirInJar, targetFileName));
    }

    private static void createNewFile(final File patchFilePath, final TFile jar) {
        Writer writer = null;
        try {
            writer = new TFileWriter(jar);
            final FileReader input = new FileReader(patchFilePath);

            IOUtils.copy(input, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(writer);
        }
    }

    private static void removeObsoleteFile(final TFile jar) {
        try {
            jar.rm();
            TVFS.umount();
        } catch (IOException ignored) {
        }
    }
}
