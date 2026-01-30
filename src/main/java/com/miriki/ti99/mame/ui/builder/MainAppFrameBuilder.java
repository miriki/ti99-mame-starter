package com.miriki.ti99.mame.ui.builder;

import com.miriki.ti99.mame.localization.I18n;
import com.miriki.ti99.mame.localization.I18nValidator;
import com.miriki.ti99.mame.ui.MainAppFrame;
import com.miriki.ti99.mame.ui.MainAppFrameComponents;
import com.miriki.ti99.mame.ui.builder.sections.MainLayoutBuilder;
import com.miriki.ti99.mame.ui.builder.sections.PathSectionBuilder;
import com.miriki.ti99.mame.ui.builder.sections.MediaSectionBuilder;
import com.miriki.ti99.mame.ui.builder.sections.PebSectionBuilder;
import com.miriki.ti99.mame.ui.builder.sections.SideportSectionBuilder;
import com.miriki.ti99.mame.ui.builder.sections.ButtonSectionBuilder;
import com.miriki.ti99.mame.ui.builder.sections.DebugSectionBuilder;
import com.miriki.ti99.mame.ui.builder.sections.MachineSectionBuilder;

/**
 * Builds and initializes the main application frame.
 */
public class MainAppFrameBuilder {

    private final MainAppFrame frame;
    private final MainAppFrameComponents ui;
    private final UiFactory factory;

    public MainAppFrameBuilder(MainAppFrame frame, MainAppFrameComponents ui) {
        this.frame = frame;
        this.ui = ui;
        // this.factory = new UiFactory(frame, ui);
        this.factory = new UiFactory(ui);
    }

    /**
     * Builds all UI sections in the correct order.
     */
    public void initGUI() {

        new MainLayoutBuilder(frame, ui, factory).build();
        new PathSectionBuilder(frame, ui, factory).build();
        new MachineSectionBuilder(frame, ui, factory).build();
        new MediaSectionBuilder(frame, ui, factory).build();
        new PebSectionBuilder(frame, ui, factory).build();
        new SideportSectionBuilder(frame, ui, factory).build();
        new ButtonSectionBuilder(frame, ui, factory).build();
        new DebugSectionBuilder(frame, ui, factory).build();
    }

    /**
     * Restores settings and validates I18n keys.
     */
    public void postGUI() {

        frame.restoreSettings();

        var validator = new I18nValidator(
                ui,
                I18n.getBundle(),
                frame.getRootPane(),
                frame.getJMenuBar()
        );

        validator.validate();
        validator.validateUnusedKeys();
    }
}
