/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import java.security.Key;
import javax.crypto.SealedObject;

/**
 *
 * @author Mattia
 */
public interface KeysManager {
    public void updateOnJoin(SealedObject[] newKeks, SealedObject newDek);
    public void updateOnLeave(SealedObject[] newKeks, SealedObject[] newDek);
    public void initialise(SealedObject[] newKeks, SealedObject newDek);
    public void confirmUpdate();
    public Key getPrivateKey();
    public Key getPublicKey();
}
