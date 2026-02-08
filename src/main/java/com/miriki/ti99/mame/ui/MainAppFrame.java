package com.miriki.ti99.mame.ui;

import java.awt.EventQueue;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miriki.ti99.mame.domain.CartridgeEntryList;
import com.miriki.ti99.mame.domain.CassetteEntryList;
import com.miriki.ti99.mame.domain.FloppyEntryList;
import com.miriki.ti99.mame.domain.HarddiskEntryList;
import com.miriki.ti99.mame.dto.EmulatorOptionsDTO;
import com.miriki.ti99.mame.ui.builder.MainAppFrameBuilder;
import com.miriki.ti99.mame.ui.mamedevices.PebDevicesController;
import com.miriki.ti99.mame.ui.util.Listeners;
import com.miriki.ti99.mame.ui.util.UiDefaults;

/**
 * Main application window.
 * Keeps only high-level orchestration; delegates logic to helper classes.
 * All media lists are owned centrally here and shared with all subsystems.
 */
public class MainAppFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(MainAppFrame.class);

    // -------------------------------------------------------------------------
    // UI components
    // -------------------------------------------------------------------------

    private final MainAppFrameComponents ui;
    /*
    public MainAppFrameComponents getUi() {
        return ui;
    }
    */

    // -------------------------------------------------------------------------
    // Central media lists (single source of truth)
    // -------------------------------------------------------------------------

    private final FloppyEntryList floppyList;
    private final HarddiskEntryList harddiskList;
    private final CassetteEntryList cassetteList;
    private final CartridgeEntryList cartridgeList;

    // -------------------------------------------------------------------------
    // Subsystems
    // -------------------------------------------------------------------------

    private final MainAppFrameBuilder builder;
    private final MainAppFrameState state;
    private final MainAppFrameMediaUpdater media;
    private final MainAppFrameSettings settings;
    private final MainAppFrameI18n i18n;
    private final MainAppFrameCollect collector;

    // -------------------------------------------------------------------------
    // PEB controller
    // -------------------------------------------------------------------------

    private PebDevicesController ctlPebDevices;

    public PebDevicesController getPebDevicesController() {
        return ctlPebDevices;
    }

    public void setPebDevicesController(PebDevicesController ctl) {
        this.ctlPebDevices = ctl;
    }

    // -------------------------------------------------------------------------
    // Event suspension flag
    // -------------------------------------------------------------------------

    private boolean eventsSuspended = true;

    public boolean getEventsSuspended() {
        return eventsSuspended;
    }

    public void setEventsSuspended(boolean evtSusp) {
        this.eventsSuspended = evtSusp;
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public MainAppFrame() {

        setEventsSuspended(true);

        setTitle("MAME TI99 Starter");
        setBounds(100, 100, 1200, 675);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        UiDefaults.apply();

        // --- 1) Create central media lists (one instance per media type) -----
        this.floppyList = new FloppyEntryList();
        this.harddiskList = new HarddiskEntryList();
        this.cassetteList = new CassetteEntryList();
        this.cartridgeList = new CartridgeEntryList();

        // --- 2) Create UI components -----------------------------------------
        this.ui = new MainAppFrameComponents();
        setContentPane(ui.getContentPane());

        // --- 3) Create subsystems and inject shared lists + UI ---------------
        this.state = new MainAppFrameState(
                this,
                ui,
                floppyList,
                harddiskList,
                cassetteList,
                cartridgeList
        );

        this.media = new MainAppFrameMediaUpdater(
                ui,
                state,
                floppyList,
                harddiskList,
                cassetteList,
                cartridgeList
        );

        this.settings = new MainAppFrameSettings(
                this,
                ui,
                state,
                media
        );

        this.i18n = new MainAppFrameI18n(
                this,
                ui
        );

        this.collector = new MainAppFrameCollect(
                this,
                ui,
                state,
                media
        );

        this.builder = new MainAppFrameBuilder(
                this,
                ui
        );

        // --- 4) Build GUI ----------------------------------------------------
        builder.initGUI();

        // --- 5) Window close handling ----------------------------------------
        addWindowListener(Listeners.onCloseMainFrame(this));
    }

    // -------------------------------------------------------------------------
    // Application exit
    // -------------------------------------------------------------------------

    public void exitApplication() {
        settings.saveSettings();
        dispose();
        System.exit(0);
    }

    // -------------------------------------------------------------------------
    // Settings
    // -------------------------------------------------------------------------

    public void restoreSettings() {
        settings.restoreSettings();
    }

    public void saveSettings() {
        settings.saveSettings();
    }

    public void updateFrameTitle() {
        settings.updateFrameTitle();
    }

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    public void refreshUI() {
        i18n.refreshUI();
    }

    // -------------------------------------------------------------------------
    // Collect emulator options
    // -------------------------------------------------------------------------

    public EmulatorOptionsDTO collectEmulatorOptions() {
        return collector.collectEmulatorOptions();
    }

    // -------------------------------------------------------------------------
    // Expose media lists if needed elsewhere (optional)
    // -------------------------------------------------------------------------

    public FloppyEntryList getFloppyList() {
        return floppyList;
    }

    public String getSelectedFloppyPath(int drive) {

        JComboBox<String> cbx = switch (drive) {
            case 1 -> ui.cbxFlop1;
            case 2 -> ui.cbxFlop2;
            case 3 -> ui.cbxFlop3;
            case 4 -> ui.cbxFlop4;
            default -> null;
        };

        if (cbx == null) return null;

        String displayName = (String) cbx.getSelectedItem();
        if (displayName == null || displayName.equals(UiConstants.CBX_SEL_NONE)) {
            return null;
        }

        var entry = getFloppyList().findByDisplayName(displayName);
        if (entry == null) return null;

        return entry.getFullPath().toString();
    }

    public HarddiskEntryList getHarddiskList() {
        return harddiskList;
    }

    public CassetteEntryList getCassetteList() {
        return cassetteList;
    }

    public CartridgeEntryList getCartridgeList() {
        return cartridgeList;
    }

    // -------------------------------------------------------------------------
    // Main entry point
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            try {
                MainAppFrame frame = new MainAppFrame();
                frame.setVisible(true);

                SwingUtilities.invokeLater(() -> {
                    frame.builder.postGUI();

                    // At this point, media updater can safely scan media,
                    // because UI and lists are fully wired.
                    // If scanAllMedia() is currently called elsewhere, you can
                    // move it here for deterministic order.
                    // frame.media.scanAllMedia();

                    frame.setEventsSuspended(false);
                    frame.collectEmulatorOptions();
                });

            } catch (Exception e) {
                log.error("Error starting the application", e);
            }
        });
    }
}
