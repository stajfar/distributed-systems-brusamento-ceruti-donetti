/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import it.polimi.brusamentocerutidonetti.securegroup.client.gui.UserInterface;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

/**
 *
 * @author Mattia
 */
public class KeyManager implements DEKManager, KeysManager{
    
    public static final String SYMM_ALGORITHM = "AES";
    public static final String ASYMM_ALGORITHM = "RSA";
    private Key[] keks;
    private Key dek;
    private Key privateKey, publicKey;
    private UserInterface logger;

    public KeyManager(UserInterface logger) {
        this.logger = logger;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            privateKey = kp.getPrivate();
            publicKey = kp.getPublic();
        } catch (NoSuchAlgorithmException e) {
            logger.receiveMessage(getClass() + ": Constructor error.");
        }
    }
    
    @Override
    public Cipher getDEK() {
        return null;
    }


    @Override
    public void confirmUpdate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Key getPrivateKey() {
        return privateKey;
    }

    @Override
    public Key getPublicKey() {
        return publicKey;
    }

    @Override
    public synchronized void updateOnJoin(SealedObject[] newKeks, SealedObject newDek) {
        if (this.keks == null) {
            return;
        }
        try {
            Cipher cipher = Cipher.getInstance(SYMM_ALGORITHM);
            for (int i = 0; i < newKeks.length; i++) {
                cipher.init(Cipher.DECRYPT_MODE, this.keks[i]);
                try {
                        this.keks[i] = (Key) newKeks[i].getObject(cipher);
                } catch (ClassNotFoundException | IllegalBlockSizeException
                                | BadPaddingException | IOException e) {
                        logger.receiveMessage(getClass() + ": KEK " + i + " NOT updated");
                }
            }

            cipher.init(Cipher.DECRYPT_MODE, this.dek);
            try {
                this.dek = (Key) newDek.getObject(cipher);
            } catch (ClassNotFoundException | IllegalBlockSizeException
                            | BadPaddingException | IOException e) {
                logger.receiveMessage(getClass() + ": DEK NOT updated");
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                    | InvalidKeyException e) {
            logger.receiveMessage(getClass() + ": updateOnJoin error.");
        }
    }

    @Override
    public synchronized void updateOnLeave(SealedObject[] newKeks, SealedObject[] newDeks) {
        if (this.keks == null) {
            return;
        }
        try {
            Cipher kekCipher = Cipher.getInstance(SYMM_ALGORITHM);
            Cipher dekCipher = Cipher.getInstance(SYMM_ALGORITHM);
            dekCipher.init(Cipher.DECRYPT_MODE, this.dek);
            for (int i = 0; i < keks.length; i++) {
                kekCipher.init(Cipher.DECRYPT_MODE, this.keks[i]);
                try {
                    SealedObject firstStep = (SealedObject) newKeks[i].getObject(dekCipher);
                    this.keks[i] = (Key)firstStep.getObject(kekCipher);
                } catch (ClassNotFoundException | IllegalBlockSizeException
                            | BadPaddingException | IOException e) {
                    logger.receiveMessage(getClass() + ": KEK " + i + " NOT updated");
                }
            }

            for (int i = 0; i < keks.length; i++) {
                kekCipher.init(Cipher.DECRYPT_MODE, this.keks[i]);
                try {
                    this.dek = (Key) newDeks[i].getObject(kekCipher);
                    break;
                } catch (ClassNotFoundException | IllegalBlockSizeException
                                | BadPaddingException | IOException e) {
                        //I was only trying with the wrong key...
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                        | InvalidKeyException e) {
                logger.receiveMessage(getClass() + ": updateOnLeav error.");
        }
		
    }

    @Override
    public synchronized void initialise(SealedObject[] newKeks, SealedObject newDek) {
        try {
            this.keks = new Key[3];
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            for (int i = 0; i < keks.length; i++) {
                this.keks[i] = (Key) newKeks[i].getObject(cipher);
            }

            this.dek = (Key) newDek.getObject(cipher);
            logger.receiveMessage("Keys received.");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                        | InvalidKeyException | ClassNotFoundException
                        | IllegalBlockSizeException | BadPaddingException | IOException e) {
            logger.receiveMessage("Error in receiving the keys.");
        }
    }
    
}
