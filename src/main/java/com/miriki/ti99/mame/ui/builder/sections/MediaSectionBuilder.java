package com.miriki.ti99.mame.ui.builder.sections;

import java.awt.Container;

import javax.swing.DefaultComboBoxModel;

import com.miriki.ti99.mame.localization.I18n;
import com.miriki.ti99.mame.ui.MainAppFrame;
import com.miriki.ti99.mame.ui.MainAppFrameComponents;
import com.miriki.ti99.mame.ui.builder.UiFactory;

/**
 * Builds the media selection section.
 */
public class MediaSectionBuilder {

    private final MainAppFrame frame;
    private final MainAppFrameComponents ui;
    private final UiFactory factory;

    public MediaSectionBuilder(MainAppFrame frame,
                               MainAppFrameComponents ui,
                               UiFactory factory) {
        this.frame = frame;
        this.ui = ui;
        this.factory = factory;
    }

    /**
     * Adds and configures all media‑related combo boxes.
     */
    public void build() {

        Container parent = frame.getContentPane();

        // Cartridge
        ui.cbxCartridge = factory.createComboBox(
                parent,
                "cbxCartridge",
                new DefaultComboBoxModel<>(ui.CARTRIDGE_NAMES),
                512, 80, 144, 22,
                0,
                e -> frame.collectEmulatorOptions()
        );

        // Floppy 1–4
        ui.cbxFlop1 = factory.createComboBoxWithLabel(
                parent,
                "cbxFlop1",
                new DefaultComboBoxModel<>(ui.FLOPPYDISKIMAGE_NAMES),
                112, 176, 216, 24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.flop1"),
                48
        );

        ui.cbxFlop2 = factory.createComboBoxWithLabel(
                parent,
                "cbxFlop2",
                new DefaultComboBoxModel<>(ui.FLOPPYDISKIMAGE_NAMES),
                112 + 8 + 48 + 8 + 216, 176, 216, 24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.flop2"),
                48
        );

        ui.cbxFlop3 = factory.createComboBoxWithLabel(
                parent,
                "cbxFlop3",
                new DefaultComboBoxModel<>(ui.FLOPPYDISKIMAGE_NAMES),
                112 + 8 + 48 + 216 + 8 + 8 + 48 + 8 + 216, 176, 216, 24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.flop3"),
                48
        );

        ui.cbxFlop4 = factory.createComboBoxWithLabel(
                parent,
                "cbxFlop4",
                new DefaultComboBoxModel<>(ui.FLOPPYDISKIMAGE_NAMES),
                112 + 8 + 48 + 216 + 8 + 8 + 48 + 8 + 216 + 8 + 48 + 8 + 216,
                176,
                216,
                24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.flop4"),
                48
        );

        // Harddisk 1–3
        ui.cbxHard1 = factory.createComboBoxWithLabel(
                parent,
                "cbxHard1",
                new DefaultComboBoxModel<>(ui.HARDDISKIMAGE_NAMES),
                112, 208, 216, 24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.hard1"),
                48
        );

        ui.cbxHard2 = factory.createComboBoxWithLabel(
                parent,
                "cbxHard2",
                new DefaultComboBoxModel<>(ui.HARDDISKIMAGE_NAMES),
                112 + 8 + 48 + 8 + 216, 208, 216, 24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.hard2"),
                48
        );

        ui.cbxHard3 = factory.createComboBoxWithLabel(
                parent,
                "cbxHard3",
                new DefaultComboBoxModel<>(ui.HARDDISKIMAGE_NAMES),
                112 + 8 + 48 + 216 + 8 + 8 + 48 + 8 + 216,
                208,
                216,
                24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.hard3"),
                48
        );

        // Cassette 1–2
        ui.cbxCass1 = factory.createComboBoxWithLabel(
                parent,
                "cbxCass1",
                new DefaultComboBoxModel<>(ui.CASSETTEIMAGE_NAMES),
                112, 240, 216, 24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.cass1"),
                48
        );

        ui.cbxCass2 = factory.createComboBoxWithLabel(
                parent,
                "cbxCass2",
                new DefaultComboBoxModel<>(ui.CASSETTEIMAGE_NAMES),
                112 + 8 + 48 + 8 + 216,
                240,
                216,
                24,
                0,
                e -> frame.collectEmulatorOptions(),
                I18n.t("main.cass2"),
                48
        );
    }
}
