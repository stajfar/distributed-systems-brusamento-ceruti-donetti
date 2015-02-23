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
public class SendButton extends JButton implements ActionListener{
    
    
    private UserInterface ui;
    
    public SendButton(String name, UserInterface ui){
        super(name);
        this.ui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ui.send();
    }
    
}
