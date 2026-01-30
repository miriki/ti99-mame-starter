package com.miriki.ti99.mame.ui.builder;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miriki.ti99.mame.ui.MainAppFrameComponents;
import com.miriki.ti99.mame.ui.util.Listeners;

/**
 * Factory for creating Swing components with consistent styling and metadata.
 */
public class UiFactory {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UiFactory.class);
    // private final MainAppFrame frame;
    private final MainAppFrameComponents ui;

    /*
    public UiFactory(MainAppFrame frame, MainAppFrameComponents ui) {
        this.frame = frame;
        this.ui = ui;
    }
    */

    public UiFactory(MainAppFrameComponents ui) {
        this.ui = ui;
    }

    // -------------------------------------------------------------------------
    // I18n key derivation
    // -------------------------------------------------------------------------

    /**
     * Derives an I18n key from a component name.
     */
    private String deriveI18nKey(String name) {

        String core = name.replaceFirst("^(lbl|txt|cbx|btn|menu)", "");
        String key = core.toLowerCase();

        return name.startsWith("menu")
                ? "menu." + key
                : "main." + key;
    }

    // -------------------------------------------------------------------------
    // LABEL
    // -------------------------------------------------------------------------

    public JLabel createLabel(Container parent,
                              String text,
                              int x, int y, int w, int h,
                              JComponent forComponent) {

    	/*
    	if ( "Slot #1".equals(text)) { 
    		log.trace( "Label - text='{}'", text );
    	}
    	*/
    	
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setBounds(x, y, w, h);

        if (forComponent != null) {
            lbl.setLabelFor(forComponent);
        }

        parent.add(lbl);
        return lbl;
    }

    // -------------------------------------------------------------------------
    // TEXTBOX
    // -------------------------------------------------------------------------

    public JTextField createTextBox(Container parent,
                                    String name,
                                    String text,
                                    int x, int y, int w, int h,
                                    ActionListener listener) {

        JTextField field = new JTextField();
        field.setName(name);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setText(text);
        field.setBounds(x, y, w, h);
        field.setHorizontalAlignment(SwingConstants.LEFT);

        if (listener != null) {
            field.getDocument().addDocumentListener(
                    Listeners.documentAsAction(field, listener)
            );

            FocusListener fl = Listeners.normalizeOnFocusLost(field);
            field.putClientProperty("normalizeListener", fl);
            field.addFocusListener(fl);
        }

        field.putClientProperty("i18n", deriveI18nKey(name));

        parent.add(field);
        return field;
    }

    public JTextField createTextBoxWithLabel(Container parent,
                                             String name,
                                             String text,
                                             int x, int y, int w, int h,
                                             ActionListener listener,
                                             String labelText,
                                             int labelWidth) {

        JTextField field = createTextBox(parent, name, text, x, y, w, h, listener);

        String labelName = "lbl" + name.substring(3);
        JLabel lbl = createLabel(parent, labelText, x - 8 - labelWidth, y, labelWidth, h, field);
        lbl.setName(labelName);
        lbl.putClientProperty("i18n", deriveI18nKey(labelName));

        ui.bind(name, field);
        ui.bind(labelName, lbl);

        return field;
    }

    // -------------------------------------------------------------------------
    // COMBOBOX
    // -------------------------------------------------------------------------

    public JComboBox<String> createComboBox(Container parent,
                                            String name,
                                            ComboBoxModel<String> model,
                                            int x, int y, int w, int h,
                                            int selectedIndex,
                                            ActionListener listener) {

        JComboBox<String> box = new JComboBox<>(model);
        box.setName(name);

        if (selectedIndex >= 0 && selectedIndex < model.getSize()) {
            box.setSelectedIndex(selectedIndex);
        } else {
            box.setSelectedIndex(0);
        }

        box.setFont(new Font("Arial", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
        box.setBounds(x, y, w, h);

        if (listener != null) {
            box.addActionListener(Listeners.comboBoxChange(box, listener));
        }

        box.putClientProperty("i18n", deriveI18nKey(name));

        parent.add(box);
        return box;
    }

    public JComboBox<String> createComboBoxWithLabel(Container parent,
                                                     String name,
                                                     ComboBoxModel<String> model,
                                                     int x, int y, int w, int h,
                                                     int selectedIndex,
                                                     ActionListener listener,
                                                     String labelText,
                                                     int labelWidth) {

    	/*
    	if ( "Slot #1".equals(labelText) ) { 
    		log.trace( "ComboBox Label - text='{}'", labelText ); 
    	}
    	*/
    	
        JComboBox<String> box = createComboBox(parent, name, model, x, y, w, h, selectedIndex, listener);

        String labelName = "lbl" + name.substring(3);
        JLabel lbl = createLabel(parent, labelText, x - 8 - labelWidth, y, labelWidth, h, box);
        lbl.setName(labelName);
        lbl.putClientProperty("i18n", deriveI18nKey(labelName));

        ui.bind(name, box);
        ui.bind(labelName, lbl);

        return box;
    }

    // -------------------------------------------------------------------------
    // Convenience overload
    // -------------------------------------------------------------------------

    public JComboBox<String> createComboBoxWithLabel(Container parent,
                                                     String name,
                                                     String[] values,
                                                     int x, int y, int w, int h,
                                                     int selectedIndex,
                                                     ActionListener listener,
                                                     String labelText,
                                                     int labelWidth) {

        return createComboBoxWithLabel(
                parent,
                name,
                new DefaultComboBoxModel<>(values),
                x, y, w, h,
                selectedIndex,
                listener,
                labelText,
                labelWidth
        );
    }
}
