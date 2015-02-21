/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import javax.crypto.Cipher;

/**
 *
 * @author Mattia
 */
public interface DEKManager {
    public Cipher getEncrypter();
    public Cipher getDecrypter();
    public Cipher getSecondaryDecrypter();
}
