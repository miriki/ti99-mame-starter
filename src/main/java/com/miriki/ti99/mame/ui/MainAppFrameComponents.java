package com.miriki.ti99.mame.ui;

import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Registry of all Swing components used by the main application frame.
 * <p>
 * Components are created here in their unconfigured state. Layout, I18n,
 * listeners and dynamic content are applied by the builder classes.
 */
public class MainAppFrameComponents {

    // --------------------------------------------------
    // Menus (top-level)
    // --------------------------------------------------

    public JMenu menuFile;
    public JMenu menuLang;
    public JMenu menuSettings;
    public JMenu menuHelp;

    // --------------------------------------------------
    // Menu: File
    // --------------------------------------------------

    public JMenuItem menuFileExit;

    // --------------------------------------------------
    // Menu: Language
    // --------------------------------------------------

    public JCheckBoxMenuItem menuLangEnglishGB;
    public JCheckBoxMenuItem menuLangEnglishUS;
    public JCheckBoxMenuItem menuLangEnglishAU;

    public JCheckBoxMenuItem menuLangGermanDE;
    public JCheckBoxMenuItem menuLangGermanAT;
    public JCheckBoxMenuItem menuLangGermanCH;

    public JCheckBoxMenuItem menuLangFrenchFR;
    public JCheckBoxMenuItem menuLangItalianIT;

    // --------------------------------------------------
    // Menu: Settings
    // --------------------------------------------------

    public JMenuItem menuSettingsSave;
    public JMenuItem menuSettingsSaveAs;
    public JMenuItem menuSettingsLoad;

    // Pick submenu
    public JMenu menuSettingsPick;
    public JMenuItem menuSettingsPickByCount;
    public JMenuItem menuSettingsPickByDate;
    public JMenuItem menuSettingsNoSel;
    public JMenuItem menuSettingsPickLoad;
    public JMenuItem menuSettingsPickDelete;
    public JMenuItem menuSettingsPickRename;

    // --------------------------------------------------
    // Menu: Help
    // --------------------------------------------------

    public JMenuItem menuHelpAbout;

    // --------------------------------------------------
    // Constant arrays (device names, machine names, etc.)
    // --------------------------------------------------

    public final String[] SIDEPORT_DEVICE_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "peb",
            "speechsyn",
            "splitter",
            "arcturus"
    };

    public final String[] PEB1_DEVICE_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "not connected",
            "TI99 console",
            "Geneve card"
    };

    public final String[] PEB_DEVICE_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "32kmem",
            "bwg",
            "ccdcc",
            "ccfdc",
            "ddcc1",
            "evpc",
            "forti",
            "hfdc",
            "horizon",
            "hsgpl",
            "ide",
            "myarcmem",
            "pcode",
            "pgram",
            "samsmem",
            "sidmaster",
            "speechadapter",
            "tifdc",
            "tipi",
            "tirs232",
            "usbsm",
            "whtscsi"
    };

    public final String[] MACHINE_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "TI99_4",
            "TI99_4e",
            "TI99_4a",
            "TI99_4ae",
            "TI99_4ev",
            "TI99_4p",
            "TI99_4qi",
            "TI99_8",
            "TI99_8e",
            "geneve",
            "genmod"
    };

    public final String[] GROM_PORT_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "single",
            "multi",
            "gkracker"
    };

    public final String[] CARTRIDGE_NAMES = { UiConstants.CBX_SEL_NONE };
    public final String[] FLOPPYDISKIMAGE_NAMES = { UiConstants.CBX_SEL_NONE };
    public final String[] HARDDISKIMAGE_NAMES = { UiConstants.CBX_SEL_NONE };
    public final String[] CASSETTEIMAGE_NAMES = { UiConstants.CBX_SEL_NONE };

    public final String[] JOYSTICK_PORT_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "twinjoy"
    };

    public final String[] BWG_FDD_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "35dd",
            "525dd",
            "525qd"
    };

    public final String[] CCDCC_FDD_NAMES = BWG_FDD_NAMES;
    public final String[] CCFDC_FDD_NAMES = BWG_FDD_NAMES;
    public final String[] DDCC1_FDD_NAMES = BWG_FDD_NAMES;

    public final String[] TIFDC_FDD_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "525dd"
    };

    public final String[] HFDC_FDD_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "35dd",
            "35hd",
            "525dd",
            "525qd"
    };

    public final String[] HFDC_HDD_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "generic",
            "st213",
            "st225",
            "st251"
    };

    public final String[] EVPC_DEVICE_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "busmouse"
    };

    public final String[] IDE_DEVICE_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "cdrom",
            "cf",
            "cp2024",
            "cr589",
            "hdd",
            "px320a",
            "xm3301",
            "zip100"
    };

    public final String[] SCSI_DEVICE_NAMES = {
            UiConstants.CBX_SEL_NONE,
            "aplcd150",
            "aplcdsc",
            "cdd2000",
            "cdr4210",
            "cdrn820s",
            "cdrom",
            "cdu415",
            "cdu561_25",
            "cdu75s",
            "cfp1080s",
            "crd254sh",
            "cw7501",
            "harddisk",
            "s1410",
            "smoc501",
            "tape"
    };

    // --------------------------------------------------
    // Main content area
    // --------------------------------------------------

    public JPanel contentPane;

    public JLabel lblExecutable;
    public JTextField txtExecutable;

    public JLabel lblWorkingDir;
    public JTextField txtWorkingDir;

    public JLabel lblRomPath;
    public JTextField txtRomPath;

    public JLabel lblCartPath;
    public JTextField txtCartPath;

    public JLabel lblMachine;
    public JComboBox<String> cbxMachine;

    public JLabel lblGromPort;
    public JComboBox<String> cbxGromPort;

    public JComboBox<String> cbxCartridge;

    public JLabel lblJoyPort;
    public JComboBox<String> cbxJoyPort;

    public JLabel lblIoPort;
    public JComboBox<String> cbxIoPort;

    public JLabel lblAddOpt;
    public JTextField txtAddOpt;

    public JLabel lblFddPath;
    public JTextField txtFddPath;

    public JLabel lblFiadPath;
    public JTextField txtFiadPath;

    public JLabel lblFlop;
    public JLabel lblFlop1;
    public JComboBox<String> cbxFlop1;

    public JLabel lblFlop2;
    public JComboBox<String> cbxFlop2;

    public JLabel lblFlop3;
    public JComboBox<String> cbxFlop3;

    public JLabel lblFlop4;
    public JComboBox<String> cbxFlop4;

    public JLabel lblHddPath;
    public JTextField txtHddPath;

    public JLabel lblHard;
    public JLabel lblHard1;
    public JComboBox<String> cbxHard1;

    public JLabel lblHard2;
    public JComboBox<String> cbxHard2;

    public JLabel lblHard3;
    public JComboBox<String> cbxHard3;

    public JLabel lblCassPath;
    public JTextField txtCassPath;

    public JLabel lblCass;
    public JLabel lblCass1;
    public JComboBox<String> cbxCass1;

    public JLabel lblCass2;
    public JComboBox<String> cbxCass2;

    public JTextArea dbgEmulatorOptions;

    public JButton btnStartEmulator;
    // public JButton btnTestFiad;
    public JButton btnFlop1Info;

    // --------------------------------------------------
    // Sidebar / tabs
    // --------------------------------------------------

    public JTabbedPane tabSideportDevices;

    // Sideport: PEB
    public JPanel Panel_PEB;
    public JTabbedPane tabPebDevices;

    // --------------------------------------------------
    // PEB main panel
    // --------------------------------------------------

    public JPanel Panel_PEB_PEB;
    public JLabel lblPeb;

    public JLabel lblSlot1;
    public JComboBox<String> cbxSlot1;

    public JLabel lblSlot2;
    public JComboBox<String> cbxSlot2;

    public JLabel lblSlot3;
    public JComboBox<String> cbxSlot3;

    public JLabel lblSlot4;
    public JComboBox<String> cbxSlot4;

    public JLabel lblSlot5;
    public JComboBox<String> cbxSlot5;

    public JLabel lblSlot6;
    public JComboBox<String> cbxSlot6;

    public JLabel lblSlot7;
    public JComboBox<String> cbxSlot7;

    public JLabel lblSlot8;
    public JComboBox<String> cbxSlot8;

    // --------------------------------------------------
    // PEB subpanels
    // --------------------------------------------------

    public JPanel Panel_PEB_32kMem;
    public JLabel lbl32kMem;

    public JPanel Panel_PEB_Bwg;
    public JLabel lblBwg;
    public JLabel lblBwg0;
    public JComboBox<String> cbxBwg0;
    public JLabel lblBwg1;
    public JComboBox<String> cbxBwg1;
    public JLabel lblBwg2;
    public JComboBox<String> cbxBwg2;
    public JLabel lblBwg3;
    public JComboBox<String> cbxBwg3;

    public JPanel Panel_PEB_CcDcc;
    public JLabel lblCcdcc;
    public JLabel lblCcdcc0;
    public JComboBox<String> cbxCcdcc0;
    public JLabel lblCcdcc1;
    public JComboBox<String> cbxCcdcc1;
    public JLabel lblCcdcc2;
    public JComboBox<String> cbxCcdcc2;
    public JLabel lblCcdcc3;
    public JComboBox<String> cbxCcdcc3;

    public JPanel Panel_PEB_CcFdc;
    public JLabel lblCcfdc;
    public JLabel lblCcfdc0;
    public JComboBox<String> cbxCcfdc0;
    public JLabel lblCcfdc1;
    public JComboBox<String> cbxCcfdc1;
    public JLabel lblCcfdc2;
    public JComboBox<String> cbxCcfdc2;
    public JLabel lblCcfdc3;
    public JComboBox<String> cbxCcfdc3;

    public JPanel Panel_PEB_DDcc1;
    public JLabel lblDdcc;
    public JLabel lblDdcc0;
    public JComboBox<String> cbxDdcc0;
    public JLabel lblDdcc1;
    public JComboBox<String> cbxDdcc1;
    public JLabel lblDdcc2;
    public JComboBox<String> cbxDdcc2;
    public JLabel lblDdcc3;
    public JComboBox<String> cbxDdcc3;

    public JPanel Panel_PEB_Evpc;
    public JLabel lblEvpc;
    public JLabel lblColorbus;
    public JComboBox<String> cbxColorbus;

    public JPanel Panel_PEB_Forti;
    public JLabel lblForti;

    public JPanel Panel_PEB_HFdc;
    public JLabel lblHfdc;
    public JLabel lblHfdcF1;
    public JComboBox<String> cbxHfdcF1;
    public JLabel lblHfdcF2;
    public JComboBox<String> cbxHfdcF2;
    public JLabel lblHfdcF3;
    public JComboBox<String> cbxHfdcF3;
    public JLabel lblHfdcF4;
    public JComboBox<String> cbxHfdcF4;
    public JLabel lblHfdcH1;
    public JComboBox<String> cbxHfdcH1;
    public JLabel lblHfdcH2;
    public JComboBox<String> cbxHfdcH2;
    public JLabel lblHfdcH3;
    public JComboBox<String> cbxHfdcH3;

    public JPanel Panel_PEB_Horizon;
    public JLabel lblHorizon;

    public JPanel Panel_PEB_HsGpl;
    public JLabel lblHsgpl;

    public JPanel Panel_PEB_Ide;
    public JLabel lblIde;
    public JLabel lblAta0;
    public JComboBox<String> cbxAta0;
    public JLabel lblAta1;
    public JComboBox<String> cbxAta1;

    public JPanel Panel_PEB_MyarcMem;
    public JLabel lblMyarcmem;

    public JPanel Panel_PEB_PCode;
    public JLabel lblPcode;

    public JPanel Panel_PEB_PGram;
    public JLabel lblPgram;

    public JPanel Panel_PEB_SamsMem;
    public JLabel lblSamsmem;

    public JPanel Panel_PEB_SidMaster;
    public JLabel lblSidmaster;

    public JPanel Panel_PEB_SpeechAdapter;
    public JLabel lblSpeechadaptger;

    public JPanel Panel_PEB_TiFdc;
    public JLabel lblTifdc;
    public JLabel lblTifdc0;
    public JComboBox<String> cbxTifdc0;
    public JLabel lblTifdc1;
    public JComboBox<String> cbxTifdc1;
    public JLabel lblTifdc2;
    public JComboBox<String> cbxTifdc2;

    public JPanel Panel_PEB_TiPi;
    public JLabel lblTipi;

    public JPanel Panel_PEB_TiRs232;
    public JLabel lblTirs232;

    public JPanel Panel_PEB_UsbSm;
    public JLabel lblUsbsm;

    public JPanel Panel_PEB_WhtScsi;
    public JLabel lblWhtscsi;

    public JLabel lblScsibus0;
    public JComboBox<String> cbxScsibus0;
    public JTextField txtScsibus0;

    public JLabel lblScsibus1;
    public JComboBox<String> cbxScsibus1;
    public JTextField txtScsibus1;

    public JLabel lblScsibus2;
    public JComboBox<String> cbxScsibus2;
    public JTextField txtScsibus2;

    public JLabel lblScsibus3;
    public JComboBox<String> cbxScsibus3;
    public JTextField txtScsibus3;

    public JLabel lblScsibus4;
    public JComboBox<String> cbxScsibus4;
    public JTextField txtScsibus4;

    public JLabel lblScsibus5;
    public JComboBox<String> cbxScsibus5;
    public JTextField txtScsibus5;

    public JLabel lblScsibus6;
    public JComboBox<String> cbxScsibus6;
    public JTextField txtScsibus6;

    public JLabel lblScsibus7;
    public JComboBox<String> cbxScsibus7;
    public JTextField txtScsibus7;

    // --------------------------------------------------
    // Other sidebar panels
    // --------------------------------------------------

    public JPanel Panel_SpeechSyn;
    public JPanel Panel_Splitter;
    public JPanel Panel_Arcturus;

    // --------------------------------------------------
    // Constructor
    // --------------------------------------------------

    /**
     * Creates all Swing components in their unconfigured state.
     */
    public MainAppFrameComponents() {

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);

        dbgEmulatorOptions = new JTextArea();
        btnStartEmulator = new JButton("start");
        // btnTestFiad = new JButton("test fiad");
        btnFlop1Info = new JButton("?");

        tabSideportDevices = new JTabbedPane(JTabbedPane.TOP);

        Panel_PEB = new JPanel();
        tabPebDevices = new JTabbedPane(JTabbedPane.TOP);

        Panel_PEB_PEB = new JPanel();
        Panel_PEB_32kMem = new JPanel();
        Panel_PEB_Bwg = new JPanel();
        Panel_PEB_CcDcc = new JPanel();
        Panel_PEB_CcFdc = new JPanel();
        Panel_PEB_DDcc1 = new JPanel();
        Panel_PEB_Evpc = new JPanel();
        Panel_PEB_Forti = new JPanel();
        Panel_PEB_HFdc = new JPanel();
        Panel_PEB_Horizon = new JPanel();
        Panel_PEB_HsGpl = new JPanel();
        Panel_PEB_Ide = new JPanel();
        Panel_PEB_MyarcMem = new JPanel();
        Panel_PEB_PCode = new JPanel();
        Panel_PEB_PGram = new JPanel();
        Panel_PEB_SamsMem = new JPanel();
        Panel_PEB_SidMaster = new JPanel();
        Panel_PEB_SpeechAdapter = new JPanel();
        Panel_PEB_TiFdc = new JPanel();
        Panel_PEB_TiPi = new JPanel();
        Panel_PEB_TiRs232 = new JPanel();
        Panel_PEB_UsbSm = new JPanel();
        Panel_PEB_WhtScsi = new JPanel();

        Panel_SpeechSyn = new JPanel();
        Panel_Splitter = new JPanel();
        Panel_Arcturus = new JPanel();
    }

    // --------------------------------------------------
    // Bind method
    // --------------------------------------------------

    /**
     * Assigns a Swing component to a field of this class via reflection.
     */
    public void bind(String fieldName, JComponent comp) {
        try {
            Field f = this.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(this, comp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to bind UI component: " + fieldName, e);
        }
    }

    // --------------------------------------------------
    // Content pane accessor
    // --------------------------------------------------

    /**
     * Returns the main content panel.
     */
    public JPanel getContentPane() {
        return contentPane;
    }
}
