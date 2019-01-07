package App;

import javax.swing.ButtonGroup;
import javax.swing.UIManager;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import java.awt.Component;

/** GUI Style commands.
 *  @author Victor Chen
 */
class GUIStyles {

    GUIStyles(JMenu menu, Component component){
        _menu = menu;
        _com = component;
        createStyle();
    }

    /** Create Style Menus. */
    private void createStyle() {
        UIManager.LookAndFeelInfo[] styles = UIManager.getInstalledLookAndFeels();
        JMenu menuStyle = new JMenu("UI Style");
        JRadioButtonMenuItem[] items = new JRadioButtonMenuItem[styles.length];
        ButtonGroup styleGroup = new ButtonGroup();

        for (int i = 0; i < styles.length; i++) {
            items[i] = new JRadioButtonMenuItem(styles[i].getName());
            items[i].setMnemonic(styles[i].getName().charAt(0));
            String className = styles[i].getClassName();
            items[i].addActionListener(e -> setActionListener(className));
            menuStyle.add(items[i]);
            styleGroup.add(items[i]);
        }
        _menu.add(menuStyle);
        items[3].setSelected(true);
    }

    /** Set actionListener to each style menuItem. */
    private void setActionListener(String classname) {
        try {
            UIManager.setLookAndFeel(classname);
            SwingUtilities.updateComponentTreeUI(_com);
        } catch (Exception e) {
            /* ... */
        }
    }

    /** Style menu. */
    private JMenu _menu;

    /** The component which we want to change style. */
    private Component _com;
}

