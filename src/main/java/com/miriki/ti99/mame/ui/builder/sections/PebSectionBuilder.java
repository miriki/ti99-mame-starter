package com.miriki.ti99.mame.ui.builder.sections;

import javax.swing.DefaultComboBoxModel;

import com.miriki.ti99.mame.ui.MainAppFrame;
import com.miriki.ti99.mame.ui.MainAppFrameComponents;
import com.miriki.ti99.mame.ui.builder.UiFactory;
import com.miriki.ti99.mame.ui.mamedevices.PebDevicesController;

/**
 * Builds the PEB configuration section.
 */
public class PebSectionBuilder {

    private final MainAppFrame frame;
    private final MainAppFrameComponents ui;
    private final UiFactory factory;

    public PebSectionBuilder(MainAppFrame frame,
                             MainAppFrameComponents ui,
                             UiFactory factory) {
        this.frame = frame;
        this.ui = ui;
        this.factory = factory;
    }

    /**
     * Builds the entire PEB section including all device tabs.
     */
    public void build() {

        // Sideport: PEB tab container
        ui.contentPane.add(ui.tabSideportDevices);
        ui.tabSideportDevices.setFont(ui.txtWorkingDir.getFont());
        ui.tabSideportDevices.setBounds(8, 272, 464 + 96 + 32, 352 - 24);

        // PEB main panel (CardLayout)
        ui.tabSideportDevices.addTab("peb", null, ui.Panel_PEB, null);
        ui.Panel_PEB.setLayout(new java.awt.CardLayout(0, 0));

        ui.Panel_PEB.add(ui.tabPebDevices, "tabPebDevices");
        ui.tabPebDevices.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        // Slot overview
        ui.tabPebDevices.addTab("peb", null, ui.Panel_PEB_PEB, null);
        ui.Panel_PEB_PEB.putClientProperty("deviceKey", "peb");
        ui.Panel_PEB_PEB.setLayout(null);
        buildPebSlots();

        // 32kmem
        ui.tabPebDevices.addTab("32kmem", null, ui.Panel_PEB_32kMem, null);
        ui.Panel_PEB_32kMem.putClientProperty("deviceKey", "32kmem");
        ui.Panel_PEB_32kMem.setLayout(null);

        // BWG
        ui.tabPebDevices.addTab("bwg", null, ui.Panel_PEB_Bwg, null);
        ui.Panel_PEB_Bwg.putClientProperty("deviceKey", "bwg");
        ui.Panel_PEB_Bwg.setLayout(null);
        buildPebBwg();

        // CCDCC
        ui.tabPebDevices.addTab("ccdcc", null, ui.Panel_PEB_CcDcc, null);
        ui.Panel_PEB_CcDcc.putClientProperty("deviceKey", "ccdcc");
        ui.Panel_PEB_CcDcc.setLayout(null);
        buildPebCcDcc();

        // CCFDC
        ui.tabPebDevices.addTab("ccfdc", null, ui.Panel_PEB_CcFdc, null);
        ui.Panel_PEB_CcFdc.putClientProperty("deviceKey", "ccfdc");
        ui.Panel_PEB_CcFdc.setLayout(null);
        buildPebCcFdc();

        // DDCC1
        ui.tabPebDevices.addTab("ddcc1", null, ui.Panel_PEB_DDcc1, null);
        ui.Panel_PEB_DDcc1.putClientProperty("deviceKey", "ddcc1");
        ui.Panel_PEB_DDcc1.setLayout(null);
        buildPebDdcc1();

        // EVPC
        ui.tabPebDevices.addTab("evpc", null, ui.Panel_PEB_Evpc, null);
        ui.Panel_PEB_Evpc.putClientProperty("deviceKey", "evpc");
        ui.Panel_PEB_Evpc.setLayout(null);
        buildPebEvpc();

        // HFDC
        ui.tabPebDevices.addTab("hfdc", null, ui.Panel_PEB_HFdc, null);
        ui.Panel_PEB_HFdc.putClientProperty("deviceKey", "hfdc");
        ui.Panel_PEB_HFdc.setLayout(null);
        buildPebHfdc();

        // Additional empty tabs
        addEmptyTab("forti", ui.Panel_PEB_Forti);
        addEmptyTab("horizon", ui.Panel_PEB_Horizon);
        addEmptyTab("hsgpl", ui.Panel_PEB_HsGpl);

        // IDE
        ui.tabPebDevices.addTab("ide", null, ui.Panel_PEB_Ide, null);
        ui.Panel_PEB_Ide.putClientProperty("deviceKey", "ide");
        ui.Panel_PEB_Ide.setLayout(null);
        buildPebIde();

        addEmptyTab("myarcmem", ui.Panel_PEB_MyarcMem);
        addEmptyTab("pcode", ui.Panel_PEB_PCode);
        addEmptyTab("pgram", ui.Panel_PEB_PGram);
        addEmptyTab("samsmem", ui.Panel_PEB_SamsMem);
        addEmptyTab("sidmaster", ui.Panel_PEB_SidMaster);
        addEmptyTab("speechadapter", ui.Panel_PEB_SpeechAdapter);

        // TIFDC
        ui.tabPebDevices.addTab("tifdc", null, ui.Panel_PEB_TiFdc, null);
        ui.Panel_PEB_TiFdc.putClientProperty("deviceKey", "tifdc");
        ui.Panel_PEB_TiFdc.setLayout(null);
        buildPebTifdc();

        addEmptyTab("tipi", ui.Panel_PEB_TiPi);
        addEmptyTab("tirs232", ui.Panel_PEB_TiRs232);
        addEmptyTab("usbsm", ui.Panel_PEB_UsbSm);

        // WHTSCSI
        ui.tabPebDevices.addTab("whtscsi", null, ui.Panel_PEB_WhtScsi, null);
        ui.Panel_PEB_WhtScsi.putClientProperty("deviceKey", "whtscsi");
        ui.Panel_PEB_WhtScsi.setLayout(null);
        buildPebWhtScsi();

        // Controller
        PebDevicesController ctl = new PebDevicesController(ui.tabPebDevices);
        frame.setPebDevicesController(ctl);
    }

    // -------------------------------------------------------------------------
    // Helper for empty tabs
    // -------------------------------------------------------------------------

    private void addEmptyTab(String key, javax.swing.JPanel panel) {
        ui.tabPebDevices.addTab(key, null, panel, null);
        panel.putClientProperty("deviceKey", key);
        panel.setLayout(null);
    }

    // -------------------------------------------------------------------------
    // PEB subsections
    // -------------------------------------------------------------------------

    private void buildPebSlots() {
        ui.cbxSlot1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_PEB, "cbxSlot1",
                new DefaultComboBoxModel<>(ui.PEB1_DEVICE_NAMES),
                82, 12, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Slot #1", 64
        );
        ui.cbxSlot1.setEnabled(false);

        ui.cbxSlot2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_PEB, "cbxSlot2",
                new DefaultComboBoxModel<>(ui.PEB_DEVICE_NAMES),
                82, 44, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Slot #2", 64
        );

        ui.cbxSlot3 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_PEB, "cbxSlot3",
                new DefaultComboBoxModel<>(ui.PEB_DEVICE_NAMES),
                82, 76, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Slot #3", 64
        );

        ui.cbxSlot4 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_PEB, "cbxSlot4",
                new DefaultComboBoxModel<>(ui.PEB_DEVICE_NAMES),
                82, 108, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Slot #4", 64
        );

        ui.cbxSlot5 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_PEB, "cbxSlot5",
                new DefaultComboBoxModel<>(ui.PEB_DEVICE_NAMES),
                82, 140, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Slot #5", 64
        );

        ui.cbxSlot6 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_PEB, "cbxSlot6",
                new DefaultComboBoxModel<>(ui.PEB_DEVICE_NAMES),
                82, 172, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Slot #6", 64
        );

        ui.cbxSlot7 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_PEB, "cbxSlot7",
                new DefaultComboBoxModel<>(ui.PEB_DEVICE_NAMES),
                82, 204, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Slot #7", 64
        );

        ui.cbxSlot8 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_PEB, "cbxSlot8",
                new DefaultComboBoxModel<>(ui.PEB_DEVICE_NAMES),
                82, 236, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Slot #8", 64
        );
    }

    private void buildPebBwg() {
        ui.cbxBwg0 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_Bwg, "cbxBwg0",
                new DefaultComboBoxModel<>(ui.BWG_FDD_NAMES),
                82, 12, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 1", 64
        );

        ui.cbxBwg1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_Bwg, "cbxBwg1",
                new DefaultComboBoxModel<>(ui.BWG_FDD_NAMES),
                82, 48, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 2", 64
        );

        ui.cbxBwg2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_Bwg, "cbxBwg2",
                new DefaultComboBoxModel<>(ui.BWG_FDD_NAMES),
                82, 84, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 3", 64
        );

        ui.cbxBwg3 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_Bwg, "cbxBwg3",
                new DefaultComboBoxModel<>(ui.BWG_FDD_NAMES),
                82, 120, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 4", 64
        );
    }

    private void buildPebCcDcc() {
        ui.cbxCcdcc0 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_CcDcc, "cbxCcdcc0",
                new DefaultComboBoxModel<>(ui.CCDCC_FDD_NAMES),
                82, 12, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 1", 64
        );

        ui.cbxCcdcc1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_CcDcc, "cbxCcdcc1",
                new DefaultComboBoxModel<>(ui.CCDCC_FDD_NAMES),
                82, 48, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 2", 64
        );

        ui.cbxCcdcc2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_CcDcc, "cbxCcdcc2",
                new DefaultComboBoxModel<>(ui.CCDCC_FDD_NAMES),
                82, 84, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 3", 64
        );

        ui.cbxCcdcc3 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_CcDcc, "cbxCcdcc3",
                new DefaultComboBoxModel<>(ui.CCDCC_FDD_NAMES),
                82, 120, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 4", 64
        );
    }

    private void buildPebCcFdc() {
        ui.cbxCcfdc0 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_CcFdc, "cbxCcfdc0",
                new DefaultComboBoxModel<>(ui.CCFDC_FDD_NAMES),
                82, 12, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 1", 64
        );

        ui.cbxCcfdc1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_CcFdc, "cbxCcfdc1",
                new DefaultComboBoxModel<>(ui.CCFDC_FDD_NAMES),
                82, 48, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 2", 64
        );

        ui.cbxCcfdc2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_CcFdc, "cbxCcfdc2",
                new DefaultComboBoxModel<>(ui.CCFDC_FDD_NAMES),
                82, 84, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 3", 64
        );

        ui.cbxCcfdc3 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_CcFdc, "cbxCcfdc3",
                new DefaultComboBoxModel<>(ui.CCFDC_FDD_NAMES),
                82, 120, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 4", 64
        );
    }

    private void buildPebDdcc1() {
        ui.cbxDdcc0 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_DDcc1, "cbxDdcc0",
                new DefaultComboBoxModel<>(ui.DDCC1_FDD_NAMES),
                82, 12, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 1", 64
        );

        ui.cbxDdcc1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_DDcc1, "cbxDdcc1",
                new DefaultComboBoxModel<>(ui.DDCC1_FDD_NAMES),
                82, 48, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 2", 64
        );

        ui.cbxDdcc2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_DDcc1, "cbxDdcc2",
                new DefaultComboBoxModel<>(ui.DDCC1_FDD_NAMES),
                82, 84, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 3", 64
        );

        ui.cbxDdcc3 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_DDcc1, "cbxDdcc3",
                new DefaultComboBoxModel<>(ui.DDCC1_FDD_NAMES),
                82, 120, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 4", 64
        );
    }

    private void buildPebEvpc() {
        ui.cbxColorbus = factory.createComboBoxWithLabel(
                ui.Panel_PEB_Evpc, "cbxColorbus",
                new DefaultComboBoxModel<>(ui.EVPC_DEVICE_NAMES),
                82, 12, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "ColorBus", 64
        );
    }

    private void buildPebHfdc() {
        ui.cbxHfdcF1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_HFdc, "cbxHfdcF1",
                new DefaultComboBoxModel<>(ui.HFDC_FDD_NAMES),
                82, 12, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 1", 64
        );

        ui.cbxHfdcF2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_HFdc, "cbxHfdcF2",
                new DefaultComboBoxModel<>(ui.HFDC_FDD_NAMES),
                82, 48, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 2", 64
        );

        ui.cbxHfdcF3 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_HFdc, "cbxHfdcF3",
                new DefaultComboBoxModel<>(ui.HFDC_FDD_NAMES),
                82, 84, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 3", 64
        );

        ui.cbxHfdcF4 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_HFdc, "cbxHfdcF4",
                new DefaultComboBoxModel<>(ui.HFDC_FDD_NAMES),
                82, 120, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 4", 64
        );

        ui.cbxHfdcH1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_HFdc, "cbxHfdcH1",
                new DefaultComboBoxModel<>(ui.HFDC_HDD_NAMES),
                82, 156, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Hard 1", 64
        );

        ui.cbxHfdcH2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_HFdc, "cbxHfdcH2",
                new DefaultComboBoxModel<>(ui.HFDC_HDD_NAMES),
                82, 192, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Hard 2", 64
        );

        ui.cbxHfdcH3 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_HFdc, "cbxHfdcH3",
                new DefaultComboBoxModel<>(ui.HFDC_HDD_NAMES),
                82, 228, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Hard 3", 64
        );
    }

    private void buildPebIde() {
        ui.cbxAta0 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_Ide, "cbxAta0",
                new DefaultComboBoxModel<>(ui.IDE_DEVICE_NAMES),
                82, 12, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "ATA 0", 64
        );

        ui.cbxAta1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_Ide, "cbxAta1",
                new DefaultComboBoxModel<>(ui.IDE_DEVICE_NAMES),
                82, 48, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "ATA 1", 64
        );
    }

    private void buildPebTifdc() {
        ui.cbxTifdc0 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_TiFdc, "cbxTifdc0",
                new DefaultComboBoxModel<>(ui.TIFDC_FDD_NAMES),
                82, 12, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 1", 64
        );

        ui.cbxTifdc1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_TiFdc, "cbxTifdc1",
                new DefaultComboBoxModel<>(ui.TIFDC_FDD_NAMES),
                82, 48, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 2", 64
        );

        ui.cbxTifdc2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_TiFdc, "cbxTifdc2",
                new DefaultComboBoxModel<>(ui.TIFDC_FDD_NAMES),
                82, 84, 96, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "Flop 3", 64
        );
    }

    private void buildPebWhtScsi() {
        ui.cbxScsibus0 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_WhtScsi, "cbxScsibus0",
                new DefaultComboBoxModel<>(ui.SCSI_DEVICE_NAMES),
                82, 12, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "SCSI 0", 64
        );

        ui.cbxScsibus1 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_WhtScsi, "cbxScsibus1",
                new DefaultComboBoxModel<>(ui.SCSI_DEVICE_NAMES),
                82, 44, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "SCSI 1", 64
        );

        ui.cbxScsibus2 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_WhtScsi, "cbxScsibus2",
                new DefaultComboBoxModel<>(ui.SCSI_DEVICE_NAMES),
                82, 76, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "SCSI 2", 64
        );

        ui.cbxScsibus3 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_WhtScsi, "cbxScsibus3",
                new DefaultComboBoxModel<>(ui.SCSI_DEVICE_NAMES),
                82, 108, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "SCSI 3", 64
        );

        ui.cbxScsibus4 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_WhtScsi, "cbxScsibus4",
                new DefaultComboBoxModel<>(ui.SCSI_DEVICE_NAMES),
                82, 140, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "SCSI 4", 64
        );

        ui.cbxScsibus5 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_WhtScsi, "cbxScsibus5",
                new DefaultComboBoxModel<>(ui.SCSI_DEVICE_NAMES),
                82, 172, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "SCSI 5", 64
        );

        ui.cbxScsibus6 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_WhtScsi, "cbxScsibus6",
                new DefaultComboBoxModel<>(ui.SCSI_DEVICE_NAMES),
                82, 204, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "SCSI 6", 64
        );

        ui.cbxScsibus7 = factory.createComboBoxWithLabel(
                ui.Panel_PEB_WhtScsi, "cbxScsibus7",
                new DefaultComboBoxModel<>(ui.SCSI_DEVICE_NAMES),
                82, 236, 256, 22,
                0,
                e -> frame.collectEmulatorOptions(),
                "SCSI 7", 64
        );
    }
}
