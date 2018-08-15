package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Search{

    public static void main(String...args) {
        JFrame frame = new JFrame();
        frame.setSize(900, 800);
        JPanel _container = new JPanel();
        _container.setLayout(new BoxLayout(_container, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("  ");
        label.setPreferredSize(new Dimension(900, 670));
        _container.add(label);
        Search a = new Search(null);
        _container.add(a.getSearch());
        frame.add(_container, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    Search(Editor editor) {
        _editor = editor;
        _search = new JPanel();
        _search.setLayout(new BoxLayout(_search, BoxLayout.Y_AXIS));
        _search.setPreferredSize(new Dimension(900, 125));
        _search.add(createStatusPanel());
        _search.add(createFindPanel());
        _search.add(createReplacePanel());
        _search.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private int find() {
        String tofind = _find.getText();
        String text = _selected? _editor.getSelectedText() : _editor.getText();
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

    private void setFindSelection() {
        int i = find();
        if (i != -1) {
            _editor.setSelectionStart(i);
            _editor.setSelectionEnd(i + _find.getText().length());
            start = _editor.getSelectionEnd();
            end = _editor.getSelectionStart() - 1;
        } else {
            start = 0;
            end = _editor.getText().length();
            setFindSelection();
        }
    }

    JPanel getSearch() {
        return _search;
    }

    private JPanel createStatusPanel() {
        JPanel status = new JPanel();
        status.setPreferredSize(new Dimension(900, 45));
        status.setLayout(new BorderLayout());
        JLabel a = new JLabel("");
        a.setPreferredSize(new Dimension(900, 5));
        status.add(a, BorderLayout.NORTH);
        _results = new JLabel("  Find in current Buffer", JLabel.LEFT);
        _results.setPreferredSize(new Dimension(200, 40));
        _results.setFont(STA_FONT);
        _options = new JLabel(STATUS + NCASE + DF, JLabel.RIGHT);
        _options.setPreferredSize(new Dimension(560, 40));
        _options.setFont(STA_FONT);
        status.add(_results, BorderLayout.WEST);
        status.add(_options, BorderLayout.CENTER);

        JButton ca = createButton(CASE, CASE_TIP, e -> { _matchcase = !_matchcase;
            resetOptions(); });
        JButton se = createButton(SEL, SEL_TIP, e -> { _selected = !_selected;
            resetOptions();});
        JButton updown = createButton(UD, UD_TIP, e -> { _up = !_up;
            resetOptions();});
        JButton close = createButton(CLOSE, CLOSE_TIP, e -> _search.setVisible(false));
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

    private JPanel createFindPanel() {
        _find = new JTextField();
        _find.setToolTipText(FIND_INIT);
        JButton find = new JButton(FIND);
        find.addActionListener(e -> setFindSelection());
        JButton findall = new JButton(FINDALL);
        return createInput(_find, find, findall);
    }

    private JPanel createReplacePanel() {
        _replace = new JTextField();
        _replace.setToolTipText(REP_INIT);
        JButton rep = new JButton(REP);
        JButton repall = new JButton(REPALL);
        return createInput(_replace, rep, repall);
    }

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

    private JButton createButton(String text, String tip, ActionListener ac) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(35, 40));
        button.setToolTipText(tip);
        button.addActionListener(ac);
        return button;
    }

    private void resetOptions() {
        String ops = STATUS;
        ops += _matchcase? YCASE:NCASE;
        ops += _selected? YSE:"";
        ops += _up? UF:DF;
        _options.setText(ops);
    }

    /** Fonts. */
    private static final Font
            DIS_FONT = new Font("LucidaGrande", Font.PLAIN, 15),
            STA_FONT = new Font("LucidaGrande", Font.PLAIN, 11);


    /** Commands. */
    private static final String
            FIND_INIT = "Find in current buffer", FIND = "Find",
            FINDALL = "Find All", REP_INIT = "Replace in current buffer",
            REP = "Replace", REPALL = "Replace All", CASE = "A a",
            CASE_TIP = "Match Case", SEL = "< >", SEL_TIP = "Only In Selection",
            UD = "⬆⬇", UD_TIP = "Previous or Next Occurrence", CLOSE = "╳",
            CLOSE_TIP = "Close Panel", STATUS = "Finding with options: ",
            NCASE = "Case Insensitive", YCASE = "Case Sensitive",
            YSE = ", Within Current Selection", DF = ", Find Next Occurrence ",
            UF = ", Find Previous Occurrence ";

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
