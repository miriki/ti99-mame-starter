package com.miriki.ti99.mame.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import com.miriki.ti99.dskimg.fiad.service.FiadService;
import com.miriki.ti99.fiad.service.FiadService;
import com.miriki.ti99.mame.dto.EmulatorOptionsDTO;
import com.miriki.ti99.mame.ui.UiConstants;

/**
 * Builds and executes the command line for launching the MAME TI‑99 emulator.
 * <p>
 * This class contains a large amount of compatibility logic for different
 * machine types (TI‑99/4A, Geneve, GenMod) and PEB device configurations.
 * <p>
 * The goal here is not to validate or interpret the DTO, but to translate it
 * into a correct MAME command line.
 */
public class EmulatorStart {

    private static final Logger log = LoggerFactory.getLogger(EmulatorStart.class);

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return (dot >= 0) ? name.substring(0, dot) : name;
    }

    // -------------------------------------------------------------------------
    // Command line construction
    // -------------------------------------------------------------------------

    /**
     * Builds the full MAME command line string from the given DTO.
     * <p>
     * This method contains the entire TI‑99/PEB/Geneve compatibility matrix.
     */
    @SuppressWarnings("unused")
    public static String emulatorOptionsConcatenate(EmulatorOptionsDTO dto) {

    	// log.debug( "emulatorOptionsConcatenate( dto={} )", dto.toString() );
        StringBuilder sb = new StringBuilder();

        // ---------------------------------------------------------------------
        // Local variables mirroring DTO fields
        // ---------------------------------------------------------------------

        String mame_AddOpt = "";
        String mame_RomPath = "";
        String mame_Machine = "";
        String mame_GromPort = "";
        String mame_Cartridge = "";
        String mame_Joystick = "";
        String mame_IoPort = "";
        String[] mame_PebDevices = new String[9];

        int Fdd_Max = 0;
        int Hdd_Max = 0;

        // ---------------------------------------------------------------------
        // Basic options
        // ---------------------------------------------------------------------

        if (!dto.mame_AddOpt.trim().isEmpty()) {
            mame_AddOpt = dto.mame_AddOpt;
        }

        List<String> paths = new ArrayList<>();
        if (!dto.mame_RomPath.isEmpty()) paths.add(dto.mame_RomPath);
        if (!dto.mame_CartPath.isEmpty()) paths.add(dto.mame_CartPath);
        mame_RomPath = String.join(";", paths);

        if (!UiConstants.CBX_SEL_NONE.equals(dto.mame_Machine.trim())) {
            mame_Machine = dto.mame_Machine.trim();
        }

        if (!UiConstants.CBX_SEL_NONE.equals(dto.mame_JoyPort.trim())) {
            mame_Joystick = dto.mame_JoyPort.trim();
        }

        if (!UiConstants.CBX_SEL_NONE.equals(dto.mame_GromPort.trim())) {
            mame_GromPort = dto.mame_GromPort.trim();
        }

        if ("geneve".equals(mame_Machine) || "genmod".equals(mame_Machine)) {
            mame_GromPort = "";
        }

        // ---------------------------------------------------------------------
        // Cartridge handling
        // ---------------------------------------------------------------------

        if ("geneve".equals(dto.mame_Machine) || "genmod".equals(dto.mame_Machine)) {
            if (dto.cartEntry != null) {
                log.warn("selected 'Cartridge' is ignored due to incompatibility with machine '{}'",
                        dto.mame_Machine);
            }
        } else {
            if (dto.cartEntry != null) {
                String ext = dto.cartEntry.getMediaExt();

                if ("zip".equalsIgnoreCase(ext)) {
                    mame_Cartridge = stripExtension(dto.cartEntry.getDisplayName());
                } else {
                    mame_Cartridge = dto.cartPathP.toString();
                }
            }
        }

        // ---------------------------------------------------------------------
        // IO-Port handling (PEB, speech, splitter, arcturus)
        // ---------------------------------------------------------------------

        if (!UiConstants.CBX_SEL_NONE.equals(dto.mame_IoPort.trim())) {
            mame_IoPort = dto.mame_IoPort.trim();

            switch (mame_IoPort) {

                case "peb" -> {
                    for (int i = 2; i < 9; i++) {

                        String chk = dto.PebDevices[i].trim();
                        if (UiConstants.CBX_SEL_NONE.equals(chk)) {
                            continue;
                        }

                        mame_PebDevices[i] = " -ioport:peb:slot" + i + " " + chk;
                        String pebslot = " -ioport:peb:slot" + i + ":" + chk + ":";

                        switch (chk) {

                            case "32kmem" -> {
                                // no sub‑options
                            }

                            case "bwg" -> {
                                if ("geneve".equals(mame_Machine) || "genmod".equals(mame_Machine)) {
                                    log.warn("selected 'bwg' device is ignored due to incompatibility with machine 'Geneve' !");
                                    mame_PebDevices[i] = "";
                                } else {
                                    if (!dto.bwg_0.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "0 " + dto.bwg_0.trim();
                                    if (!dto.bwg_1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "1 " + dto.bwg_1.trim();
                                    if (!dto.bwg_2.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "2 " + dto.bwg_2.trim();
                                    if (!dto.bwg_3.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "3 " + dto.bwg_3.trim();
                                    Fdd_Max = 4;
                                }
                            }

                            case "ccdcc" -> {
                                if (!dto.ccdcc_0.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "0 " + dto.ccdcc_0.trim();
                                if (!dto.ccdcc_1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "1 " + dto.ccdcc_1.trim();
                                if (!dto.ccdcc_2.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "2 " + dto.ccdcc_2.trim();
                                if (!dto.ccdcc_3.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "3 " + dto.ccdcc_3.trim();
                                Fdd_Max = 4;
                            }

                            case "ccfdc" -> {
                                if (!dto.ccfdc_0.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "0 " + dto.ccfdc_0.trim();
                                if (!dto.ccfdc_1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "1 " + dto.ccfdc_1.trim();
                                if (!dto.ccfdc_2.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "2 " + dto.ccfdc_2.trim();
                                if (!dto.ccfdc_3.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "3 " + dto.ccfdc_3.trim();
                                Fdd_Max = 4;
                            }

                            case "ddcc1" -> {
                                if (!dto.ddcc1_0.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "0 " + dto.ddcc1_0.trim();
                                if (!dto.ddcc1_1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "1 " + dto.ddcc1_1.trim();
                                if (!dto.ddcc1_2.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "2 " + dto.ddcc1_2.trim();
                                if (!dto.ddcc1_3.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "3 " + dto.ddcc1_3.trim();
                                Fdd_Max = 4;
                            }

                            case "evpc" -> {
                                if ("geneve".equals(mame_Machine) || "genmod".equals(mame_Machine)) {
                                    log.warn("selected 'evpc' device is ignored due to incompatibility with machine 'Geneve' !");
                                    mame_PebDevices[i] = "";
                                } else {
                                    if (!dto.evpc_colorbus.equals(UiConstants.CBX_SEL_NONE)) {
                                        mame_PebDevices[i] += pebslot + "colorbus " + dto.evpc_colorbus.trim();
                                    }
                                }
                            }

                            case "hfdc" -> {
                                if (!dto.hfdc_f1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "f1 " + dto.hfdc_f1.trim();
                                if (!dto.hfdc_f2.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "f2 " + dto.hfdc_f2.trim();
                                if (!dto.hfdc_f3.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "f3 " + dto.hfdc_f3.trim();
                                if (!dto.hfdc_f4.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "f4 " + dto.hfdc_f4.trim();
                                if (!dto.hfdc_h1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "h1 " + dto.hfdc_h1.trim();
                                if (!dto.hfdc_h2.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "h2 " + dto.hfdc_h2.trim();
                                if (!dto.hfdc_h3.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "h3 " + dto.hfdc_h3.trim();
                                Fdd_Max = 4;
                                Hdd_Max = 3;
                            }

                            case "ide" -> {
                                if (!dto.ide_0.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "ata:0 " + dto.ide_0.trim();
                                if (!dto.ide_1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "ata:1 " + dto.ide_1.trim();
                            }

                            case "tifdc" -> {
                                if (!dto.tifdc_0.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "0 " + dto.tifdc_0.trim();
                                if (!dto.tifdc_1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "1 " + dto.tifdc_1.trim();
                                if (!dto.tifdc_2.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "2 " + dto.tifdc_2.trim();
                                Fdd_Max = 3;
                            }

                            case "whtscsi" -> {
                                if (!dto.whtscsi_0.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "scsibus:0 " + dto.whtscsi_0.trim();
                                if (!dto.whtscsi_1.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "scsibus:1 " + dto.whtscsi_1.trim();
                                if (!dto.whtscsi_2.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "scsibus:2 " + dto.whtscsi_2.trim();
                                if (!dto.whtscsi_3.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "scsibus:3 " + dto.whtscsi_3.trim();
                                if (!dto.whtscsi_4.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "scsibus:4 " + dto.whtscsi_4.trim();
                                if (!dto.whtscsi_5.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "scsibus:5 " + dto.whtscsi_5.trim();
                                if (!dto.whtscsi_6.equals(UiConstants.CBX_SEL_NONE)) mame_PebDevices[i] += pebslot + "scsibus:6 " + dto.whtscsi_6.trim();
                            }

                            default -> {
                                // no sub‑options
                            }
                        }
                    }
                }

                case "speechsyn" -> {
                    // no sub‑options
                }

                case "splitter" -> {
                    // ...
                }

                case "arcturus" -> {
                    // ...
                }

                default -> {
                    // ignore
                }
            }
        }

        // ---------------------------------------------------------------------
        // Build command line
        // ---------------------------------------------------------------------

        if (!mame_Machine.isEmpty()) sb.append(mame_Machine);
        if (!mame_AddOpt.isEmpty()) sb.append(" ").append(mame_AddOpt);
        if (!mame_RomPath.isEmpty()) sb.append(" -rp ").append(mame_RomPath);

        if (!mame_GromPort.isEmpty()) {
            if ("geneve".equals(mame_Machine) || "genmod".equals(mame_Machine)) {
                log.warn("selected 'GromPort' is ignored due to incompatibility with machine 'Geneve' !");
            } else {
                sb.append(" -gromport ").append(mame_GromPort);
            }
        }

        if (!mame_Cartridge.isEmpty()) {
            sb.append(" -cart ").append(mame_Cartridge);
        }

        if (!mame_Joystick.isEmpty()) {
            sb.append(" -joyport ").append(mame_Joystick);
        }

        if (!mame_IoPort.isEmpty()) {
            sb.append(" -ioport ").append(mame_IoPort);

            if ("peb".equals(mame_IoPort)) {
                for (int i = 2; i < 9; i++) {
                    if (mame_PebDevices[i] != null && !mame_PebDevices[i].isEmpty()) {
                        sb.append(mame_PebDevices[i]);
                    }
                }
            }
        }

        // log.trace( "  calling appendMedia with 'sb', '-flop1' and dto.fddPathRel1='{}'", dto.fddPathRel1 );
        appendMedia(sb, "-flop1", dto.fddPathP1, dto.fddPathRel1);
        appendMedia(sb, "-flop2", dto.fddPathP2, dto.fddPathRel2);
        appendMedia(sb, "-flop3", dto.fddPathP3, dto.fddPathRel3);
        appendMedia(sb, "-flop4", dto.fddPathP4, dto.fddPathRel4);

        appendMedia(sb, "-hard1", dto.hddPathP1, dto.hddPathRel1);
        appendMedia(sb, "-hard2", dto.hddPathP2, dto.hddPathRel2);
        appendMedia(sb, "-hard3", dto.hddPathP3, dto.hddPathRel3);

        if ("geneve".equals(dto.mame_Machine) || "genmod".equals(dto.mame_Machine)) {
            log.warn("selected 'Cassette' is ignored due to incompatibility with machine '{}'",
                    dto.mame_Machine);
        } else {
            appendMedia(sb, "-cass1", dto.cassPathP1, dto.cassPathRel1);
            appendMedia(sb, "-cass2", dto.cassPathP2, dto.cassPathRel2);
        }

        String result = sb.toString();

        if ("geneve".equals(mame_Machine) || "genmod".equals(mame_Machine)) {
            result = result.replace(" -ioport peb", "");
            result = result.replace(" -ioport:peb:", " -peb:");
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // Media helper
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------
    // FIAD → DSK Parameterauflösung
    // -------------------------------------------------------------
    
    private static void appendMedia(StringBuilder sb, String option, Path absPath, Path relPath) {
        if (absPath == null || relPath == null) {
            // log.trace("appendMedia: skipped '{}': absPath or relPath is null", option);
            return;
        }

        // log.trace("appendMedia: absPath='{}', relPath='{}'", absPath, relPath);
        // log.trace("appendMedia: isDirectory(absPath) = {}", Files.isDirectory(absPath));
        String param;

        if (Files.isDirectory(absPath)) {
            String name = relPath.getFileName().toString();
            Path parent = relPath.getParent();
            param = parent != null
                ? parent.resolve(name + ".dsk").toString()
                : name + ".dsk";
            // log.trace("[directory] param: '{}'", param);
        } else {
            param = relPath.toString();
            // log.trace("[file] param: '{}'", param);
        }

        sb.append(" ").append(option).append(" ").append(param);
    }

    // -------------------------------------------------------------------------
    // Process execution
    // -------------------------------------------------------------------------

    /*
    private static boolean isFiad(Path absPath) {
        return absPath != null && Files.isDirectory(absPath);
    }
    */

    /**
     * Launches the emulator using the DTO configuration.
     */
    public static boolean emulatorStartProgram(EmulatorOptionsDTO dto) {

        boolean result = true;

        String mame_WorkingPath = dto.mame_WorkingPath;
        String mame_Executable = dto.mame_Executable;
        String mame_Parameter = emulatorOptionsConcatenate(dto);

        // Liste der erzeugten .dsk-Dateien (für späteres Löschen)
        List<Path> tempDsks = new ArrayList<>();

        try {
            // ---------------------------------------------------------
            // 1) FIAD → DSK erzeugen (aber DTO NICHT ändern!)
            // ---------------------------------------------------------
            FiadService fiad = new FiadService();
            // prepareFiad(dto.fddPathP1, tempDsks);
            fiad.prepareFiad(dto.fddPathP1, tempDsks);
            // prepareFiad(dto.fddPathP2, tempDsks);
            fiad.prepareFiad(dto.fddPathP2, tempDsks);
            // prepareFiad(dto.fddPathP3, tempDsks);
            fiad.prepareFiad(dto.fddPathP3, tempDsks);
            // prepareFiad(dto.fddPathP4, tempDsks);
            fiad.prepareFiad(dto.fddPathP4, tempDsks);

            // ---------------------------------------------------------
            // 2) MAME starten
            // ---------------------------------------------------------
            String fullExecutablePath = mame_WorkingPath + File.separator + mame_Executable;

            List<String> command = new ArrayList<>();
            command.add(fullExecutablePath);

            if (mame_Parameter != null && !mame_Parameter.isEmpty()) {
                command.addAll(Arrays.asList(mame_Parameter.split(" ")));
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(mame_WorkingPath));
            pb.inheritIO();

            Process process = pb.start();
            process.waitFor();

        } catch (IOException ex) {
            log.error("Programm kann nicht gestartet werden!", ex);
            result = false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Emulator wurde unterbrochen", e);

        } finally {
            // ---------------------------------------------------------
            // 3) Aufräumen: erzeugte .dsk löschen
            // ---------------------------------------------------------
            for (Path p : tempDsks) {
                try {
                    Files.deleteIfExists(p);
                    log.trace("Temp .dsk gelöscht: {}", p);
                } catch (IOException ex) {
                    log.warn("Konnte temp .dsk nicht löschen: {}", p);
                }
            }
        }

        return result;
    }

}
