package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 *  @author Victor Chen
 */
class Menubar {

    Menubar(GUI gui) {
        _menuBar = new JMenuBar();
        _manager = new FileManagement(gui);
    }

    JMenuBar createMenuBar() {
        createSignMenu();
        createFileMenu();
        createEditMenu();
        createViewMenu();
        createHelpMenu();
        return _menuBar;
    }

    FileManagement getManager() {
        return _manager;
    }

    /** Create Signature Menu. */
    private void createSignMenu() {
        JMenu menu = createMenu(SIGN, _menuBar);
        menu.setFont(SIGN_FONT);
//        createMenuItem(ABOUT, menu, this);
        menu.addSeparator();
        createMenuItem(QUIT, menu, KeyEvent.VK_Q, false, e -> System.exit(0));
    }

    /** Create File Menu. */
    private void createFileMenu() {
        JMenu menu = createMenu(FILE, _menuBar);
        createMenuItem(NEW_WINDOW, menu, KeyEvent.VK_N, true, e -> new GUI());
        createMenuItem(NEW_FILE, menu, KeyEvent.VK_N, false, e -> _manager.newFile());
        createMenuItem(OPEN, menu, KeyEvent.VK_O, false, e -> _manager.openFile());
        _reopen = createMenu(REOPEN, menu);
        _reopen.setEnabled(false);
        _reopenlast = createMenuItem(REOPEN_LAST, menu, KeyEvent.VK_T, true, e -> _manager.openLastFile());
        _reopenlast.setEnabled(false);
        menu.addSeparator();
        createMenuItem(SAVE, menu, KeyEvent.VK_S, false, e -> _manager.saveFile());
        createMenuItem(SAVE_AS, menu, KeyEvent.VK_S, true, e -> _manager.saveAsFile());
        createMenuItem(SAVE_ALL, menu, e -> _manager.saveAll());
        menu.addSeparator();
        createMenuItem(CLOSE_TAB, menu, KeyEvent.VK_W, false, e -> _manager.closeFile());
//        createMenuItem(CLOSE_WINDOW, menu, KeyEvent.VK_W, true, e -> );
        createMenuItem(CLOSE_ALL, menu, e -> _manager.closeAll());
    }

    /** Create history JMenuItems in REOPEN JMenu. */
    void createHistory() {
        _reopen.setEnabled(true);
        _reopenlast.setEnabled(true);
        _reopen.removeAll();
        createMenuItem(CLEAR_H, _reopen, e -> _manager.clearHistory());
        _reopen.addSeparator();
        for (Editor ed : _manager.getHistory()) {
            JMenuItem newItem = new JMenuItem(ed.getFile().getAbsolutePath());
            newItem.addActionListener(e -> _manager.open(ed.getFile()));
            _reopen.add(newItem, 2);
        }
    }

    /** Create Edit Menu. */
    private void createEditMenu() {
        JMenu menu = createMenu(EDIT, _menuBar);
    }

    /** Create View Menu. */
    private void createViewMenu() {
        JMenu menu = createMenu(VIEW, _menuBar);
    }

    /** Create Help Menu. */
    private void createHelpMenu() {
        JMenu menu = createMenu(HELP, _menuBar);
    }


    private JMenu createMenu(String command, JMenuBar menuBar) {
        JMenu menu = new JMenu(command);
        menuBar.add(menu);
        return menu;
    }

    private JMenu createMenu(String command, JMenu toMenu) {
        JMenu addMenu = new JMenu(command);
        toMenu.add(addMenu);
        return addMenu;
    }

    /** Add a menuitem without accelerator to MENU. */
    private void createMenuItem(String command, JMenu toMenu,
                                ActionListener actListener) {
        createMenuItem(command, toMenu, 0, false, actListener);
    }

    /** Add a menuitem to MENU. If it has an accelerator key, use ctrl or
     * ctrl + shift commend based on boolean ADVANCE. */
    private JMenuItem createMenuItem(String command, JMenu toMenu, int aclKey,
                                boolean advance, ActionListener actListener) {
        JMenuItem menuitem = new JMenuItem(command);
        menuitem.addActionListener(actListener);
        if (aclKey != 0) {
            if (advance) {
                menuitem.setAccelerator(KeyStroke.getKeyStroke(aclKey,
                        InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
            } else {
                menuitem.setAccelerator(KeyStroke.getKeyStroke(aclKey,
                        InputEvent.CTRL_MASK));
            }
        }
        toMenu.add(menuitem);
        return menuitem;
    }

    /** Fonts. */
    private static final Font
            SIGN_FONT = new Font("LucidaGrande", Font.BOLD, 14);

    /** Commends. */
    private static final String
            SIGN = "Victor", FILE = "File", EDIT = "Edit", VIEW = "View",
            HELP = "Help",

            ABOUT = "About ...", QUIT = "Quit",

            NEW_WINDOW = "New Window", NEW_FILE = "New File", OPEN = "Open",
            REOPEN = "Reopen", REOPEN_LAST = "Reopen Last Item", SAVE = "Save",
            SAVE_AS = "Save As...", SAVE_ALL = "Save All", CLOSE_TAB = "Close Tab",
            CLOSE_ALL = "Close All", CLOSE_WINDOW = "Close Window",
            CLEAR_H = "Clear History";

    /** Menu REOPEN_LAST. */
    private JMenuItem _reopenlast;

    /** Menu REOPEN. */
    private JMenu _reopen;

    /** MenuBar. */
    private JMenuBar _menuBar;

    /** File Commends Manager . */
    private FileManagement _manager;
}
