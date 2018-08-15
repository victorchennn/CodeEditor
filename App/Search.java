package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Search implements ActionListener{

    public static void main(String...args) {
        new Search(null);
    }

    Search(Editor editor) {
        JFrame frame = new JFrame();
        frame.setSize(900, 800);
        JPanel _container = new JPanel();
        _container.setLayout(new BoxLayout(_container, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("  ");
        label.setPreferredSize(new Dimension(900, 680));
        _container.add(label);
        _editor = editor;
        createPanel();
        _container.add(_search);
        frame.add(_container, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {

    }

    private void createPanel() {
        _search = new JPanel();
        _search.setLayout(new BoxLayout(_search, BoxLayout.Y_AXIS));
        _search.setPreferredSize(new Dimension(900, 120));
        _search.add(createStatusPanel());
        _search.add(createFindPanel());
        _search.add(createReplacePanel());
    }

    private JPanel createStatusPanel() {
        JPanel status = new JPanel();
        status.setPreferredSize(new Dimension(900, 40));
        status.setLayout(new BorderLayout());
        _results = new JLabel("  Find in current Buffer", JLabel.LEFT);
        _results.setPreferredSize(new Dimension(380, 40));
        _options = new JLabel(STATUS, JLabel.RIGHT);
        _options.setPreferredSize(new Dimension(380, 40));
        status.add(_results, BorderLayout.WEST);
        status.add(_options, BorderLayout.CENTER);

        JButton ca = createButton(CASE, CASE_TIP, e -> _matchcase = !_matchcase);
        JButton se = createButton(SEL, SEL_TIP, e -> _selected = !_selected);
        JButton updown = createButton(UD, UD_TIP, e -> _up = !_up);
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

    /** Fonts. */
    private static final Font DIS_FONT =
            new Font("LucidaGrande", Font.PLAIN, 15);

    /** Commands. */
    private static final String
            FIND_INIT = "Find in current buffer", FIND = "Find",
            FINDALL = "Find All", REP_INIT = "Replace in current buffer",
            REP = "Replace", REPALL = "Replace All", CASE = "A a",
            CASE_TIP = "Match Case", SEL = "< >", SEL_TIP = "Only In Selection",
            UD = "⬆⬇", UD_TIP = "Previous or Next Occurrence", CLOSE = "╳",
            CLOSE_TIP = "Close Panel", STATUS = "Finding with options: Case Insensitive  ";

    /** True if choose only in selection button. */
    private boolean _selected;

    /** True if choose find previous occurrence button. */
    private boolean _up;

    /** True if choose match case button. */
    private boolean _matchcase;

    /** Whole search panel. */
    private JPanel _search;

    /** Show search results. */
    private JLabel _results;

    /** Show search options. */
    private JLabel _options;

    /** Input of words need to be found. */
    private JTextField _find;

    /** Input of new words. */
    private JTextField _replace;

    /** Working editor. */
    private Editor _editor;
}
