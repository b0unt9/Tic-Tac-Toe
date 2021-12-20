import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToeMain extends JFrame implements ActionListener {
    private JLabel levelChoiceLabel;
    private final JButton[] levelChoiceBtn = new JButton[3];
    private boolean showLevelChoiceBtn = false;

    public TicTacToeMain() {
        // 멀티 플레이 버튼
        JButton multiPlayBtn = new JButton("멀티 플레이");
        multiPlayBtn.setBounds(20, 20, 250, 50);

        multiPlayBtn.addActionListener(event -> {
            new TicTacToeGame(0);
            this.setVisible(false);
        });

        this.add(multiPlayBtn);

        // 싱글 플레이 버튼
        JButton singlePlayBtn = new JButton("싱글 플레이");
        singlePlayBtn.setBounds(20, 80, 250, 50);
        singlePlayBtn.addActionListener(event -> {
            showLevelChoiceBtn = !showLevelChoiceBtn;
            levelChoiceLabel.setVisible(showLevelChoiceBtn);
            for (int i = 0; i < 3; i++) {
                levelChoiceBtn[i].setVisible(showLevelChoiceBtn);
            }
            this.setSize(310, (showLevelChoiceBtn) ? 410 : 190);
        });

        this.add(singlePlayBtn);

        // 난이도 선택
        levelChoiceLabel = new JLabel(TicTacToeGame.setColor("싱글 플레이 난이도", "blue"));
        levelChoiceLabel.setBounds(20, 140, 250, 50);
        levelChoiceLabel.setVisible(false);
        this.add(levelChoiceLabel);

        levelChoiceBtn[0] = new JButton("쉬움");
        levelChoiceBtn[1] = new JButton("보통");
        levelChoiceBtn[2] = new JButton("어려움");

        for (int i = 0; i < 3; i++) {
            levelChoiceBtn[i].setBounds(20, 180 + 60 * i, 250, 50);
            levelChoiceBtn[i].setVisible(false);
            levelChoiceBtn[i].addActionListener(this);
            this.add(levelChoiceBtn[i]);
        }

        setTitle("틱택토");
        setSize(310, 190);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        // 창 정중앙 배치
        Dimension frameSize = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - 410) / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 3; i++) {
            if (e.getSource() == levelChoiceBtn[i]) {
                new TicTacToeGame(i + 1);
                this.setVisible(false);
            }
        }
    }
}
