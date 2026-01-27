package com.miriki.ti99.mame.ui.util;

import java.awt.Window;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miriki.ti99.dskimg.domain.DiskFormat;
import com.miriki.ti99.dskimg.domain.DiskFormatPreset;
import com.miriki.ti99.dskimg.domain.Ti99File;
import com.miriki.ti99.dskimg.domain.Ti99Image;
import com.miriki.ti99.dskimg.domain.enums.FileType;
import com.miriki.ti99.dskimg.domain.io.ImageFormatter;
import com.miriki.ti99.dskimg.fs.FileImporter;
import com.miriki.ti99.mame.persistence.SettingsPathRegistry;
import com.miriki.ti99.mame.persistence.SettingsUsageRegistry;
import com.miriki.ti99.mame.ui.MainAppFrame;
import com.miriki.ti99.mame.tools.EmulatorStart;
import com.miriki.ti99.mame.tools.FileTools;

/**
 * Collection of reusable Swing listeners used throughout the UI.
 */
public final class Listeners {

    private static final Logger log = LoggerFactory.getLogger(Listeners.class);

    private Listeners() {}

    // -------------------------------------------------------------------------
    // COMBOBOX LISTENER
    // -------------------------------------------------------------------------

    /**
     * Fires only when the selected item changes and events are not suspended.
     */
    public static ActionListener comboBoxChange(JComboBox<?> combo, ActionListener delegate) {

        final Object[] lastValue = { null };

        return e -> {

            Window wnd = SwingUtilities.getWindowAncestor(combo);
            if (!(wnd instanceof MainAppFrame frame)) return;

            if (frame.getEventsSuspended()) return;

            Object newValue = combo.getSelectedItem();

            if (!Objects.equals(lastValue[0], newValue)) {
                lastValue[0] = newValue;
                delegate.actionPerformed(e);
            }
        };
    }

    // -------------------------------------------------------------------------
    // DOCUMENT LISTENER
    // -------------------------------------------------------------------------

    /**
     * Converts document changes into ActionListener-style events.
     */
    public static DocumentListener documentAsAction(JTextField field, ActionListener delegate) {

        return new DocumentListener() {

            private void fire(String command) {

                Window w = SwingUtilities.getWindowAncestor(field);

                if (w instanceof MainAppFrame frame && !frame.getEventsSuspended()) {
                    delegate.actionPerformed(
                        new ActionEvent(field, ActionEvent.ACTION_PERFORMED, command)
                    );
                }
            }

            @Override public void changedUpdate(DocumentEvent e) { fire("changed"); }
            @Override public void insertUpdate(DocumentEvent e)  { fire("insert"); }
            @Override public void removeUpdate(DocumentEvent e)  { fire("remove"); }
        };
    }

    // -------------------------------------------------------------------------
    // FOCUS LISTENER
    // -------------------------------------------------------------------------

    /**
     * Normalizes a multi-path field when focus is lost.
     */
    public static FocusListener normalizeOnFocusLost(JTextField field) {

        return new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {

                String cleaned = FileTools.normalizeMultiPath(field.getText());

                if (!cleaned.equals(field.getText())) {

                    Window wnd = SwingUtilities.getWindowAncestor(field);

                    if (wnd instanceof MainAppFrame frame) {
                        Listeners.withEventsSuspended(frame, () -> field.setText(cleaned));
                    }
                }
            }
        };
    }

    // -------------------------------------------------------------------------
    // WINDOW LISTENERS
    // -------------------------------------------------------------------------

    /**
     * Enables events and triggers initial option collection.
     */
    public static WindowListener onOpenMainFrame(MainAppFrame frame) {

        return new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                frame.setEventsSuspended(false);
                SwingUtilities.invokeLater(frame::collectEmulatorOptions);
            }
        };
    }

    /**
     * Delegates to the application's exit logic.
     */
    public static WindowListener onCloseMainFrame(MainAppFrame frame) {

        return new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                frame.exitApplication();
            }
        };
    }

    // -------------------------------------------------------------------------
    // BUTTON LISTENERS
    // -------------------------------------------------------------------------

    /**
     * Starts the emulator and updates usage statistics.
     */
    public static MouseAdapter startEmulatorClick(MainAppFrame frame) {

        return new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                EmulatorStart.emulatorStartProgram(frame.collectEmulatorOptions());

                Path current = SettingsPathRegistry.getCurrent();
                SettingsUsageRegistry.increment(current);
                SettingsUsageRegistry.updateLastUsed(SettingsPathRegistry.getCurrent());
            }
        };
    }

    /**
     * Debug helper for FIAD testing.
     */
    public static MouseAdapter testFiadCreate() {

        return new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                try {

                    Path targetDir = Path.of("C:/Users/mritt/AppData/Roaming/TI99MAME/ti99_fiad/chess");
                    Path imagePath = targetDir.resolve("chess.dsk");

                    DiskFormatPreset preset = DiskFormatPreset.TI_DSDD;
                    DiskFormat format = preset.getFormat();
                    Ti99Image image = new Ti99Image(format);
                    ImageFormatter.initialize(image);

                    importSvg(image, targetDir, "br.svg", "BR");
                    importSvg(image, targetDir, "bp.svg", "BP");
                    importSvg(image, targetDir, "wq.svg", "WQ");
                    importSvg(image, targetDir, "bk.svg", "BK");
                    importSvg(image, targetDir, "wn.svg", "WN");

                    targetDir = Path.of("C:/Users/mritt/AppData/Roaming/TI99MAME/ti99_fiad/ti99xdt");
                    importSvg(image, targetDir, "xbas99.py", "XBAS");

                    Files.write(imagePath, image.getRawData());

                    log.info("Import abgeschlossen: {}", imagePath);

                } catch (Exception ex) {
                    log.error("Fehler beim Erstellen der DSK-Images", ex);
                }
            }

            private void importSvg(Ti99Image image, Path dir, String fileName, String tiName) throws Exception {

                Path hostFile = dir.resolve(fileName);
                byte[] content = Files.readAllBytes(hostFile);

                Ti99File tiFile = new Ti99File();
                tiFile.setFileName(tiName);
                tiFile.setType(FileType.PGM);
                tiFile.setRecordLength(0);
                tiFile.setContent(content);

                FileImporter.importFile(image, tiFile);
            }
        };
    }

    // -------------------------------------------------------------------------
    // EVENT SUSPENSION
    // -------------------------------------------------------------------------

    /**
     * Runs an action with UI events temporarily suspended.
     */
    public static void withEventsSuspended(MainAppFrame frame, Runnable action) {

        boolean old = frame.getEventsSuspended();
        frame.setEventsSuspended(true);

        try {
            action.run();
        } finally {
            frame.setEventsSuspended(old);
        }
    }
}
