import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class TicTacToeGame extends JFrame implements ActionListener {
    // 보드 버튼
    private final JButton[] board;

    // 보드 버튼 크기
    private final static int BOARD_BUTTON_SIZE = 80;

    // 보드 버튼 위치
    private final static int[] BOARD_BUTTON_X = {30, BOARD_BUTTON_SIZE + 30, BOARD_BUTTON_SIZE * 2 + 30, BOARD_BUTTON_SIZE * 3 + 30};
    private final static int[] BOARD_BUTTON_Y = {120, BOARD_BUTTON_SIZE + 120, BOARD_BUTTON_SIZE * 2 + 120, BOARD_BUTTON_SIZE * 3 + 120 + 20};

    // 점수 보드
    private final JLabel TableOfScore;

    // 플레이어 점수
    private int player1Score = 0;
    private int player2Score = 0;

    // 게임 분류 & 난이도
    private final int GAME_LEVEL;
    private final int LEVEL_MULTI = 0; // 멀티 플레이
    private final int LEVEL_SINGLE_EASY = 1; // 싱글 플레이 - 쉬움
    private final int LEVEL_SINGLE_MEDIUM = 2; // 싱글 플레이 - 보통
    private final int LEVEL_SINGLE_HARD = 3; // 싱글 플레이 - 어려움

    // 플레이어 1 턴 
    private static boolean player1Trun = true;

    private static final Random random = new Random();
    private boolean randomSelect = true;

    // 인공지능의 계산을 편하게 하기 위해 미리 우승 가능성 있는 버튼 배열
    private final int[] playBoard = new int[8];

    private boolean centerFirst = false; // 중앙 선점 여부
    private boolean cornerFirst = false; // 모서리 선점 여부
    private boolean sideFirst = false; // 변 선점 여부
    private boolean sideAfterCorner = false; // 모서리 선택 후 변 선택 여부
    private boolean cornerAfterSide = false; // 변 선택 후 모서리 선택 여부
    private boolean cornerAfterCenter = false; // 모서리 선택 후 중앙 선택 여부
    private boolean sideAfterSide = false; // 변 선택 후 변 선택 여부
    private int counter = 0; // 선택된 버튼 개수
    private boolean corner = false; // 모서리 선택 여부

    /**
     * @param GAME_LEVEL 게임 종류
     */
    public TicTacToeGame(int GAME_LEVEL) {
        this.GAME_LEVEL = GAME_LEVEL;

        if (this.GAME_LEVEL == LEVEL_MULTI) {
            this.setTitle("틱택토 멀티플레이");
        } else if (this.GAME_LEVEL == LEVEL_SINGLE_EASY) {
            this.setTitle("틱택토 싱글플레이(쉬움)");
        } else if (this.GAME_LEVEL == LEVEL_SINGLE_MEDIUM) {
            this.setTitle("틱택토 싱글플레이(보통)");
        } else if (this.GAME_LEVEL == LEVEL_SINGLE_HARD) {
            this.setTitle("틱택토 싱글플레이(어려움)");
        }

        // 점수 표시 테이블
        TableOfScore = new JLabel("");
        printScore(player1Score, player2Score);
        TableOfScore.setBounds(30, 10, 240, 100);
        this.add(TableOfScore);

        // 틱택토 보드
        board = new JButton[9];

        for (int i = 0; i < board.length; i++) {
            board[i] = new JButton();
            if (i < 3) {
                board[i].setBounds(BOARD_BUTTON_X[i], BOARD_BUTTON_Y[0], BOARD_BUTTON_SIZE, BOARD_BUTTON_SIZE);
            } else if (i < 6) {
                board[i].setBounds(BOARD_BUTTON_X[i - 3], BOARD_BUTTON_Y[1], BOARD_BUTTON_SIZE, BOARD_BUTTON_SIZE);
            } else {
                board[i].setBounds(BOARD_BUTTON_X[i - 6], BOARD_BUTTON_Y[2], BOARD_BUTTON_SIZE, BOARD_BUTTON_SIZE);
            }

            board[i].setBackground(Color.WHITE);
            this.add(board[i]);
        }

        for (int i = 0; i < 9; i++) {
            board[i].addActionListener(this);
        }

        // 점수 초기화 버튼
        JButton btnReset = new JButton("점수 초기화");
        btnReset.setBounds(BOARD_BUTTON_X[0], BOARD_BUTTON_Y[3], BOARD_BUTTON_SIZE * 3, 40);

        btnReset.addActionListener(event -> {
            clear();
            resetScore();
        });
        this.add(btnReset);

        // 현재 진행 중인 게임 초기화
        JButton btnClear = new JButton("진행 초기화");
        btnClear.setBounds(BOARD_BUTTON_X[0], BOARD_BUTTON_Y[3] + btnReset.getHeight() + 10, BOARD_BUTTON_SIZE * 3, 40);
        btnClear.addActionListener(event -> clear());
        this.add(btnClear);

        // 메인으로 가는 버튼
        JButton btnBackToMain = new JButton("메인으로");
        btnBackToMain.setBounds(BOARD_BUTTON_X[0], BOARD_BUTTON_Y[3] + btnReset.getHeight() + 10 + btnClear.getHeight() + 10, BOARD_BUTTON_SIZE * 3, 40);
        btnBackToMain.addActionListener(event -> {
            this.setVisible(false);
            new TicTacToeMain();
        });
        add(btnBackToMain);

        // 창 사이즈
        this.setSize(board[0].getWidth() * 3 + 75, TableOfScore.getHeight() + board[0].getHeight() * 3 + btnReset.getHeight() + btnClear.getHeight() + btnBackToMain.getHeight() + 120);

        // 창 정중앙 배치
        Dimension frameSize = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 멀티 플레이
        if (GAME_LEVEL == LEVEL_MULTI) {
            for (int i = 0; i < 9; i++) {
                if (e.getSource().equals(board[i]))
                    multiPlay(i);
            }
            // 싱글 플레이 - 쉬움
        } else if (GAME_LEVEL == LEVEL_SINGLE_EASY) {
            for (int i = 0; i < 9; i++) {
                if (e.getSource() == board[i]) {
                    if (board[i].getText().equals("")) {
                        singlePlayEasy(i);
                    }
                }
            }
            // 싱글 플레이 - 보통
        } else if (GAME_LEVEL == LEVEL_SINGLE_MEDIUM) {
            for (int i = 0; i < 9; i++) {
                if (e.getSource() == board[i]) {
                    if (board[i].getText().equals("")) {
                        singlePlayMediumPlayer(i);

                        if (getResult(true)) {
                            singlePlayMediumAI();
                            getResult(false);
                        }
                    }
                }
            }
            // 싱글 플레이 - 어려움
        } else if (GAME_LEVEL == LEVEL_SINGLE_HARD) {
            for (int i = 0; i < 9; i++) {
                if (e.getSource() == board[i]) {
                    if (board[i].getText().equals("")) {
                        singlePlayHardPlayer(i);

                        if (getResult(true)) {
                            singlePlayHardAI();
                            getResult(false);
                        }
                    }
                }
            }
        }

    }

    /**
     * HTML 태그 포함 글 색 설정
     *
     * @param text  텍스트
     * @param color 색
     * @return HTML 태그 포함 HTML 코드
     */
    public static String setColor(String text, String color) {
        return "<html><font color='" + color + "'>" + text + "</font></html>";
    }

    /**
     * HTML 미 포함 글 색 설정
     *
     * @param text  텍스트
     * @param color 색
     * @return HTML 태그 미 포함 HTML 코드
     */
    public static String setColorOnly(String text, String color) {
        return "<font color='" + color + "'>" + text + "</font>";
    }

    /**
     * 점수판 데이터 출력
     *
     * @param player1 플레이어 1
     * @param player2 플레이어 2
     */
    private void printScore(int player1, int player2) {
        String p1Score = setColorOnly(String.valueOf(player1), "green");
        String p2Score = setColorOnly(String.valueOf(player2), "green");
        if (player1 > player2) {
            p1Score = setColorOnly(String.valueOf(player1), "green");
            p2Score = setColorOnly(String.valueOf(player2), "red");
        } else if (player1 < player2) {
            p1Score = setColorOnly(String.valueOf(player1), "red");
            p2Score = setColorOnly(String.valueOf(player2), "green");
        }

        String p1Name = "플레이어";
        String p2Name = "인공지능";

        if (GAME_LEVEL == LEVEL_MULTI) {
            p1Name = "플레이어 1 (X)";
            p2Name = "플레이어 2 (O)";
        }

        TableOfScore.setText("<html><table width='240'><tr><th colspan='2'><p style='text-align:center;'>점수</p></th></tr>"
                + "<tr><td><p style='text-align:center;'><b>" + p1Name + "</b></p></td><td><p style='text-align:center;'>" + p1Score + "</p></td></tr>"
                + "<tr><td><p style='text-align:center;'><b>" + p2Name + "</b></p></td><td><p style='text-align:center;'>" + p2Score + "</p></td></tr></html>");
    }

    /**
     * 결과 계산
     *
     * @param Player1Turn 플레이어 1 턴 여부
     * @return 게임 종료 여부 (true: 계속 / false: 종료)
     */
    private boolean getResult(boolean Player1Turn) {
        if (((board[0].getText().equals(board[3].getText())) && (board[0].getText().equals(board[6].getText())) && (!board[0].getText().equals("")))
                || ((board[1].getText().equals(board[4].getText())) && (board[1].getText().equals(board[7].getText())) && (!board[1].getText().equals("")))
                || ((board[2].getText().equals(board[5].getText())) && (board[2].getText().equals(board[8].getText())) && (!board[2].getText().equals("")))
                || ((board[0].getText().equals(board[1].getText())) && (board[0].getText().equals(board[2].getText())) && (!board[0].getText().equals("")))
                || ((board[3].getText().equals(board[4].getText())) && (board[3].getText().equals(board[5].getText())) && (!board[3].getText().equals("")))
                || ((board[6].getText().equals(board[7].getText())) && (board[6].getText().equals(board[8].getText())) && (!board[6].getText().equals("")))
                || ((board[0].getText().equals(board[4].getText())) && (board[0].getText().equals(board[8].getText())) && (!board[0].getText().equals("")))
                || ((board[2].getText().equals(board[4].getText())) && (board[2].getText().equals(board[6].getText())) && (!board[2].getText().equals("")))) {

            if (Player1Turn) {
                player1Score++;
                if (GAME_LEVEL == LEVEL_MULTI)
                    JOptionPane.showMessageDialog(null, setColor("플레이어 1 (X) 승리 !", "green"));
                else
                    JOptionPane.showMessageDialog(null, setColor("승리 !", "green"));
            } else {
                player2Score++;
                if (GAME_LEVEL == LEVEL_MULTI)
                    JOptionPane.showMessageDialog(null, setColor("플레이어 2 (O) 승리 !", "green"));
                else
                    JOptionPane.showMessageDialog(null, setColor("패배 !", "red"));
            }
            printScore(player1Score, player2Score);
            clear();
            return false;
        } else {
            if (!board[0].getText().equals("")
                    && !board[1].getText().equals("")
                    && !board[2].getText().equals("")
                    && !board[3].getText().equals("")
                    && !board[4].getText().equals("")
                    && !board[5].getText().equals("")
                    && !board[6].getText().equals("")
                    && !board[7].getText().equals("")
                    && !board[8].getText().equals("")) {
                JOptionPane.showMessageDialog(null, " 무승부 !");
                clear();
                return false;
            } else {
                randomSelect = true;
            }
        }
        return true;
    }

    /**
     * 현재 진행 중인 게임 초기화
     */
    private void clear() {
        for (int i = 0; i < 9; i++) {
            board[i].setText("");
        }
        if (GAME_LEVEL == LEVEL_SINGLE_EASY) {
            randomSelect = false;
        } else {
            for (int i = 0; i < 8; i++) {
                playBoard[i] = 0;
            }

            centerFirst = false;
            cornerFirst = false;
            sideFirst = false;
            sideAfterCorner = false;
            cornerAfterSide = false;
            cornerAfterCenter = false;
            sideAfterSide = false;
            corner = false;
            counter = 0;
        }
    }

    /**
     * 점수 초기화
     */
    private void resetScore() {
        clear();
        printScore(player1Score = 0, player2Score = 0);
    }

    /**
     * 멀티 플레이
     *
     * @param index 선택 버튼 번호
     */
    private void multiPlay(int index) {
        if (board[index].getText().equals("")) {
            if (player1Trun) {
                board[index].setText(setColor("X", "green"));
                getResult(player1Trun);
                player1Trun = false;
            } else {
                board[index].setText(setColor("O", "blue"));
                getResult(player1Trun);
                player1Trun = true;
            }
        }
    }

    /**
     * 싱글 플레이 - 쉬움 (무작위로 선택)
     *
     * @param index 선택 버튼 번호
     */
    private void singlePlayEasy(int index) {
        board[index].setText(setColor("X", "green"));
        getResult(true);

        if (randomSelect) {
            while (true) {
                int i = random.nextInt(9);
                if (board[i].getText().equals("")) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {
                    }

                    board[i].setText(setColor("O", "blue"));
                    getResult(false);
                    break;
                }
            }
        }
    }

    /**
     * 싱글 플레이 - 보통 플레이어
     *
     * @param index 선택 버튼 번호
     */
    private void singlePlayMediumPlayer(int index) {
        board[index].setText(setColor("X", "green"));

        // 중앙 선점 여부 체크
        if (index == 4) {
            if (counter == 0) {
                centerFirst = true;
            }
        } else {
            corner = index == 0 || index == 2 || index == 6 || index == 8;
        }
        fillArray(index, 1);
        counter++;
    }

    /**
     * 싱글 플레이 - 보통 인공지능
     */
    private void singlePlayMediumAI() {
        // 플레이어가 중앙을 선점했다면
        if (centerFirst) {
            selectAICorner();
            centerFirst = false;

            // 플레이어가 중앙을 선점하지 않고 중앙을 선택했다면
        } else if (board[4].getText().equals("")) {
            selectAIHandler(4);

            // 플레이어가 모서리를 선택했다면
        } else if (corner) {
            selectAISide();

            // 선택 안한 모서리가 있는지
        } else {
            selectAICorner();
        }
    }

    /**
     * 싱글 플레이 - 어려움 플레이어
     *
     * @param index 선택 버튼 번호
     */
    private void singlePlayHardPlayer(int index) {
        board[index].setText(setColor("X", "green"));

        // 중앙 선점 여부 체크
        if (index == 4) {
            if (counter == 0) {
                centerFirst = true;
            }
            // 모서리 선택 여부
        } else if (index == 0 || index == 2 || index == 6 || index == 8) {
            // 모서리 선점 여부
            if (counter == 0) {
                cornerFirst = true;
            } else {
                // 중앙을 선점한 뒤 모서리를 선택 했다면
                if (centerFirst) {
                    cornerAfterCenter = true;
                    centerFirst = false;
                }
                corner = true;
                cornerFirst = false;
                if (sideFirst) {
                    cornerAfterSide = true;
                }
                sideFirst = false;
            }
        } else {
            // 변 선점 여부
            if (counter == 0) {
                sideFirst = true;
                // 그 외
            } else {
                if (cornerFirst) {
                    sideAfterCorner = true;
                    cornerFirst = false;
                } else if (sideFirst) {
                    sideAfterSide = true;
                    sideFirst = false;
                } else if (centerFirst) {
                    cornerAfterCenter = false;
                    centerFirst = false;
                } else {
                    corner = false;
                }
            }
        }
        fillArray(index, 1);
        counter++;
    }

    /**
     * 싱글 플레이 - 어려움 인공지능
     */
    private void singlePlayHardAI() {
        // 플레이어가 중앙을 선점 했다면
        if (centerFirst) {
            selectAICorner();
            // 플레이어가 모서리를 선점 했다면
        } else if (cornerFirst) {
            selectAIHandler(4);
            // 플레이어가 변을 선점 했다면
        } else if (sideFirst) {
            selectAIHandler(4);
            // 플레이어가 변을 선택 후 변을 선택했다면
        } else if (sideAfterSide) {
            int index = getBestCorner();
            selectAIHandler(index);
            corner = false;
            sideAfterSide = false;
            // 플레이어가 모서리를 선택 후 중앙을 선택했다면
        } else if (cornerAfterCenter) {
            selectAICorner();
            cornerAfterCenter = false;

            // 인공지능이 승리 할 수 있는 칸이 있다면
        } else if (AICanWin() != -1) {
            CanWinHandler(AICanWin());

            // 플레이어가 승리 할 수 있는 칸이 있다면
        } else if (playerCanWin() != -1) {
            CanWinHandler(playerCanWin());

            // 플레이어가 모서리를 선택 후 변을 선택 했다면
        } else if (cornerAfterSide) {
            int index = getBestSide();
            selectAIHandler(index);
            corner = false;
            cornerAfterSide = false;

            // 플레이어가 변을 선택 후 모서리를 선택 했다면
        } else if (sideAfterCorner) {
            int index = getBestSide();
            selectAIHandler(index);
            corner = false;
            sideAfterCorner = false;

            // 플레이어가 모서리를 선택 했다면
        } else if (corner) {
            selectAISide();
        } else {
            if (counter <= 4) {
                int index = getBestCorner();
                selectAIHandler(index);
                corner = false;
            } else {
                selectAICorner();
            }
        }
    }

    /**
     * 보드 선택 값에 따른 계산을 편하게 하기 위해 미리 배열로 정리
     *
     * @param index 버튼 번호
     * @param num   값
     */
    private void fillArray(int index, int num) {
        switch (index) {
            case 0 -> {
                playBoard[0] += num;
                playBoard[3] += num;
                playBoard[7] += num;
            }
            case 1 -> {
                playBoard[0] += num;
                playBoard[4] += num;
            }
            case 2 -> {
                playBoard[0] += num;
                playBoard[5] += num;
                playBoard[6] += num;
            }
            case 3 -> {
                playBoard[1] += num;
                playBoard[3] += num;
            }
            case 4 -> {
                playBoard[1] += num;
                playBoard[4] += num;
                playBoard[6] += num;
                playBoard[7] += num;
            }
            case 5 -> {
                playBoard[1] += num;
                playBoard[5] += num;
            }
            case 6 -> {
                playBoard[2] += num;
                playBoard[3] += num;
                playBoard[6] += num;
            }
            case 7 -> {
                playBoard[2] += num;
                playBoard[4] += num;
            }
            case 8 -> {
                playBoard[2] += num;
                playBoard[5] += num;
                playBoard[7] += num;
            }
        }
    }

    /**
     * 인공지능 선택 핸들러
     *
     * @param index 인공지능이 선택한 버튼 번호
     */
    private void selectAIHandler(int index) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
        }

        board[index].setText(setColor("O", "blue"));
        fillArray(index, -1);
        counter++;
        corner = false;
    }

    /**
     * 인공지능 모서리 선택
     */
    private void selectAICorner() {
        if (board[0].getText().equals("")) {
            selectAIHandler(0);
        } else if (board[2].getText().equals("")) {
            selectAIHandler(2);
        } else if (board[6].getText().equals("")) {
            selectAIHandler(6);
        } else if (board[8].getText().equals("")) {
            selectAIHandler(8);
        }
    }

    /**
     * 인공지능 변 선택
     */
    private void selectAISide() {
        if (board[1].getText().equals("")) {
            selectAIHandler(1);
        } else if (board[3].getText().equals("")) {
            selectAIHandler(3);
        } else if (board[5].getText().equals("")) {
            selectAIHandler(5);
        } else if (board[7].getText().equals("")) {
            selectAIHandler(7);
        }
    }

    /**
     * 플레이어가 2개 이상 연결한 OX 체크
     *
     * @return 버튼 번호
     */
    private int playerCanWin() {
        int index = -1;
        for (int i = 0; i < playBoard.length; i++) {
            index = (playBoard[i] == 2) ? i : index;
        }
        return index;
    }

    /**
     * AI가 2개 이상 연결한 OX 체크
     *
     * @return 버튼 번호
     */
    private int AICanWin() {
        int index = -1;
        for (int i = 0; i < playBoard.length; i++) {
            index = (playBoard[i] == -2) ? i : index;
        }
        return index;
    }

    /**
     * 우승 가능한 경우 처리
     *
     * @param indexRowNum 버튼 번호 배열
     */
    private void CanWinHandler(int indexRowNum) {
        int[] indexRow = getIndexRow(indexRowNum);
        int index = getEmptyIndex(indexRow);
        selectAIHandler(index);
        corner = false;
        cornerAfterCenter = false;
        sideAfterSide = false;
        sideAfterCorner = false;
        cornerAfterSide = false;
    }

    /**
     * 입력 받은 버튼 번호에 따른 선택해야하는 버튼 목록 가져오기
     *
     * @param index 버튼 번호
     * @return 버튼 번호 배열
     */
    private static int[] getIndexRow(int index) {
        return switch (index) {
            case 0 -> new int[]{0, 1, 2};
            case 1 -> new int[]{3, 4, 5};
            case 2 -> new int[]{6, 7, 8};
            case 3 -> new int[]{0, 3, 6};
            case 4 -> new int[]{1, 4, 7};
            case 5 -> new int[]{2, 5, 8};
            case 6 -> new int[]{2, 4, 6};
            case 7 -> new int[]{0, 4, 8};
            default -> new int[]{};
        };
    }

    /**
     * 빈 곳 체크
     *
     * @param array 버튼 배열
     * @return 빈 보드 버튼 번호
     */
    private int getEmptyIndex(int[] array) {
        int index = -1;
        for (int i = 0; i < 3; i++)
            if (board[array[i]].getText().equals("")) {
                index = array[i];
            }

        return index;
    }

    /**
     * 가장 좋은 변 선택
     *
     * @return 버튼 번호
     */
    private int getBestSide() {
        int one = playBoard[0] + playBoard[4];
        int two = playBoard[2] + playBoard[4];
        int three = playBoard[1] + playBoard[3];
        int four = playBoard[1] + playBoard[5];

        if ((one <= four) && (one <= two) && (one <= three)) {
            return 1;
        } else if ((two <= three) && (two <= one) && (two <= four)) {
            return 7;
        } else if ((three <= two) && (three <= one) && (three <= four)) {
            return 3;
        } else {
            return 5;
        }
    }

    /**
     * 가장 좋은 모서리 선택
     *
     * @return 버튼 번호
     */
    private int getBestCorner() {
        int one = playBoard[0] + playBoard[3];
        int two = playBoard[2] + playBoard[3];
        int three = playBoard[0] + playBoard[5];
        int four = playBoard[2] + playBoard[5];

        if ((one >= two) && (one >= three) && (one >= four)) {
            return 0;
        } else if ((two >= one) && (two >= three) && (two >= four)) {
            return 6;
        } else if ((three >= one) && (three >= two) && (three >= four)) {
            return 2;
        } else {
            return 8;
        }
    }
}
