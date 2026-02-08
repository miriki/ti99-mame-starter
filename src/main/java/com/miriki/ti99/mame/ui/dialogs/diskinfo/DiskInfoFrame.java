package com.miriki.ti99.mame.ui.dialogs.diskinfo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.miriki.ti99.dskimg.impl.Ti99DiskImageImpl;
import com.miriki.ti99.dskimg.domain.*;
import com.miriki.ti99.dskimg.domain.enums.HfdcEncoding;
import com.miriki.ti99.dskimg.domain.enums.HfdcFileKind;
import com.miriki.ti99.dskimg.domain.enums.RecordFormat;

public class DiskInfoFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JLabel statusLabel;

    private Ti99DiskImageImpl image;
    private Ti99FileSystem fs;

    boolean[] abmSide0;
    boolean[] abmSide1;

    PixelMapPanel  pixelMap0Panel;
    PixelMapPanel  pixelMap1Panel;

    public DiskInfoFrame(JFrame owner, String dskName) {
        super("Disketten-Info: " + dskName);

        Font mono = new Font("Consolas", Font.PLAIN, 14);
        UIManager.put("Table.font", mono);
        UIManager.put("Label.font", mono);
        UIManager.put("TextField.font", mono);
        UIManager.put("TextArea.font", mono);
        UIManager.put("List.font", mono);
        
        setSize(1200, 750);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        statusLabel = new JLabel("Bereit.");
        statusLabel.setBorder(new EmptyBorder(4, 8, 4, 8));
        add(statusLabel, BorderLayout.SOUTH);

        try {
        	// HFDC: DiskImage laden
        	this.image = new Ti99DiskImageImpl(Path.of(dskName));

        	// HFDC: Filesystem laden
        	this.fs = Ti99DiskImageImpl.loadFileSystem(image.getDomainImage());

        	// Diskformat lesen
        	DiskFormat fmt = image.getFormat();

        	int sides = fmt.getSides();                 // 1 oder 2
        	int totalSectors = fmt.getTotalSectors();   // z.B. 720 oder 1440
        	int sectorsPerSide = totalSectors / sides;  // 720 bei DS/DD

        	// Allocation Bitmap (physische Sektoren)
        	boolean[] fullAbm = fs.getAbm().toBooleanArray();

        	// ------------------------------
        	// Seite 0
        	// ------------------------------
        	boolean[] abm0 = Arrays.copyOfRange(fullAbm, 0, sectorsPerSide);

        	Set<Integer> fdr0 = new HashSet<>();
        	Set<Integer> data0 = new HashSet<>();

        	for (FileDescriptorRecord fdr : fs.getFiles()) {

        	    int fdrSector = fdr.getFirstSector();
        	    if (fdrSector < sectorsPerSide) {
        	        fdr0.add(fdrSector);
        	    }

        	    for (int s : fdr.getDataChain()) {
        	        if (s < sectorsPerSide) {
        	            data0.add(s);
        	        }
        	    }
        	}

        	pixelMap0Panel = new PixelMapPanel(
        	        abm0,
        	        0,                // baseOffset
        	        totalSectors,
        	        fdr0,
        	        data0,
        	        false
        	);
            // pixelMap0Panel.dumpAsciiPixelMap();

        	// ------------------------------
        	// Seite 1 (nur bei DS)
        	// ------------------------------
        	if (sides == 2) {

        	    boolean[] abm1 = Arrays.copyOfRange(fullAbm, sectorsPerSide, sectorsPerSide * 2);

        	    Set<Integer> fdr1 = new HashSet<>();
        	    Set<Integer> data1 = new HashSet<>();

        	    for (FileDescriptorRecord fdr : fs.getFiles()) {

        	        int fdrSector = fdr.getFirstSector();
        	        if (fdrSector >= sectorsPerSide && fdrSector < sectorsPerSide * 2) {
        	            fdr1.add(fdrSector);
        	        }

        	        for (int s : fdr.getDataChain()) {
        	            if (s >= sectorsPerSide && s < sectorsPerSide * 2) {
        	                data1.add(s);
        	            }
        	        }
        	    }

        	    pixelMap1Panel = new PixelMapPanel(
        	            abm1,
        	            sectorsPerSide,   // baseOffset
        	            totalSectors,
        	            fdr1,
        	            data1,
        	            false
        	    );
                // pixelMap1Panel.dumpAsciiPixelMap();

        	} else {
        	    // SS-Diskette → Seite 1 leer
        	    pixelMap1Panel = new PixelMapPanel(
        	            new boolean[800],   // leer
        	            sectorsPerSide,
        	            totalSectors,
        	            Set.of(),
        	            Set.of(),
        	            true
        	    );
        	}

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Diskette:\n" + ex.getMessage());
            return;
        }

        add(buildMainLayout(), BorderLayout.CENTER);
    }

    private int countFree(AllocationBitmap abm) {
        int free = 0;
        for (int i = 0; i < abm.getTotalSectors(); i++) {
            if (!abm.isUsed(i)) free++;
        }
        return free;
    }

    private int countUsed(AllocationBitmap abm) {
        int used = 0;
        for (int i = 0; i < abm.getTotalSectors(); i++) {
            if (abm.isUsed(i)) used++;
        }
        return used;
    }

    private String tooltipForSector(int sector) {

        FileDescriptorRecord fdr = findFileBySector(sector);

        if (fdr == null) {
            return "Sektor " + sector + " (frei)";
        }

        return "Sektor " + sector + " → " + fdr.getFileName();
    }

    private JPanel buildMainLayout() {

        // Linke Spalte (flexibel)
        JPanel leftColumn = new JPanel(new BorderLayout(0, 5));
        leftColumn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        JPanel diskInfoPanel = buildDiskInfoPanel();
        JPanel directoryPanel = buildDirectoryPanel();

        leftColumn.add(diskInfoPanel, BorderLayout.NORTH);
        leftColumn.add(directoryPanel, BorderLayout.CENTER);

     // Rechte Spalte: PixelMaps nebeneinander
        JPanel maps = new JPanel(new GridLayout(1, 2, 10, 0));
        maps.add(pixelMap0Panel);
        maps.add(pixelMap1Panel);
        pixelMap0Panel.setTooltipProvider(this::tooltipForSector);
        pixelMap1Panel.setTooltipProvider(this::tooltipForSector);
        pixelMap0Panel.setSectorHoverListener(this::showSectorInfo);
        pixelMap1Panel.setSectorHoverListener(this::showSectorInfo);
        pixelMap0Panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateTooltip(pixelMap0Panel, e);
            }
        });
        pixelMap1Panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateTooltip(pixelMap1Panel, e);
            }
        });

        // ScrollPane NUR für die Höhe
        JScrollPane scrollMaps = new JScrollPane(
                maps,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // ScrollPane soll NICHT breiter werden als die PixelMaps
        scrollMaps.setPreferredSize(maps.getPreferredSize());
        scrollMaps.setMinimumSize(maps.getPreferredSize());
        scrollMaps.setMaximumSize(maps.getPreferredSize());

        // Hauptlayout
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(scrollMaps, BorderLayout.EAST);
        root.add(leftColumn, BorderLayout.CENTER);

        return root;
    }

    private JPanel buildDiskInfoPanel() {

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Disk-Info"));

        DiskFormat format = image.getFormat();

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row1.add(new JLabel("Volume:"));
        row1.add(makeValue(fs.getVib().getVolumeName()));
        row1.add(new JLabel("Typ:"));
        row1.add(makeValue(format.getDensity().name()));
        // row1.add(new JLabel("DOS:"));
        // row1.add(makeValue("HFDC"));

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row2.add(new JLabel("Tracks:"));
        row2.add(makeValue(String.valueOf(format.getTracksPerSide())));
        row2.add(new JLabel("Heads:"));
        row2.add(makeValue(String.valueOf(format.getSides())));
        row2.add(new JLabel("Sekt/Track:"));
        row2.add(makeValue(String.valueOf(format.getSectorsPerTrack())));

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row3.add(new JLabel("Frei:"));
        row3.add(makeValue(String.valueOf(countFree(fs.getAbm()))));
        row3.add(new JLabel("Belegt:"));
        row3.add(makeValue(String.valueOf(countUsed(fs.getAbm()))));
        row3.add(new JLabel("Cluster:"));
        row3.add(makeValue(String.valueOf(format.getSectorsPerCluster())));

        panel.add(row1);
        panel.add(row2);
        panel.add(row3);

        return panel;
    }

    private JLabel makeValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(java.awt.Font.BOLD));
        return lbl;
    }

    private JPanel buildDirectoryPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Directory-Info"));

        String[] columns = {
                "Dateiname",
                "Größe",
                "Typ",
                "RecLen",
                "P",
                "B",
                "E",
                "F"
        };

        List<FileDescriptorRecord> fdrs = fs.getFiles();
        Object[][] data = new Object[fdrs.size()][columns.length];

        for (int i = 0; i < fdrs.size(); i++) {

            FileDescriptorRecord fdr = fdrs.get(i);

            data[i][0] = fdr.getFileName();
            data[i][1] = fdr.getFileSizeInBytes();

            // HFDC → TI-DOS-kompatible Anzeige
            if (fdr.getKind() == HfdcFileKind.PROGRAM) {

                data[i][2] = "Program";
                data[i][3] = "";

            } else {

                String kind = (fdr.getEncoding() == HfdcEncoding.DIS) ? "Dis" : "Int";
                String fmt = (fdr.getRecordFormat() == RecordFormat.FIX) ? "Fix" : "Var";

                data[i][2] = kind + "/" + fmt;
                data[i][3] = String.valueOf(fdr.getLogicalRecordLength());
            }

            data[i][4] = fdr.isProtected() ? "P" : "";
            data[i][5] = fdr.isBackup() ? "B" : "";
            data[i][6] = fdr.isEmulated() ? "E" : "";
            data[i][7] = fdr.isFragmented() ? "J" : "N";
        }

        JTable table = new JTable(data, columns);

        DefaultTableCellRenderer headerRenderer =(DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
      	headerRenderer.setHorizontalAlignment(JLabel.CENTER);
       	headerRenderer.setVerticalAlignment(JLabel.CENTER);
        
        // Renderer für zentrierte Zellen
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setVerticalAlignment(JLabel.CENTER);

        // Renderer für die erste Spalte (linksbündig + Padding)
        DefaultTableCellRenderer leftPaddedRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                lbl.setHorizontalAlignment(JLabel.LEFT);
                lbl.setVerticalAlignment(JLabel.CENTER);

                // Polsterung links
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

                // Hintergrund sichtbar machen
                lbl.setOpaque(true);

                return lbl;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(leftPaddedRenderer);
        
        // Alle Spalten außer der ersten zentrieren
        for (int col = 1; col < table.getColumnCount(); col++) {
            table.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
        }

	    // Schriftart für die Tabelle setzen
	    Font mono = new Font("Consolas", Font.PLAIN, 14);
	    table.setFont(mono);
	    table.setRowHeight(24);
	    table.getTableHeader().setFont(mono);
        table.setFillsViewportHeight(true);

	    // Auto-Resize deaktivieren
	    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	
	    // Spaltenbreiten setzen
	    table.getColumnModel().getColumn(0).setPreferredWidth(170); // Dateiname
	    table.getColumnModel().getColumn(1).setPreferredWidth(65);  // Größe
	    table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Typ
	    table.getColumnModel().getColumn(3).setPreferredWidth(55);  // RecLen
	    table.getColumnModel().getColumn(4).setPreferredWidth(30);  // P
	    table.getColumnModel().getColumn(5).setPreferredWidth(30);  // B
	    table.getColumnModel().getColumn(6).setPreferredWidth(30);  // E
	    table.getColumnModel().getColumn(7).setPreferredWidth(30);  // Frag

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private FileDescriptorRecord findFileBySector(int sector) {

        for (FileDescriptorRecord fdr : fs.getFiles()) {

            if (fdr.getFirstSector() == sector) {
                return fdr;
            }

            if (fdr.getDataChain().contains(sector)) {
                return fdr;
            }
        }

        return null;
    }

    private void showSectorInfo(int sector) {

        FileDescriptorRecord fdr = findFileBySector(sector);

        if (fdr == null) {
            statusLabel.setText("Sektor " + sector + " ist frei.");
            return;
        }

        String name = fdr.getFileName();
        int first = fdr.getFirstSector();
        List<Integer> chain = fdr.getDataChain();

        statusLabel.setText(
            "Sektor " + sector + " → \"" + name + "\" (FDR " + first + "), Kette: " + chain
        );
    }

    private void updateTooltip(PixelMapPanel panel, MouseEvent e) {

        int index = panel.sectorFromCoordinates(e.getX(), e.getY());
        if (index < 0) {
            panel.setToolTipText(null);
            return;
        }

        int sector = panel.getBaseOffset() + index;

        FileDescriptorRecord fdr = findFileBySector(sector);
        if (fdr == null) {
            panel.setToolTipText("Sektor " + sector + " (frei)");
        } else {
            panel.setToolTipText("Sektor " + sector + " → " + fdr.getFileName());
        }
    }

}
