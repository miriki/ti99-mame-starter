package com.miriki.ti99.mame.ui.builder.sections;

import java.awt.Container;

// import static com.miriki.ti99.BuildConfig.DEV;
import com.miriki.ti99.mame.ui.MainAppFrame;
import com.miriki.ti99.mame.ui.MainAppFrameComponents;
import com.miriki.ti99.mame.ui.builder.UiFactory;
import com.miriki.ti99.mame.ui.util.Listeners;

/**
 * Builds the button section of the main application frame.
 * <p>
 * This includes the "Start Emulator" and "Test FIAD" buttons, their layout
 * and event bindings.
 */
public class ButtonSectionBuilder {

    private final MainAppFrame frame;
    private final MainAppFrameComponents ui;
    @SuppressWarnings("unused")
    private final UiFactory factory;

    public ButtonSectionBuilder(MainAppFrame frame,
                                MainAppFrameComponents ui,
                                UiFactory factory) {
        this.frame = frame;
        this.ui = ui;
        this.factory = factory;
    }

    /**
     * Adds and configures all buttons in this section.
     */
    public void build() {

        Container parent = frame.getContentPane();

        // ---------------------------------------------------------------------
        // Start Emulator Button
        // ---------------------------------------------------------------------

        parent.add(ui.btnStartEmulator);
        ui.btnStartEmulator.setName("btnStartEmulator");
        ui.btnStartEmulator.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        ui.btnStartEmulator.setBounds(616, 592 - 24, 464 + 88, 32);
        ui.btnStartEmulator.addMouseListener(Listeners.startEmulatorClick(frame));
        ui.btnStartEmulator.putClientProperty("i18n", "button.startemulator");

        // ---------------------------------------------------------------------
        // Test FIAD Button
        // ---------------------------------------------------------------------

        /*
        if (DEV) {
	        parent.add(ui.btnTestFiad);
	        ui.btnTestFiad.setName("btnTestFiad");
	        ui.btnTestFiad.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
	        ui.btnTestFiad.setBounds(616, 240, 464 + 88, 32);
	        ui.btnTestFiad.addMouseListener(Listeners.testFiadCreate());
	        ui.btnTestFiad.putClientProperty("i18n", "button.testfiad");
        }
        */
    }
}
