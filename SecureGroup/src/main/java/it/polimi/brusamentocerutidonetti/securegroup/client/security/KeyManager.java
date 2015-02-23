/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageSender;
import it.polimi.brusamentocerutidonetti.securegroup.client.gui.Logger;
import it.polimi.brusamentocerutidonetti.securegroup.common.Message;
import it.polimi.brusamentocerutidonetti.securegroup.common.Parameters;
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
    
    private static final String SYMM_ALGORITHM = Parameters.SYMM_ALGORITHM;
    private static final String ASYMM_ALGORITHM = Parameters.ASYMM_ALGORITHM;
    private Key[] keks;
    private Key dek;
    private Key privateKey, publicKey;
    private Logger logger;
    
    private Cipher decrypter, secondaryDecrypter;
    private Cipher encrypter;

    public KeyManager(Logger logger) {
        this.logger = logger;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ASYMM_ALGORITHM);
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            privateKey = kp.getPrivate();
            publicKey = kp.getPublic();
        } catch (NoSuchAlgorithmException e) {
            logger.error(getClass() + ": Constructor error.");
        }
    }
    


    @Override
    public synchronized void confirmUpdate() {
        try {
            secondaryDecrypter = decrypter;
            decrypter = Cipher.getInstance(SYMM_ALGORITHM);
            decrypter.init(Cipher.DECRYPT_MODE, dek);
            encrypter = Cipher.getInstance(SYMM_ALGORITHM);
            encrypter.init(Cipher.ENCRYPT_MODE, dek);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            logger.error(getClass() + ": Error in changing the keys.");
        }
        
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
                    logger.error(getClass() + ": KEK " + i + " NOT updated");
                }
            }

            cipher.init(Cipher.DECRYPT_MODE, this.dek);
            try {
                this.dek = (Key) newDek.getObject(cipher);
            } catch (ClassNotFoundException | IllegalBlockSizeException
                            | BadPaddingException | IOException e) {
                logger.error(getClass() + ": DEK NOT updated");
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                    | InvalidKeyException e) {
            logger.error(getClass() + ": updateOnJoin error.");
        }
    }

    @Override
    public synchronized void updateOnLeave(SealedObject[] newKeks, SealedObject[] newDeks) {
        if (this.keks == null) {
            return;
        }
        try {
            Cipher kekCipher = Cipher.getInstance(SYMM_ALGORITHM);
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
            Cipher dekCipher = Cipher.getInstance(SYMM_ALGORITHM);
            dekCipher.init(Cipher.DECRYPT_MODE, this.dek);
            for (int i = 0; i < keks.length; i++) {
                kekCipher.init(Cipher.DECRYPT_MODE, this.keks[i]);
                try {
                    SealedObject firstStep = (SealedObject) newKeks[i].getObject(dekCipher);
                    this.keks[i] = (Key)firstStep.getObject(kekCipher);
                } catch (ClassNotFoundException | IllegalBlockSizeException
                            | BadPaddingException | IOException e) {
                    logger.error(getClass() + ": KEK " + i + " NOT updated");
                }
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                        | InvalidKeyException e) {
                logger.error(getClass() + ": updateOnLeav error.");
        }
        
    }

    @Override
    public synchronized void initialise(SealedObject[] newKeks, SealedObject newDek) {
        try {
            this.keks = new Key[Parameters.FLAT_TABLE];
            this.keks = new Key[Parameters.FLAT_TABLE];
            Cipher cipher = Cipher.getInstance(ASYMM_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            for (int i = 0; i < keks.length; i++) {
                this.keks[i] = (Key) newKeks[i].getObject(cipher);
            }

            this.dek = (Key) newDek.getObject(cipher);
            decrypter = Cipher.getInstance(SYMM_ALGORITHM);
            decrypter.init(Cipher.DECRYPT_MODE, dek);
            secondaryDecrypter = decrypter;
            encrypter = Cipher.getInstance(SYMM_ALGORITHM);
            encrypter.init(Cipher.ENCRYPT_MODE, dek);
            logger.log(getClass() + ": Keys received.");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                        | InvalidKeyException | ClassNotFoundException
                        | IllegalBlockSizeException | BadPaddingException | IOException e) {
            logger.error(getClass() + ": Error in receiving the keys.");
        }
    }
    

    @Override
    public synchronized Cipher getEncrypter() {
       return encrypter;
    }

    @Override
    public synchronized Cipher getDecrypter() {
        return decrypter;
    }

    @Override
    public synchronized Cipher getSecondaryDecrypter() {
        return secondaryDecrypter;
    }

    
    
}
