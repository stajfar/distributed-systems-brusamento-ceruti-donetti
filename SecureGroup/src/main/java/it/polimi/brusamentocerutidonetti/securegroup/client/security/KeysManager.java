/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import java.security.Key;

/**
 *
 * @author Mattia
 */
public interface KeysManager {
    public void updateKEKs(Key[] newKEKs);
    public void updateDEK(Key DEK);
    public void confirmUpdate();
    public Key getPrivateKey();
    public Key getPublicKey();
}
