package App;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;

import static java.awt.Color.*;

/** All the commands about searching, including find, findAll,
 * replace and replaceAll with optional search options like
 * case match, if in selected text and next or previous occurrence.
 *  @author Victor Chen
 */
class Search{

    /** Fonts. */
    private static final Font
            DIS_FONT = new Font("LucidaGrande", Font.PLAIN, 15),
            STA_FONT = new Font("LucidaGrande", Font.PLAIN, 11);

    /** Colors. */
    static final Color
            BORDER = new Color(204, 204, 204),
            FOUND = new Color(18, 197, 28),
            HILIGHT = new Color(164, 205, 255);

    /** Commands. */
    private static final String
            FIND_INIT = " Find in current buffer ", FIND = "Find",
            FINDALL = " Find All ", REP_INIT = " Replace in current buffer ",
            REP = "Replace", REPALL = "Replace All", CASE = "A a",
            CASE_TIP = " Match Case ", SEL = "< >", SEL_TIP = " Only In Selection ",
            UD = "⬆⬇", UD_TIP = " Previous or Next Occurrence ", CLOSE = "╳",
            CLOSE_TIP = " Close Panel ", STATUS = "Finding with options: ",
            NCASE = "Case Insensitive", YCASE = "Case Sensitive",
            YSE = ", Within Current Selection", DF = ", Find Next Occurrence ",
            UF = ", Find Previous Occurrence ", FIND_TIP = " Find Next ",
            REP_TIP = " Replace Next [when there are results] ",
            REPALL_TIP = " Replace All [when there are results] ";


    /** Set default highlight painter to BLUE. */
    private static final Highlighter.HighlightPainter painter =
            new DefaultHighlighter.DefaultHighlightPainter(HILIGHT);

//    /** Used for Test. */
//    public static void main(String...args) {
//        JFrame frame = new JFrame();
//        frame.setSize(900, 800);
//        JPanel _container = new JPanel();
//        _container.setLayout(new BoxLayout(_container, BoxLayout.Y_AXIS));
//        JLabel label = new JLabel("  ");
//        label.setPreferredSize(new Dimension(900, 675));
//        _container.add(label);
//        Search a = new Search(null);
//        _container.add(a.getSearch());
//        frame.add(_container, BorderLayout.CENTER);
//        frame.setVisible(true);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//    }

    /** Create a new search panel. */
    Search(Editor editor) {
        _editor = editor;
        _search = new JPanel();
        _search.setLayout(new BorderLayout());
        JLabel blank1 = new JLabel(" ");
        blank1.setPreferredSize(new Dimension(10, 125));
        JLabel blank2 = new JLabel(" ");
        blank2.setPreferredSize(new Dimension(10, 125));
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(createStatusPanel());
        center.add(createFindPanel());
        center.add(createReplacePanel());
        _search.add(blank1, BorderLayout.WEST);
        _search.add(center, BorderLayout.CENTER);
        _search.add(blank2, BorderLayout.EAST);
        center.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(0, 2, 0, 2)));
        start = _editor.getCaretPosition();
        end = _editor.getCaretPosition();
        _selectedText = _editor.getSelectedText();
    }

    /** Return the search panel. */
    JPanel getSearch() {
        return _search;
    }

    /** Find the string and highlight it, set the status bar with results, including
     * total occurrences of it and its specific order in the text. If there is no
     * occurrence in text, set alarm hint in the status bar with highlighted red. */
    private void setFindSelection() {
        String tofind = _find.getText();
        String text = _selected? _selectedText : _editor.getText();
        if (!tofind.equals("")) {
            int i = find(tofind, text);
            if (i >= 0) {
                _editor.setSelectionStart(i);
                _editor.setSelectionEnd(i + _find.getText().length());
                _editor.setSelectedTextColor(YELLOW);
                start = _editor.getSelectionEnd();
                end = _editor.getSelectionStart() - 1;
                int[] occur = getOccurrence(tofind, text, _matchcase, i);
                _results.setForeground(BLACK);
                if (occur[0] == 1) {
                    _results.setText(String.format(" 1 result found for '%s'", tofind));
                } else {
                    _results.setText(String.format(" %d results found for '%s', %d of %d",
                            occur[0], tofind, occur[1], occur[0]));
                }
                _results.setForeground(FOUND);
            } else if (i == -2) {
                _results.setForeground(RED);
                _results.setText(String.format(" No results found for '%s'", _find.getText()));
            } else {
                start = 0;
                end = _editor.getText().length();
                setFindSelection();
            }
        }
    }

    /** Find a specific string TOFIND in the text TEXT. Check if the text contains the
     * string at first, then search it by choosing whether to match case and search order
     * (next or previous occurrence).
     * @return the start index of the first or last occurrence of the specified substring,
     *          searching forward or backward from the specified index START and END,
     *          or {@code -1} if reach the start or end of text,
     *          or {@code -2} if there is no such occurrence.
     */
    private int find(String tofind, String text) {
        if (_matchcase) {
            if (!text.contains(tofind)) {
                return -2;
            }
        } else {
            if (!text.toLowerCase().contains(tofind.toLowerCase())) {
                return -2;
            }
        }
        int index;
        if (_up) {
            index = _matchcase? text.lastIndexOf(tofind, end) :
                    text.toLowerCase().lastIndexOf(tofind.toLowerCase(), end);
        } else {
            index = _matchcase? text.indexOf(tofind, start) :
                    text.toLowerCase().indexOf(tofind.toLowerCase(), start);
        }
        return index;
    }

    /** Create a database to record occurrences of a certain string. Divided by if choose
     * to match case, for each case, record the start index and order of that found one. */
    private int[] getOccurrence(String key, String text, boolean matchcase, int fromindex) {
        int[] results = new int[2];
        int two = 2;
        boolean mc = true;
        while (two > 0) {
            int i = 1, index = 0;
            HashMap<Integer, Integer> keyoccur = new HashMap<>();
            while (index != -1) {
                index = mc? text.indexOf(key, index):
                        text.toLowerCase().indexOf(key.toLowerCase(), index);
                if (index != -1) {
                    keyoccur.put(index, i);
                    index += key.length();
                    i++;
                }
            }
            occur.put(mc, keyoccur);
            mc = false;
            two--;
        }
        results[0] = occur.get(matchcase).size();
        results[1] = occur.get(matchcase).get(fromindex);
        return results;
    }

    /** Find all the needed strings in text and highlight them. */
    private void findall() {
        setFindSelection();
        Highlighter highlighter = _editor.getHighlighter();
        highlighter.removeAllHighlights();
        String tofind = _find.getText();
        if (!tofind.equals("")) {
            try {
                for (int i : occur.get(_matchcase).keySet()) {
                    highlighter.addHighlight(i, i + tofind.length(), painter);
                }
            } catch (BadLocationException e) {
                /* ... */
            }
        }
    }

    /** Replace currently selected string to a new string. */
    private void replace() {
        String tofind = _find.getText();
        String newone = _replace.getText();
        if (!tofind.equals("") && !newone.equals("")) {
            setFindSelection();
            if (_editor.getSelectedText() != null) {
                _editor.replaceSelection(newone);
            }
        }
    }

    /** Replace all the strings need to be replaced to the new string
     * and highlight them. */
    private void replaceall() {
        String tofind = _find.getText();
        String newone = _replace.getText();
        if (!tofind.equals("") && !newone.equals("")) {
            setFindSelection();
            Highlighter highlighter = _editor.getHighlighter();
            highlighter.removeAllHighlights();
            try {
                for (int i : occur.get(_matchcase).keySet()) {
                    _editor.replaceRange(newone, i, i + newone.length());
                    highlighter.addHighlight(i, i + newone.length(), painter);
                }
            } catch (BadLocationException e) {
                /* ..*/
            }
        }
    }

    /** Create the status panel in the search panel. */
    private JPanel createStatusPanel() {
        JPanel status = new JPanel();
        status.setPreferredSize(new Dimension(900, 45));
        status.setLayout(new BorderLayout());
        JLabel a = new JLabel("");
        a.setPreferredSize(new Dimension(900, 5));
        status.add(a, BorderLayout.NORTH);
        _results = new JLabel(FIND_INIT, JLabel.LEFT);
        _results.setPreferredSize(new Dimension(200, 40));
        _results.setFont(STA_FONT);
        _options = new JLabel(STATUS + NCASE + DF, JLabel.RIGHT);
        _options.setPreferredSize(new Dimension(560, 40));
        _options.setFont(STA_FONT);
        status.add(_results, BorderLayout.WEST);
        status.add(_options, BorderLayout.CENTER);

        JButton ca = createButton(CASE, CASE_TIP, e -> { _matchcase = !_matchcase;
            resetOptions(); }, true);
        JButton se = createButton(SEL, SEL_TIP, e -> { _selected = !_selected;
            resetOptions();}, true);
        JButton updown = createButton(UD, UD_TIP, e -> { _up = !_up;
            resetOptions();}, true);
        JButton close = createButton(CLOSE, CLOSE_TIP, e -> { _search.setVisible(false);
            _editor.getHighlighter().removeAllHighlights();}, true);
        close.setBorder(BorderFactory.createEmptyBorder());

        JPanel commands = new JPanel();
        commands.setSize(new Dimension(140, 40));
        commands.setLayout(new BoxLayout(commands, BoxLayout.X_AXIS));
        commands.add(ca);
        commands.add(se);
        commands.add(updown);
        commands.add(close);
        status.add(commands, BorderLayout.EAST);
        return status;
    }

    /** Create the find panel in the search panel. */
    private JPanel createFindPanel() {
        _find = new JTextField();
        _find.setToolTipText(FIND_INIT);
        JButton find = createButton(FIND, FIND_TIP, e -> setFindSelection(), false);
        JButton findall = createButton(FINDALL, FINDALL, e -> findall(), false);
        return createInput(_find, find, findall);
    }

    /** Create the replace panel in the search panel. */
    private JPanel createReplacePanel() {
        _replace = new JTextField();
        _replace.setToolTipText(REP_INIT);
        JButton rep = createButton(REP, REP_TIP, e -> replace(), false);
        JButton repall = createButton(REPALL, REPALL_TIP, e -> replaceall(), false);
        return createInput(_replace, rep, repall);
    }

    /** Helper of creating find and replace panel. */
    private JPanel createInput(JTextField tx, JButton one, JButton two) {
        JPanel input = new JPanel();
        input.setPreferredSize(new Dimension(900, 40));
        input.setLayout(new BorderLayout());
        tx.setPreferredSize(new Dimension(600, 40));
        one.setPreferredSize(new Dimension(20, 40));
        two.setPreferredSize(new Dimension(180, 40));
        tx.setFont(DIS_FONT);
        input.add(tx, BorderLayout.WEST);
        input.add(one, BorderLayout.CENTER);
        input.add(two, BorderLayout.EAST);
        return input;
    }

    /** Create button in the search panel. */
    private JButton createButton(String text, String tip, ActionListener ac, boolean setsize) {
        JButton button = new JButton(text);
        if (setsize) {
            button.setPreferredSize(new Dimension(35, 40));
        }
        button.setToolTipText(tip);
        button.addActionListener(ac);
        return button;
    }

    /** Create search options status. */
    private void resetOptions() {
        String ops = STATUS;
        ops += _matchcase? YCASE:NCASE;
        ops += _selected? YSE:"";
        ops += _up? UF:DF;
        _options.setText(ops);
    }

    /** Record selected text when opening a new search panel. */
    private String _selectedText;

    /** Record index and occurrences of substring in the text, divided by if
     * choose to match case, matchcase->index->order. */
    private HashMap<Boolean, HashMap<Integer, Integer>> occur = new HashMap<>();

    /** Record start and end index of search string. */
    private int start, end;

    /** True if click the button. */
    private boolean _selected = false, _up = false, _matchcase = false;

    /** Whole search panel. */
    private JPanel _search;

    /** Show search status. */
    private JLabel _results, _options;

    /** Input of words need to be found and new words. */
    private JTextField _find, _replace;

    /** Working editor. */
    private Editor _editor;
}
