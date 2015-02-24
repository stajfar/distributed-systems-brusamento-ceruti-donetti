/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.gui;

import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageSender;
import it.polimi.brusamentocerutidonetti.securegroup.client.communication.ServerDispatcher;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
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
public class Chat extends JFrame implements UserInterface, Logger{
    private static final long serialVersionUID = 5288322019518493055L;
    private String username;
    private JPanel panesPanel = new JPanel();
    private JTextPane loggerPane = new JTextPane();
    private JTextPane textPane = new JTextPane();
    private StyledDocument chat = textPane.getStyledDocument();
    private StyledDocument logger = loggerPane.getStyledDocument();
    private JPanel inputPanel = new JPanel();
    private JTextField textField = new JTextField();
    private static Chat instance;
    private SendButton sendButton;
    private JoinButton joinButton;
    
    private MessageSender sender;
    private ServerDispatcher serverDispatcher;
    
    public static Chat get() {
        if (instance == null) {
            instance = new Chat();
        }
        return instance;
    }

    private Chat() {
        super("Secure Group Communication");
        username = "Unknown";

        try{
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            username = addr.getHostName();
        }catch (UnknownHostException ex){
            System.out.println("Hostname can not be resolved");
        }
        
        this.setPreferredSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        
        sendButton = new SendButton("Send", this);
        sendButton.setEnabled(false);
        sendButton.addActionListener(sendButton);
        
        joinButton = new JoinButton("Join", this);
        joinButton.addActionListener(joinButton);
        
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        textPane.setEditable(false);
        ((DefaultCaret) textPane.getCaret())
                        .setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textPane.setBorder(BorderFactory.createCompoundBorder(null, padding));
        textPane.setPreferredSize(new Dimension(600, 400));
        loggerPane.setEditable(false);
        ((DefaultCaret) loggerPane.getCaret())
                        .setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        loggerPane.setBorder(BorderFactory.createCompoundBorder(null, padding));

        textField.addKeyListener((KeyListener) new Chat.SendListener());

        panesPanel.setLayout(new GridLayout(0, 2));
        panesPanel.add(new JScrollPane(textPane,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        panesPanel.add(new JScrollPane(loggerPane,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.add(panesPanel, BorderLayout.CENTER);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createCompoundBorder(null, padding));
        inputPanel.add(joinButton, BorderLayout.WEST);
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        this.add(inputPanel, BorderLayout.SOUTH);
        pack();
        textPane.setSize(new Dimension(this.getWidth(), this.getHeight() / 2));
        loggerPane.setSize(new Dimension(this.getWidth(), this.getHeight() / 2));
        textField.requestFocusInWindow();
        setLocationRelativeTo(null);
        setVisible(false);
    }

    
    
    
    @Override
    public void receiveMessage(String msg) {
        this.print(msg);
    }

    
    private synchronized void print(String msg) {
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.BLACK);
        StyleConstants.setBold(keyWord, true);
        try {
            chat.insertString(chat.getLength(), msg + "\n", keyWord);
        } catch (BadLocationException e) {
                e.printStackTrace();
        }
    }

    @Override
    public synchronized void send() {
        String msg = textField.getText();
        if(msg.length()>0){
           msg = username + ": << " + msg + " >>";
           textField.setText(null);
           sender.sendMessage(msg, String.class);
        }
    }

    @Override
    public synchronized void error(String e) {
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.RED);
        StyleConstants.setBold(keyWord, true);
        try {
            logger.insertString(logger.getLength(), e + "\n", keyWord);
        } catch (BadLocationException ex) {
                ex.printStackTrace();
        }
    }

    @Override
    public synchronized void log(String l) {
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.BLUE);
        StyleConstants.setBold(keyWord, true);
        try {
            logger.insertString(logger.getLength(), l + "\n", keyWord);
        } catch (BadLocationException ex) {
                ex.printStackTrace();
        }
    }

    @Override
    public void joinAccepeted() {
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setCursor(Cursor.getDefaultCursor());
        sendButton.setEnabled(true);
        joinButton.setText("Leave");
        joinButton.setEnabled(true);
        try {
            validate();
        } catch (Exception e) {
            joinAccepeted();
        }
    }

    @Override
    public void requestJoin() {
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        joinButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            validate();
        } catch (Exception e) {
            requestJoin();
        }
        serverDispatcher.requestJoin();
    }
    
    
    @Override
    public void refusedJoin() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        joinButton.setEnabled(true);
        setCursor(Cursor.getDefaultCursor());
        try {
            validate();
        } catch (Exception e) {
            refusedJoin();
        }
    }

    @Override
    public void leaveAccepetd() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setCursor(Cursor.getDefaultCursor());
        sendButton.setEnabled(false);
        joinButton.setText("Join");
        joinButton.setEnabled(true);
        try {
            validate();
        } catch (Exception e) {
            leaveAccepetd();
        }
    }

    @Override
    public void lockForUpdate() {
        joinButton.setEnabled(false);
        sendButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            validate();
        } catch (Exception e) {
            lockForUpdate();
        }
    }

    @Override
    public void updateComplete() {
        joinButton.setEnabled(true);
        sendButton.setEnabled(true);
        setCursor(Cursor.getDefaultCursor());
        try {
            validate();
        } catch (Exception e) {
            updateComplete();
        }
    }

    @Override
    public void requestLeave() {
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        joinButton.setEnabled(false);
        sendButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            validate();
        } catch (Exception e) {
            requestLeave();
        }
        serverDispatcher.requestLeave();
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }

    public void setServerDispatcher(ServerDispatcher serverDispatcher) {
        this.serverDispatcher = serverDispatcher;
    }
    
    
    
    
    private class SendListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
                // not implemented
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if(sendButton.isEnabled()){
                if (e.getKeyCode() == 10) {
                    send();
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
                // not implemented
        }
    }


}
