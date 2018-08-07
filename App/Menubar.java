package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *  @author Victor Chen
 */
class Menubar {

    Menubar() {
        _menuBar = new JMenuBar();
    }

    JMenuBar createMenuBar() {
        createSignMenu();
        createFileMenu();
        createEditMenu();
        createViewMenu();
        return _menuBar;
    }

    private void createSignMenu() {
        JMenu filemenu = createMenu(SIGN, 0, _menuBar);
        filemenu.setFont(SIGN_FONT);
    }

    private void createFileMenu() {
        JMenu menu = createMenu(FILE, KeyEvent.VK_F, _menuBar);
        createMenuItem(NEW_WINDOW, KeyEvent.VK_N, menu, KeyEvent.VK_N, e -> System.out.println("new window"));
        createMenuItem(NEW_FILE, KeyEvent.VK_N, menu, KeyEvent.VK_N, e -> System.out.println("new file"));
        createMenuItem(OPEN, KeyEvent.VK_O, menu, KeyEvent.VK_O, e -> System.out.println("open"));
        createMenuItem(REOPEN, menu, e -> System.out.println("reopen"));
        createMenuItem(REOPEN_LAST, menu, e -> System.out.println("reopen last item"));
    }

    private void createEditMenu() {
        JMenu menu = createMenu(EDIT, KeyEvent.VK_E, _menuBar);
    }

    private void createViewMenu() {
        JMenu menu = createMenu(VIEW, KeyEvent.VK_V, _menuBar);
    }

    private JMenu createMenu(String commend, int key, JMenuBar menuBar) {
        JMenu menu = new JMenu(commend);
        if (key != 0) {
            menu.setMnemonic(key);
        }
        menuBar.add(menu);
        return menu;
    }

    private void createMenuItem(String commend, JMenu menu, ActionListener al) {
        createMenuItem(commend, 0, menu, 0, al);
    }


    private void createMenuItem(String commend, int key, JMenu menu,
                                int aclKey, ActionListener al) {
        JMenuItem menuitem = key == 0?
                new JMenuItem(commend) : new JMenuItem(commend, key);
        menuitem.addActionListener(al);
        if (aclKey != 0) {
            menuitem.setAccelerator(KeyStroke.getKeyStroke(aclKey,
                    InputEvent.CTRL_MASK));
        }
        menu.add(menuitem);
    }

    /** Fonts. */
    private static final Font
            SIGN_FONT = new Font("LucidaGrande", Font.BOLD, 14);

    /** Commends. */
    private static final String
            SIGN = "Victor", FILE = "File", EDIT = "Edit", VIEW = "View",

            NEW_WINDOW = "New Window", NEW_FILE = "New File", OPEN = "Open",
            REOPEN = "Reopen", REOPEN_LAST = "Reopen Last Item";

    /** Menu bar. */
    private JMenuBar _menuBar;
}
