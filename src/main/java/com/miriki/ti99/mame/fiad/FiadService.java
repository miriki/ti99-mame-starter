package com.miriki.ti99.mame.fiad;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miriki.ti99.imagetools.domain.DiskFormat;
import com.miriki.ti99.imagetools.domain.DiskFormatPreset;
import com.miriki.ti99.imagetools.domain.Ti99File;
import com.miriki.ti99.imagetools.domain.Ti99Image;
import com.miriki.ti99.imagetools.domain.io.ImageFormatter;
import com.miriki.ti99.imagetools.fs.FileImporter;

public class FiadService {

    private static final Logger log = LoggerFactory.getLogger(FiadService.class);

    public boolean isFiad(Path absPath) {
        return absPath != null && Files.isDirectory(absPath);
    }

    public void prepareFiad(Path absPath, List<Path> tempDsks) throws IOException {
        if (isFiad(absPath)) {
            Path dsk = createDskFromFiad(absPath);
            tempDsks.add(dsk);
        }
    }

    public Path createDskFromFiad(Path fiadDir) throws IOException {
        Path dskPath = fiadDir.resolveSibling(fiadDir.getFileName().toString() + ".dsk");

        DiskFormatPreset preset = DiskFormatPreset.TI_DSDD;
        DiskFormat format = preset.getFormat();

        Ti99Image image = new Ti99Image(format);
        ImageFormatter.initialize(image);

        try (Stream<Path> stream = Files.list(fiadDir)) {
            stream.filter(Files::isRegularFile).forEach(file -> {
                try {
                    importTiFile(image, file);
                } catch (Exception ex) {
                    log.warn("Konnte Datei nicht importieren: {}", file, ex);
                }
            });
        }

        Files.write(dskPath, image.getRawData());
        log.trace("FIAD → DSK erzeugt: {}", dskPath);
        return dskPath;
    }

    public void importTiFile(Ti99Image image, Path hostFile) throws Exception {
        byte[] raw = Files.readAllBytes(hostFile);

        if (raw.length >= 128 && TiFilesHeader.isTiFilesHeader(raw)) {
            TiFilesHeader header = TiFilesHeader.parse(raw);
            byte[] content = Arrays.copyOfRange(raw, 128, raw.length);

            Ti99File ti = new Ti99File();
            ti.setFileName(header.getFileName());
            ti.setFileType(header.getFileType());
            ti.setRecordLength(header.getRecordLength());
            ti.setFlags(header.getFlags());
            ti.setContent(content);

            log.info("TIFILES erkannt: {} → {} (Type={}, RecLen={}, Flags={})",
                    hostFile.getFileName(),
                    ti.getFileName(),
                    ti.getFileType(),
                    ti.getRecordLength(),
                    ti.getFlags());

            FileImporter.importFile(image, ti);
        } else {
            log.info("Kein TIFILES-Header: {} → Import als PROGRAM", hostFile.getFileName());
            importAsProgram(image, hostFile);
        }
    }

    public void importAsProgram(Ti99Image image, Path hostFile) throws Exception {
        byte[] content = Files.readAllBytes(hostFile);

        Ti99File tiFile = new Ti99File();
        String baseName = hostFile.getFileName().toString().replaceAll("\\..*$", "").toUpperCase();
        if (baseName.length() > 10) baseName = baseName.substring(0, 10);

        tiFile.setFileName(baseName);
        tiFile.setFileType("PROGRAM");
        tiFile.setRecordLength(0);
        tiFile.setContent(content);

        FileImporter.importFile(image, tiFile);
    }
}
