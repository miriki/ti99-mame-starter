package com.miriki.ti99.mame.ui.menu;

import javax.swing.*;

import com.miriki.ti99.mame.localization.I18n;
import com.miriki.ti99.mame.ui.MainAppFrame;
import com.miriki.ti99.mame.ui.MainAppFrameComponents;

/**
 * Builds the Help menu.
 */
public class HelpMenuBuilder {

    private final MainAppFrame frame;
    private final MainAppFrameComponents ui;

    public HelpMenuBuilder(MainAppFrame frame, MainAppFrameComponents ui) {
        this.frame = frame;
        this.ui = ui;
    }

    /**
     * Populates the given Help menu.
     */
    public void build(JMenu menu) {

        ui.menuHelp = menu;

        ui.menuHelpAbout = I18nMenuFactory.createMenuItem(menu, "menu.help.about");

        ui.menuHelpAbout.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                frame,
                """
                MAME TI99 Starter

                v0.99.67 (public beta)

                (c) 2025 by

                Michael 'miriki' Rittweger

                [ Benjamin Franklin Edition ]
                """,
                I18n.t("menu.help.about"),
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
}
