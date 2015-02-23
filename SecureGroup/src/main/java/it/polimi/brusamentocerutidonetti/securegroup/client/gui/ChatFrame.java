/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Mattia
 */
public class ChatFrame extends JFrame {
	private static final long serialVersionUID = 5288322019518493055L;
	private JPanel panesPanel = new JPanel();
	private JTextPane debugPane = new JTextPane();
	private JTextPane textPane = new JTextPane();
	private StyledDocument chat = textPane.getStyledDocument();
	private StyledDocument debug = debugPane.getStyledDocument();
	private JPanel inputPanel = new JPanel();
	private JTextField textField = new JTextField();
	private JLabel nickname = new JLabel("nickname");
	private static ChatFrame instance;

	public static ChatFrame get() {
		if (instance == null) {
			instance = new ChatFrame();
		}
		return instance;
	}

	private ChatFrame() {
		super("Secure Group Communication");
		this.setPreferredSize(new Dimension(1000, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.setLayout(new BorderLayout());

		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		textPane.setEditable(false);
		((DefaultCaret) textPane.getCaret())
				.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textPane.setBorder(BorderFactory.createCompoundBorder(null, padding));
		textPane.setPreferredSize(new Dimension(800, 400));
		debugPane.setEditable(false);
		((DefaultCaret) debugPane.getCaret())
				.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		debugPane.setBorder(BorderFactory.createCompoundBorder(null, padding));

		nickname.setBorder(BorderFactory.createCompoundBorder(null, padding));
		textField.addKeyListener((KeyListener) new SubmitListener());

		panesPanel.setLayout(new GridLayout(2, 0));
		panesPanel.add(new JScrollPane(textPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		panesPanel.add(new JScrollPane(debugPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		this.add(panesPanel, BorderLayout.CENTER);
		inputPanel.setLayout(new BorderLayout());
		inputPanel.setBorder(BorderFactory.createCompoundBorder(null, padding));
		inputPanel.add(nickname, BorderLayout.WEST);
		inputPanel.add(textField, BorderLayout.CENTER);
		this.add(inputPanel, BorderLayout.SOUTH);
		pack();
		textPane.setSize(new Dimension(this.getWidth(), this.getHeight() / 2));
		debugPane.setSize(new Dimension(this.getWidth(), this.getHeight() / 2));
		textField.requestFocusInWindow();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void append(String message) {
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.BLUE);
		StyleConstants.setBold(keyWord, true);
		try {
			chat.insertString(chat.getLength(), nickname.getText() + "> "
					+ message + "\n", keyWord);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void print(String message) {
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.BLACK);
		StyleConstants.setBold(keyWord, true);
		try {
			chat.insertString(chat.getLength(), message + "\n", keyWord);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void debug(String debugMessage) {
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.RED);
		StyleConstants.setBold(keyWord, true);
		try {
			debug.insertString(debug.getLength(), debugMessage + "\n", keyWord);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public String getMessage() throws InterruptedException {
		synchronized (textField) {
			if (this.textField.getText().equals("")) {
				textField.wait();
			}
			String text = this.textField.getText();
			textField.setText("");
			return text;
		}
	}

	public void setNickname(String nickname) {
		this.nickname.setText(nickname);
	}

	private class SubmitListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			// not implemented
		}

		@Override
		public void keyPressed(KeyEvent e) {
			synchronized (textField) {
				if (e.getKeyCode() == 10) {
					append(textField.getText());
					textField.notify();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// not implemented
		}

	}
}
