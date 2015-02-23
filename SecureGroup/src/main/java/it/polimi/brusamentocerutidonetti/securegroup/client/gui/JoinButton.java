/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author Mattia
 */
public class JoinButton extends JButton implements ActionListener{
    
    private UserInterface ui;

    public JoinButton(String join, UserInterface ui) {
        super(join);
        this.ui = ui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(this.getText().equals("Join")){
            ui.requestJoin();
        }else{
            ui.requestLeave();
        }
    }
    
}
