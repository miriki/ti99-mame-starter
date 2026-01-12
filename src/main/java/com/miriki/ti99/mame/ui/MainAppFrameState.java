package com.miriki.ti99.mame.ui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miriki.ti99.mame.domain.*;
import com.miriki.ti99.mame.dto.*;
import com.miriki.ti99.mame.tools.EmulatorStart;

/**
 * Holds UI state, slot rules, pending selections and DTO-building logic.
 * Extracted from MainAppFrame for clarity and modularity.
 */
public class MainAppFrameState {

    private static final Logger log = LoggerFactory.getLogger(MainAppFrameState.class);

    private final MainAppFrame frame;
    private final MainAppFrameComponents ui;

    // Pending selections for ComboBoxes (restored after model rebuild)
    private final Map<JComboBox<?>, String> pendingSelections = new HashMap<>();

    // Shared media lists (injected from MainAppFrame)
    private final FloppyEntryList floppyList;
    private final HarddiskEntryList harddiskList;
    private final CassetteEntryList cassetteList;
    private final CartridgeEntryList cartridgeList;

    // -------------------------------------------------------------------------
    // Constructor – receives shared lists from MainAppFrame
    // -------------------------------------------------------------------------
    public MainAppFrameState(
            MainAppFrame frame,
            MainAppFrameComponents ui,
            FloppyEntryList floppyList,
            HarddiskEntryList harddiskList,
            CassetteEntryList cassetteList,
            CartridgeEntryList cartridgeList
    ) {
        this.frame = frame;
        this.ui = ui;

        this.floppyList = floppyList;
        this.harddiskList = harddiskList;
        this.cassetteList = cassetteList;
        this.cartridgeList = cartridgeList;
    }

    // -------------------------------------------------------------------------
    // Pending selections
    // -------------------------------------------------------------------------

    public void setPendingSelection(JComboBox<?> cbx, String value) {
        pendingSelections.put(cbx, value);
    }

    public String getPendingSelection(JComboBox<?> cbx) {
        return pendingSelections.get(cbx);
    }

    // -------------------------------------------------------------------------
    // Model helpers
    // -------------------------------------------------------------------------

    public boolean modelContains(ComboBoxModel<String> model, String value) {
        for (int i = 0; i < model.getSize(); i++) {
            if (Objects.equals(model.getElementAt(i), value)) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Media path resolution
    // -------------------------------------------------------------------------

    public Path resolveMediaPath(MediaEntryList<?> list, String displayName) {
        // log.debug("resolveMediaPath( list={}, displayName='{}' )", list.getDisplayNames(), displayName);

        if (displayName == null || displayName.equals(UiConstants.CBX_SEL_NONE)) {
            return null;
        }

        // log.trace("  looking for '{}' in list.findByDisplayName(displayName) ...", displayName);
        // MediaEntry entry = list.findByDisplayName(displayName);

        return list.resolveMediaPath(displayName);
        // return entry != null ? entry.getFullPath() : null;
    }

    // -------------------------------------------------------------------------
    // Slot rules
    // -------------------------------------------------------------------------

    public void setSlot(JComboBox<?> cbx, int index, boolean enabled) {
        cbx.setSelectedIndex(index);
        cbx.setEnabled(enabled);
    }

    public void removeEvpcFromSlots(JComboBox<?>... slots) {
        for (JComboBox<?> slot : slots) {
            if ("evpc".equals(slot.getSelectedItem().toString())) {
                slot.setSelectedIndex(0);
            }
        }
    }

    public void applyMachineRules(String machine,
                                  JComboBox<?> cbxIoPort,
                                  JComboBox<?> cbxSlot1,
                                  JComboBox<?> cbxSlot2,
                                  JComboBox<?>... otherSlots) {

        switch (machine) {

            case "TI99_4ev":
                setSlot(cbxIoPort, 1, false);
                setSlot(cbxSlot1, 2, false);
                setSlot(cbxSlot2, 6, false);
                break;

            case "geneve":
            case "genmod":
                setSlot(cbxIoPort, 1, false);
                setSlot(cbxSlot1, 3, false);
                cbxSlot2.setEnabled(true);
                removeEvpcFromSlots(otherSlots);
                break;

            default:
                cbxIoPort.setEnabled(true);
                setSlot(cbxSlot1, 2, false);
                cbxSlot2.setEnabled(true);
                removeEvpcFromSlots(otherSlots);
                break;
        }
    }

    public void applyIoPortRules(JComboBox<?> cbxIoPort, JComboBox<?> cbxSlot1) {
        if (cbxIoPort.getSelectedIndex() == 0) {
            setSlot(cbxSlot1, 1, false);
        }
    }

    // -------------------------------------------------------------------------
    // DTO builder
    // -------------------------------------------------------------------------

    public EmulatorOptionsDTO buildDTOFromUI() {

        // log.debug("buildDTOFromUI()");

        final String cbxSelNone = UiConstants.CBX_SEL_NONE;

        EmulatorOptionsDTO result = new EmulatorOptionsDTO();

        JComboBox<?>[] slots = {
                ui.cbxSlot2, ui.cbxSlot3, ui.cbxSlot4,
                ui.cbxSlot5, ui.cbxSlot6, ui.cbxSlot7, ui.cbxSlot8
        };

        // ---------------------------------------------------------------------
        // Base paths
        // ---------------------------------------------------------------------

        result.mame_WorkingPath = ui.txtWorkingDir.getText().trim();
        Path workingPath = Paths.get(result.mame_WorkingPath);

        result.mame_Executable = ui.txtExecutable.getText().trim();
        result.mame_RomPath = ui.txtRomPath.getText().trim();
        result.mame_CartPath = ui.txtCartPath.getText().trim();
        result.mame_DskPath = ui.txtFddPath.getText().trim();
        // log.trace( "  [dto].mame_DskPath='{}'", result.mame_DskPath );
        result.mame_FiadPath = ui.txtFiadPath.getText().trim();
        result.mame_WdsPath = ui.txtHddPath.getText().trim();
        // log.trace( "  [dto].mame_WdsPath='{}'", result.mame_WdsPath );
        result.mame_CsPath = ui.txtCassPath.getText().trim();

        // ---------------------------------------------------------------------
        // Main machine options
        // ---------------------------------------------------------------------

        result.mame_Machine = ui.cbxMachine.getSelectedItem().toString();
        result.mame_GromPort = ui.cbxGromPort.getSelectedItem().toString();
        result.mame_JoyPort = ui.cbxJoyPort.getSelectedItem().toString();

        frame.setEventsSuspended(true);
        applyMachineRules(result.mame_Machine, ui.cbxIoPort, ui.cbxSlot1, ui.cbxSlot2, slots);
        applyIoPortRules(ui.cbxIoPort, ui.cbxSlot1);
        frame.setEventsSuspended(false);

        result.mame_IoPort = ui.cbxIoPort.getSelectedItem().toString();

        // ---------------------------------------------------------------------
        // Cartridge
        // ---------------------------------------------------------------------

        result.cartDisplayName = ui.cbxCartridge.getSelectedItem().toString();
        result.cartEntry = cartridgeList.findByDisplayName(result.cartDisplayName);
        result.cartPathP = resolveMediaPath(cartridgeList, result.cartDisplayName);

        // ---------------------------------------------------------------------
        // Floppy
        // ---------------------------------------------------------------------

        String dsk1 = ui.cbxFlop1.getSelectedItem().toString();
        // log.trace( "  dsk1='{}'", dsk1 );
        String dsk2 = ui.cbxFlop2.getSelectedItem().toString();
        String dsk3 = ui.cbxFlop3.getSelectedItem().toString();
        String dsk4 = ui.cbxFlop4.getSelectedItem().toString();

        result.mame_DSK1 = dsk1;
        // log.trace( "  [dto].mame_DSK1='{}'", result.mame_DSK1 );
        result.mame_DSK2 = dsk2;
        result.mame_DSK3 = dsk3;
        result.mame_DSK4 = dsk4;

        result.fddPathP1 = resolveMediaPath(floppyList, dsk1);
        // log.trace( "  [dto].fddPathP1='{}'", result.fddPathP1 );
        result.fddPathP2 = resolveMediaPath(floppyList, dsk2);
        result.fddPathP3 = resolveMediaPath(floppyList, dsk3);
        result.fddPathP4 = resolveMediaPath(floppyList, dsk4);

        result.fddPathRel1 = floppyList.resolveMediaRelativePath(dsk1, workingPath);
        // log.trace( "  [dto].fddPathRel1='{}'", result.fddPathRel1 );
        result.fddPathRel2 = floppyList.resolveMediaRelativePath(dsk2, workingPath);
        result.fddPathRel3 = floppyList.resolveMediaRelativePath(dsk3, workingPath);
        result.fddPathRel4 = floppyList.resolveMediaRelativePath(dsk4, workingPath);

        // ---------------------------------------------------------------------
        // Harddisk
        // ---------------------------------------------------------------------

        String wds1 = ui.cbxHard1.getSelectedItem().toString();
        // log.trace( "  wds1='{}'", wds1 );
        String wds2 = ui.cbxHard2.getSelectedItem().toString();
        String wds3 = ui.cbxHard3.getSelectedItem().toString();

        result.mame_WDS1 = wds1;
        // log.trace( "  [dto].mame_WDS1='{}'", result.mame_WDS1 );
        result.mame_WDS2 = wds2;
        result.mame_WDS3 = wds3;

        result.hddPathP1 = resolveMediaPath(harddiskList, wds1);
        // log.trace( "  [dto].hddPathP1='{}'", result.hddPathP1 );
        result.hddPathP2 = resolveMediaPath(harddiskList, wds2);
        result.hddPathP3 = resolveMediaPath(harddiskList, wds3);

        result.hddPathRel1 = harddiskList.resolveMediaRelativePath(wds1, workingPath);
        // log.trace( "  [dto].hddPathRel1='{}'", result.hddPathRel1 );
        result.hddPathRel2 = harddiskList.resolveMediaRelativePath(wds2, workingPath);
        result.hddPathRel3 = harddiskList.resolveMediaRelativePath(wds3, workingPath);

        // ---------------------------------------------------------------------
        // Cassette
        // ---------------------------------------------------------------------

        String cass1 = ui.cbxCass1.getSelectedItem().toString();
        String cass2 = ui.cbxCass2.getSelectedItem().toString();

        result.mame_CS1 = cass1;
        result.mame_CS2 = cass2;

        result.cassPathP1 = resolveMediaPath(cassetteList, cass1);
        result.cassPathP2 = resolveMediaPath(cassetteList, cass2);

        result.cassPathRel1 = cassetteList.resolveMediaRelativePath(cass1, workingPath);
        result.cassPathRel2 = cassetteList.resolveMediaRelativePath(cass2, workingPath);

        // ---------------------------------------------------------------------
        // PEB slots
        // ---------------------------------------------------------------------

        List<String> selectedCards = new ArrayList<>();
        Set<String> seenDevices = new HashSet<>();

        selectedCards.add("peb");

        result.PebDevices[1] = ui.cbxSlot1.getSelectedItem().toString();
        result.PebDevices[2] = ui.cbxSlot2.getSelectedItem().toString();
        result.PebDevices[3] = ui.cbxSlot3.getSelectedItem().toString();
        result.PebDevices[4] = ui.cbxSlot4.getSelectedItem().toString();
        result.PebDevices[5] = ui.cbxSlot5.getSelectedItem().toString();
        result.PebDevices[6] = ui.cbxSlot6.getSelectedItem().toString();
        result.PebDevices[7] = ui.cbxSlot7.getSelectedItem().toString();
        result.PebDevices[8] = ui.cbxSlot8.getSelectedItem().toString();

        for (JComboBox<?> slot : slots) {

            String sel = slot.getSelectedItem().toString();

            if (!cbxSelNone.equals(sel)) {

                if (!seenDevices.add(sel)) {
                    log.warn("Device '{}' was selected more than once, removed!", sel);
                    slot.setSelectedIndex(0);
                }

                switch (sel) {

                    case "bwg":
                        result.bwg_0 = ui.cbxBwg0.getSelectedItem().toString();
                        result.bwg_1 = ui.cbxBwg1.getSelectedItem().toString();
                        result.bwg_2 = ui.cbxBwg2.getSelectedItem().toString();
                        result.bwg_3 = ui.cbxBwg3.getSelectedItem().toString();
                        break;

                    case "ccdcc":
                        result.ccdcc_0 = ui.cbxCcdcc0.getSelectedItem().toString();
                        result.ccdcc_1 = ui.cbxCcdcc1.getSelectedItem().toString();
                        result.ccdcc_2 = ui.cbxCcdcc2.getSelectedItem().toString();
                        result.ccdcc_3 = ui.cbxCcdcc3.getSelectedItem().toString();
                        break;

                    case "ccfdc":
                        result.ccfdc_0 = ui.cbxCcfdc0.getSelectedItem().toString();
                        result.ccfdc_1 = ui.cbxCcfdc1.getSelectedItem().toString();
                        result.ccfdc_2 = ui.cbxCcfdc2.getSelectedItem().toString();
                        result.ccfdc_3 = ui.cbxCcfdc3.getSelectedItem().toString();
                        break;

                    case "ddcc1":
                        result.ddcc1_0 = ui.cbxDdcc0.getSelectedItem().toString();
                        result.ddcc1_1 = ui.cbxDdcc0.getSelectedItem().toString();
                        result.ddcc1_2 = ui.cbxDdcc0.getSelectedItem().toString();
                        result.ddcc1_3 = ui.cbxDdcc0.getSelectedItem().toString();
                        break;

                    case "evpc":
                        result.evpc_colorbus = cbxSelNone;
                        break;

                    case "hfdc":
                        result.hfdc_f1 = ui.cbxHfdcF1.getSelectedItem().toString();
                        result.hfdc_f2 = ui.cbxHfdcF2.getSelectedItem().toString();
                        result.hfdc_f3 = ui.cbxHfdcF3.getSelectedItem().toString();
                        result.hfdc_f4 = ui.cbxHfdcF4.getSelectedItem().toString();
                        result.hfdc_h1 = ui.cbxHfdcH1.getSelectedItem().toString();
                        result.hfdc_h2 = ui.cbxHfdcH2.getSelectedItem().toString();
                        result.hfdc_h3 = ui.cbxHfdcH3.getSelectedItem().toString();
                        break;

                    case "ide":
                        result.ide_0 = ui.cbxAta0.getSelectedItem().toString();
                        result.ide_1 = ui.cbxAta1.getSelectedItem().toString();
                        break;

                    case "tifdc":
                        result.tifdc_0 = ui.cbxTifdc0.getSelectedItem().toString();
                        result.tifdc_1 = ui.cbxTifdc0.getSelectedItem().toString();
                        result.tifdc_2 = ui.cbxTifdc0.getSelectedItem().toString();
                        break;

                    case "whtscsi":
                        result.whtscsi_0 = ui.cbxScsibus0.getSelectedItem().toString();
                        result.whtscsi_1 = ui.cbxScsibus0.getSelectedItem().toString();
                        result.whtscsi_2 = ui.cbxScsibus0.getSelectedItem().toString();
                        result.whtscsi_3 = ui.cbxScsibus0.getSelectedItem().toString();
                        result.whtscsi_4 = ui.cbxScsibus0.getSelectedItem().toString();
                        result.whtscsi_5 = ui.cbxScsibus0.getSelectedItem().toString();
                        result.whtscsi_6 = ui.cbxScsibus0.getSelectedItem().toString();
                        break;

                    default:
                        break;
                }

                selectedCards.add(sel);
            }
        }

        result.mame_AddOpt = ui.txtAddOpt.getText().trim();

        frame.getPebDevicesController().updateTabs(selectedCards);

        ui.dbgEmulatorOptions.setText(EmulatorStart.emulatorOptionsConcatenate(result));

        return result;
    }
}
