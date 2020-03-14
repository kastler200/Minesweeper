import java.awt.BorderLayout;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.io.File;
import javax.swing.ImageIcon;
import javax.sound.sampled.Clip;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;



public class Minesweeper extends JPanel implements ActionListener, MouseListener
{
	private static final long serialVersionUID = -5434549872444497651L;
	static int easyHandW;
	static int easyMines;
	static int mediumHandW;
	static int mediumMines;
	static int expertH;
	static int expertW;
	static int expertMines;
	static int absurdH;
	static int absurdW;
	static int absurdMines;
	int NUMROWS;
	int NUMCOLS;
	int NUMMINES;
	int explodedX;
	int explodedY;
	int totalWins;
	Clip mainClip;
	ImageIcon tileUp;
	ImageIcon tilePressed;
	ImageIcon tileOver;
	ImageIcon tileExplodedMine;
	ImageIcon tileMine;
	ImageIcon tileHovered;
	ImageIcon tileFlag;
	ImageIcon winImg;
	ImageIcon[] tileNum;
	File scoreFile;
	static JButton reset;
	static JButton resetWins;
	static JButton playMusic;
	boolean musicOn;
	String[] difficulties;
	JComboBox<String> diffButton;
	JLabel scoreBoard;
	JPanel topPanel;
	JPanel botPanel;
	JPanel GUI;
	ArrayList<ArrayList<JButton>> buttons;
	JFrame frame;
	boolean gameLost;
	boolean winClicked;
	String scoreStored;
	boolean[][] wasPressed;
	int[] mineList;
	int[][] cellNums;
	boolean[][] mineField;
	int[][] adjacentMineVals;

	static {
		easyHandW = 9;
		easyMines = 10;
		mediumHandW = 16;
		mediumMines = 40;
		expertH = 16;
		expertW = 30;
		expertMines = 99;
		absurdH = 35;
		absurdW = 45;
		absurdMines = 500;
		reset = new JButton("Reset");
		resetWins = new JButton("Set to 0");
		playMusic = new JButton("Tunes");
	}

	public Minesweeper() throws IOException {
		NUMROWS = easyHandW;
		NUMCOLS = easyHandW;
		NUMMINES = easyMines;
		explodedX = -1;
		explodedY = -1;
		mainClip = loadClip("lib/AmbientLoop.wav");
		tileUp = new ImageIcon(ImageIO.read(new File("lib/Cell.png")));
		tilePressed = new ImageIcon(ImageIO.read(new File("lib/CellDown.png")));
		tileOver = new ImageIcon(ImageIO.read(new File("lib/CellOver.png")));
		tileExplodedMine = new ImageIcon(ImageIO.read(new File("lib/ExplodedMineCell.png")));
		tileMine = new ImageIcon(ImageIO.read(new File("lib/Mine.png")));
		tileHovered = new ImageIcon(ImageIO.read(new File("lib/CellOver.png")));
		tileFlag = new ImageIcon(ImageIO.read(new File("lib/flag.png")));
		winImg = new ImageIcon(ImageIO.read(new File("lib/winImgResized.png")));
		tileNum = new ImageIcon[9];
		scoreFile = new File("lib/ScoreInfo.txt");
		musicOn = false;
		difficulties = new String[] { "Easy", "Medium", "Expert", "Absurd!" };
		diffButton = new JComboBox<String>(difficulties);
		scoreBoard = new JLabel();
		topPanel = new JPanel();
		botPanel = new JPanel();
		buttons = new ArrayList<ArrayList<JButton>>();
		frame = buildFrame();
		gameLost = false;
		winClicked = false;
		wasPressed = new boolean[NUMROWS][NUMCOLS];
		mineList = GameBoard.setMines(NUMMINES, NUMROWS, NUMCOLS);
		cellNums = GameBoard.setCellNum(NUMROWS, NUMCOLS);
		mineField = GameBoard.getMineField(mineList, cellNums);
		adjacentMineVals = GameBoard.getAdjacentMineVals(cellNums, mineField);
		setButtonGrid();
		GUI.setOpaque(false);
		for (int k = 0; k < tileNum.length; ++k) {
			tileNum[k] = new ImageIcon(ImageIO.read(new File("lib/Cell" + k + ".png")));
		}
		reset.addActionListener(this);
		diffButton.addActionListener(this);
		resetWins.addActionListener(this);
		playMusic.addActionListener(this);
		topPanel.add(diffButton, 0.0f);
		topPanel.add(reset, 0.5f);
		botPanel.add(playMusic, 0.0f);
		botPanel.add(resetWins, 0.5f);
		setFrameLocation();
		manageScore();
		frame.add(topPanel, "North");
		frame.setResizable(false);
		frame.pack();
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			for (int k = 0; k < buttons.get(0).size(); ++k) {
				for (int l = 0; l < buttons.get(k).size(); ++l) {
					if (e.getSource() == buttons.get(k).get(l)) {
						if (buttons.get(k).get(l).getIcon() == tileFlag) {
							buttons.get(k).get(l).setIcon(tileUp);
						}else if (buttons.get(k).get(l).getIcon() == tileUp){
							buttons.get(k).get(l).setIcon(tileFlag);
						}
					}
				}
			}		
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource().equals(resetWins)) {
			totalWins = 0;
			try {
				updateScore();
			}
			catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		else if (e.getSource().equals(playMusic)) {
			if (!musicOn) {
				musicOn = true;
				playClip(mainClip);
				mainClip.loop(-1);
			}
			else {
				musicOn = false;
				stopClip(mainClip);
			}
		}
		else if (e.getSource().equals(diffButton)) {
			if (diffButton.getSelectedItem() == "Easy") {
				NUMROWS = easyHandW;
				NUMCOLS = easyHandW;
				NUMMINES = easyMines;
			}
			else if (diffButton.getSelectedItem() == "Medium") {
				NUMROWS = mediumHandW;
				NUMCOLS = mediumHandW;
				NUMMINES = mediumMines;
			}
			else if (diffButton.getSelectedItem() == "Expert") {
				NUMROWS = expertH;
				NUMCOLS = expertW;
				NUMMINES = expertMines;
			}
			else if (diffButton.getSelectedItem() == "Absurd!") {
				NUMROWS = absurdH;
				NUMCOLS = absurdW;
				NUMMINES = absurdMines;
			}
		}
		else if (e.getSource().equals(reset)) {
			winClicked = false;
			frame.remove(GUI);
			setButtonGrid();
			if (NUMROWS == absurdH) {
				JOptionPane.showMessageDialog(frame, "Jesus....Good luck");
			}
			for (int i = 0; i < NUMROWS; ++i) {
				for (int j = 0; j < NUMCOLS; ++j) {
					gameLost = false;
					explodedX = -1;
					explodedY = -1;
					buttons.equals(new ArrayList<>());
					mineList = GameBoard.setMines(NUMMINES, NUMROWS, NUMCOLS);
					cellNums = GameBoard.setCellNum(NUMROWS, NUMCOLS);
					mineField = GameBoard.getMineField(mineList, cellNums);
					adjacentMineVals = GameBoard.getAdjacentMineVals(cellNums, mineField);
					buttons.get(i).get(j).setIcon(tileUp);
					buttons.get(i).get(j).setPressedIcon(tilePressed);
				}
			}
		}
		else {
			for (int i = 0; i < buttons.get(0).size(); ++i) {
				for (int j = 0; j < buttons.get(i).size(); ++j) {
					if (e.getSource().equals(buttons.get(i).get(j))) {
						if (!mineField[i][j]) {
							buttons.get(i).get(j).setIcon(tileNum[adjacentMineVals[i][j]]);
							if (adjacentMineVals[i][j] == 0) {
								clearZeros(j, i);
							}
							try {
								checkWin();
							}
							catch (IOException e3) {
								e3.printStackTrace();
							}
						}
						else if (mineField[i][j] && !winClicked) {
							for (int k = 0; k < buttons.get(0).size(); ++k) {
								for (int l = 0; l < buttons.get(k).size(); ++l) {
									if (k != explodedY || l != explodedX) {
										if (mineField[k][l]) {
											buttons.get(k).get(l).setIcon(tileMine);
										}
										else {
											buttons.get(k).get(l).setIcon(tileNum[adjacentMineVals[k][l]]);
										}
									}
									buttons.get(k).get(l).setPressedIcon(null);
								}
							}
							if (explodedX == -1) {
								explodedX = j;
								explodedY = i;
								buttons.get(i).get(j).setIcon(tileExplodedMine);
							}
							lostGame();
						}
						buttons.get(i).get(j).setPressedIcon(null);
					}
				}
			}
		}
	}

	public void setButtonGrid() {
		GUI = new JPanel(new GridLayout(NUMROWS, NUMCOLS));
		buttons = new ArrayList<ArrayList<JButton>>();
		for (int q = 0; q < NUMROWS; ++q) {
			for (int w = 0; w < NUMCOLS; ++w) {
				buttons.add(new ArrayList<JButton>());
				buttons.get(q).add(new JButton());
			}
		}
		for (int i = 0; i < NUMROWS; ++i) {
			for (int j = 0; j < NUMCOLS; ++j) {
				buttons.get(i).get(j).setIcon(tileUp);
				buttons.get(i).get(j).setBorder(null);
				buttons.get(i).get(j).addActionListener(this);
				buttons.get(i).get(j).addMouseListener(this);
				buttons.get(i).get(j).setPressedIcon(tilePressed);
				GUI.add(buttons.get(i).get(j));
			}
		}
		frame.add(GUI, "Center");
		frame.pack();
		setFrameLocation();
	}

	public void clearZeros(int buttonX, int buttonY) {
		final boolean[][] counted = new boolean[NUMROWS][NUMCOLS];
		int areAdjacentZeros = 1;
		final ArrayList<Integer> toCheckX = new ArrayList<Integer>();
		final ArrayList<Integer> toCheckY = new ArrayList<Integer>();
		int coordIndex = 0;
		if (adjacentMineVals[buttonY][buttonX] != 0) {
			return;
		}
		while (areAdjacentZeros > 0) {
			for (int dy = -1; dy <= 1; ++dy) {
				for (int dx = -1; dx <= 1; ++dx) {
					if (buttonY + dy >= 0 && buttonY + dy < buttons.get(buttonY).size() && buttonX + dx >= 0 && buttonX + dx < buttons.get(buttonX).size() && !counted[buttonY + dy][buttonX + dx]) {
						if (adjacentMineVals[buttonY + dy][buttonX + dx] == 0) {
							++areAdjacentZeros;
							toCheckX.add(buttonX + dx);
							toCheckY.add(buttonY + dy);
							counted[buttonY + dy][buttonX + dx] = true;
							buttons.get(buttonY + dy).get(buttonX + dx).setIcon(tileNum[adjacentMineVals[buttonY + dy][buttonX + dx]]);
							buttons.get(buttonY + dy).get(buttonX + dx).setPressedIcon(null);
						}
						else {
							buttons.get(buttonY + dy).get(buttonX + dx).setIcon(tileNum[adjacentMineVals[buttonY + dy][buttonX + dx]]);
							buttons.get(buttonY + dy).get(buttonX + dx).setPressedIcon(null);
						}
					}
				}
			}
			counted[buttonY][buttonX] = true;
			if (coordIndex < toCheckX.size()) {
				buttonX = toCheckX.get(coordIndex);
				buttonY = toCheckY.get(coordIndex);
			}
			++coordIndex;
			--areAdjacentZeros;
		}
	}

	//triggering too early
	public void checkWin() throws IOException {
		if (!gameLost) {
			int counts = 0;
			final int req = NUMROWS * NUMCOLS - NUMMINES;
			for (int i = 0; i < buttons.get(0).size(); ++i) {
				for (int j = 0; j < buttons.get(i).size(); ++j) {
					if (buttons.get(i).get(j).getIcon() != tileUp) {
						++counts;
					}
				}
			}
			if (counts >= req && !winClicked) {
				for (int x = 0; x < buttons.get(0).size(); ++x) {
					for (int z = 0; z < buttons.get(x).size(); ++z) {
						if (mineField[x][z]) {
							buttons.get(x).get(z).setIcon(tileMine);
						}
					}
				}
				winClicked = true;
				++totalWins;
				updateScore();
				JOptionPane.showMessageDialog(frame, "You Win!", "Noice", 1, winImg);
			}
		}
	}

	public void lostGame() {
		if (!gameLost) {
			if (NUMROWS == absurdH) {
				JOptionPane.showMessageDialog(frame, "Yeah I don't blame you...Good try anyway");
			}
			else {
				JOptionPane.showMessageDialog(frame, "You idiot! Why did you chose that one? You gotta play another round to make up for it", "RIP", 2, null);
			}
			gameLost = true;
		}
	}

	public void manageScore() throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(scoreFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			scoreStored = line.substring("TOTAL SCORE = ".length());
			if (scoreStored.matches("[0-9]+")) {
				totalWins = Integer.parseInt(scoreStored);
			}
		}
		reader.close();
		scoreBoard.setText("Wins = " + scoreStored);
		botPanel.add(scoreBoard, 1.0f);
		frame.add(botPanel, "South");
	}

	public void updateScore() throws IOException {
		String updatedScore = "";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile));
		updatedScore = String.valueOf(updatedScore) + totalWins;
		writer.write("TOTAL SCORE = " + updatedScore);
		writer.close();
		scoreBoard.setText("Wins = " + updatedScore);
	}

	public Clip loadClip(final String filename) {
		Clip clip = null;
		final File clipFile = new File(filename);
		try {
			final AudioInputStream audioIn = AudioSystem.getAudioInputStream(clipFile);
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		}
		catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		catch (IOException e2) {
			e2.printStackTrace();
		}
		catch (LineUnavailableException e3) {
			e3.printStackTrace();
		}
		return clip;
	}

	public void playClip(final Clip clip) {
		clip.setFramePosition(0);
		clip.start();
	}

	public void stopClip(final Clip clip) {
		if (clip.isRunning()) {
			clip.stop();
		}
	}

	public static JFrame buildFrame() {
		final JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
		return frame;
	}

	public void setFrameLocation() {
		frame.setLocationRelativeTo(null);
	}

	public static void main(final String[] args) throws IOException {
		new Minesweeper();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
