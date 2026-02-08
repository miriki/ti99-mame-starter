package com.miriki.ti99.mame.ui.dialogs.diskinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Set;

import javax.swing.JPanel;

public class PixelMapPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // --- INPUT-DATEN ---------------------------------------------------------

    private final boolean[] abm;       // Bits für diese Seite (z.B. 720 oder 800)
    private final int baseOffset;      // physischer Startsektor dieser Seite
    private final int totalSectors;    // gesamte Sektoranzahl der Disk

    private final Set<Integer> fdrSectors;   // physische Sektornummern
    private final Set<Integer> dataSectors;  // physische Sektornummern
    
    private final boolean emptyPanel;

    // --- DARSTELLUNG ---------------------------------------------------------

    private final int cols = 20;
    private final int rows = 40;
    private final int cell = 16;

    // Farben
    private final Color gridColor      = Color.DARK_GRAY;
    private final Color fadedGridColor = new Color(200, 200, 200); // hellgrau
    private final Color baseColor      = getBackground(); // neutral

    private final Color vibColor       = new Color(200,  60,  60); // dunkelrot
    private final Color fdiColor       = new Color(255, 120, 120); // hellrot

    private final Color fdrZoneColor   = new Color(255, 255, 191); // hellgelb
    private final Color fdrColor       = new Color(220, 220, 80);  // dunkleres gelb
    
    private final Color dataColor      = new Color(120, 200, 120); // dunkleres grün
    private final Color freeColor      = new Color(223, 255, 223); // helleres grün

    private final Color blockedColor   = new Color(127, 127, 127); // dunkelgrau

    // --- HOVER-EVENT ---------------------------------------------------------

    private SectorHoverListener hoverListener;
    private int lastSector = -1;
    private int hoverSector = -1;

    public interface SectorHoverListener {void onSectorHover(int sectorNumber);}

    public void setSectorHoverListener(SectorHoverListener l) {this.hoverListener = l;}

    public int getBaseOffset() {
        return baseOffset;
    }
    
    public interface TooltipProvider {
        String getTooltipForSector(int sector);
    }

    private TooltipProvider tooltipProvider;

    public void setTooltipProvider(TooltipProvider provider) {
        this.tooltipProvider = provider;
    }
    // --- KONSTRUKTOR ---------------------------------------------------------

    public PixelMapPanel(
            boolean[] abm,
            int baseOffset,
            int totalSectors,
            Set<Integer> fdrSectors,
            Set<Integer> dataSectors,
            boolean emptyPanel) {

        this.abm = abm;
        this.baseOffset = baseOffset;
        this.totalSectors = totalSectors;
        this.fdrSectors = fdrSectors;
        this.dataSectors = dataSectors;
        this.emptyPanel = emptyPanel;

        setPreferredSize(new Dimension(cols * cell, rows * cell));

        installMouseHover();
        setToolTipText(""); // aktiviert Tooltips

    }

    // --- MOUSE-HOVER-LOGIK ---------------------------------------------------

    private void installMouseHover() {

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                int sectorIndex = sectorFromCoordinates(e.getX(), e.getY());
                if (sectorIndex < 0) return;

                int sector = baseOffset + sectorIndex;

                // Debounce: Nur melden, wenn sich der Sektor geändert hat
                if (sector != lastSector) {
                    lastSector = sector;
                    hoverSector = sector;   // <--- HIER wird der Hover gespeichert
                    repaint();              // <--- neu zeichnen

                    if (hoverListener != null) {
                        hoverListener.onSectorHover(sector);
                    }
                }
            }
        });
    }

    @Override
    public String getToolTipText(MouseEvent e) {

        int index = sectorFromCoordinates(e.getX(), e.getY());
        if (index < 0) return null;

        int sector = baseOffset + index;

        if (tooltipProvider != null) {
            return tooltipProvider.getTooltipForSector(sector);
        }

        return "Sektor " + sector;
    }

    // --- SEKTOR-BERECHNUNG ---------------------------------------------------

    public int sectorFromCoordinates(int x, int y) {

        int col = x / cell;
        int row = y / cell;

        if (col < 0 || col >= cols) return -1;
        if (row < 0 || row >= rows) return -1;

        return row * cols + col;
    }

    // --- ZEICHNEN ------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < cols * rows; i++) { // IMMER 800 Zellen zeichnen

            int x = (i % cols) * cell;
            int y = (i / cols) * cell;

            g.setColor(getColorForSector(i));
            g.fillRect(x, y, cell, cell);

            g.setColor(emptyPanel ? fadedGridColor : gridColor);
            g.drawRect(x, y, cell, cell);

            // Hover-Highlight
            if (hoverSector >= baseOffset) {

                int index = hoverSector - baseOffset;

                if (index >= 0 && index < cols * rows) {

                    x = (index % cols) * cell;
                    y = (index / cols) * cell;

                    g.setColor(new Color(0, 0, 0, 80)); // halbtransparentes Schwarz
                    g.fillRect(x, y, cell, cell);

                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, cell, cell);
                }
            }
        }
    }

    // --- FARBLOGIK -----------------------------------------------------------

    private Color getColorForSector(int sectorIndex) {

        if (emptyPanel) {
            return baseColor;
        }

        if (sectorIndex >= abm.length) {
            return blockedColor;
        }

        int sector = baseOffset + sectorIndex;

        if (sector >= totalSectors) {
            return blockedColor;
        }

        if (sector == 0) return vibColor;
        if (sector == 1) return fdiColor;

        if (fdrSectors.contains(sector)) return fdrColor;

        if (dataSectors.contains(sector)) return dataColor;

        if (sector >= 2 && sector <= 33) return fdrZoneColor;

        return freeColor;
    }

    // --- ASCII-DUMP ----------------------------------------------------------

    public void dumpAsciiPixelMap() {

        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                int i = row * cols + col;
                int sector = baseOffset + i;

                char c;

                if (sector >= totalSectors) {
                    c = '#';
                } else if (sector == 0 || sector == 1) {
                    c = '*';
                } else if (fdrSectors.contains(sector)) {
                    c = 'F';
                } else if (dataSectors.contains(sector)) {
                    c = 'D';
                } else {
                    c = '.';
                }

                sb.append(c);
            }
            sb.append('\n');
        }

        System.out.println(sb.toString());
    }
}
