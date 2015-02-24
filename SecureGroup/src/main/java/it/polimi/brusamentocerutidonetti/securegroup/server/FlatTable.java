/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import it.polimi.brusamentocerutidonetti.securegroup.common.Parameters;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

/**
 *
 * @author Mattia
 */
public class FlatTable {
    
    private static final String ALGORITHM = Parameters.SYMM_ALGORITHM;
    private static final String ASYMM_ALGORITHM = Parameters.ASYMM_ALGORITHM;
    private static final int BITS = Parameters.FLAT_TABLE;
    
    private Key[] zeros = new Key[BITS];
    private Key[] ones = new Key[BITS];
    private Key[] oldZeros = new Key[BITS];
    private Key[] oldOnes = new Key[BITS];
    private Key dek, oldDek;
    private KeyGenerator keygen;
    
    public FlatTable() {
        try {
            keygen = KeyGenerator.getInstance(ALGORITHM);
            keygen.init(new SecureRandom());
            dek = keygen.generateKey();
            for (int i = 0; i < BITS; i++) {
                ones[i] = keygen.generateKey();
                zeros[i] = keygen.generateKey();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Update the Keys when a member is joining.
     * @param memberID
     */
    public synchronized void updateKeys(int memberID){
        oldOnes = ones.clone();
        oldZeros = zeros.clone();
        oldDek = dek;
        int[] bits = getBits(memberID);
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == 1) {
                ones[i] = keygen.generateKey();
            } else {
                zeros[i] = keygen.generateKey();
            }
        }
        refreshDEK();
    }
    
    /**
     * Encrypt each new KEK with the old one and the DEK with the old one.
     * @param memberID
     * @return 
     */
    public synchronized SealedObject[] getEncryptedKeysJoin(int memberID) {
        SealedObject[] keys = new SealedObject[BITS+1];
        int[] bits = getBits(memberID);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            for (int i = 0; i < bits.length; i++) {
                if (bits[i] == 1) {
                    try {
                        cipher.init(Cipher.ENCRYPT_MODE, this.oldOnes[i]);
                        keys[i] = new SealedObject(ones[i], cipher);
                    } catch (InvalidKeyException | IOException | IllegalBlockSizeException ex) {
                        Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        cipher.init(Cipher.ENCRYPT_MODE, this.oldZeros[i]);
                        keys[i] = new SealedObject(zeros[i], cipher);
                    } catch (InvalidKeyException | IOException | IllegalBlockSizeException ex) {
                        Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                cipher.init(Cipher.ENCRYPT_MODE, this.oldDek);
                keys[BITS] = new SealedObject(dek, cipher);
            } catch (InvalidKeyException | IOException | IllegalBlockSizeException ex) {
                Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keys;
    
    }
    
    
    /**
     * Retrieve the Key Encryption Keys of a given member.
     * @param publicKey
     * @param memberID 
     * @return an array of Key.
     */
    public SealedObject[] getKeysNewMember(Key publicKey, int memberID) {
        SealedObject[] keys = new SealedObject[BITS+1];
        int[] bits = getBits(memberID);
        try {
            Cipher cipher = Cipher.getInstance(ASYMM_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            for (int i = 0; i < bits.length; i++) {
                if (bits[i] == 1) {
                    keys[i] = new SealedObject(ones[i], cipher);
                } else {
                    keys[i] = new SealedObject(zeros[i], cipher);
                }
            }
            keys[BITS] = new SealedObject(dek, cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keys;
    }
    
    /**
     * First step when leaving: encrypt the new DEK with other KEKs.
     * @param memberID
     * @return 
     */
    public SealedObject[] getDEKsAfterLeave(int memberID){
        SealedObject[] encDeks = new SealedObject[BITS];
        try {
            Key[] validKeys = getOtherKEKs(memberID);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            for(int i=0; i< BITS; i++){
                cipher.init(Cipher.ENCRYPT_MODE, validKeys[i]);
                encDeks[i] = new SealedObject(getDEK(), cipher);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encDeks;
    }
    
    /**
     * Retrieves the KEKs not known by a given member.
     * @param memberID
     * @return 
     */
    private Key[] getOtherKEKs(int memberID){
        Key[] keks = new Key[BITS];
        int[] bits = getBits(memberID);
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == 1) {
                keks[i] = zeros[i];
            } else {
                keks[i] = ones[i];
            }
        }
        return keks;
    }
    
    /**
     * Encrypt each KEK with the respective old KEK and then with the new DEK.
     * @param memberID
     * @return 
     */
    public SealedObject[] getKEKsLeave(int memberID){
        SealedObject[] keys = new SealedObject[BITS];
        int[] bits = getBits(memberID);
        try {
            Cipher kekcipher = Cipher.getInstance(ALGORITHM);
            Cipher dekcipher = Cipher.getInstance(ALGORITHM);
            dekcipher.init(ENCRYPT_MODE, this.dek);
            for (int i = 0; i < bits.length; i++) {
                if (bits[i] == 1) {
                    try {
                        kekcipher.init(Cipher.ENCRYPT_MODE, this.oldOnes[i]);
                        SealedObject firstStep = new SealedObject(ones[i], kekcipher);
                        keys[i] = new SealedObject(firstStep, dekcipher);
                    } catch (InvalidKeyException | IOException | IllegalBlockSizeException ex) {
                        Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        kekcipher.init(Cipher.ENCRYPT_MODE, this.oldZeros[i]);
                        SealedObject firstStep = new SealedObject(ones[i], kekcipher);
                        keys[i] = new SealedObject(firstStep, dekcipher);
                    } catch (InvalidKeyException | IOException | IllegalBlockSizeException ex) {
                        Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            Logger.getLogger(FlatTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keys;
    
    }
    
    
    /**
     * Generates a new DEK.
     * @return the generated DEK.
     */
    private Key refreshDEK() {
        dek = keygen.generateKey();
        return dek;
    }
    
    public Key getDEK() {
        return dek;
    }
    
    
    /**
     * Convert an integer to a binary array of the dimension of the flat table length.
     * @param ID the member ID.
     * @return an array with the converted bits.
     */
    private int[] getBits(int ID){
        String base2 = Integer.toBinaryString(ID);
        while (base2.length() < BITS) {
            base2 = "0" + base2;
        }
        char[] splitted = base2.toCharArray();
        int[] toReturn = new int[splitted.length];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = Character.getNumericValue(splitted[i]);
        }
        return toReturn;
    }
}
