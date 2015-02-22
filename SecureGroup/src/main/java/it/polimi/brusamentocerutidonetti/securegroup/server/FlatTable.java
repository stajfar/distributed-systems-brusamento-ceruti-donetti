/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import it.polimi.brusamentocerutidonetti.securegroup.common.Parameters;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;

/**
 *
 * @author Mattia
 */
public class FlatTable {
    private static final String ALGORITHM = Parameters.SYMM_ALGORITHM;
    private static final int BITS = Parameters.FLAT_TABLE;
    private Key[] ones, zeros;
    private Key dek;
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
     * Update the Key Encryption Keys known by a given member.
     * @param memberID
     * @return 
     */
    public Key[] updateKEKs(int memberID){
        Key[] keks = new Key[BITS];
        int[] bits = getBits(memberID);
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == 1) {
                ones[i] = keygen.generateKey();
                keks[i] = ones[i];
            } else {
                zeros[i] = keygen.generateKey();
                keks[i] = zeros[i];
            }
        }
        return keks;
    }
    
    
    /**
     * Retrieve the Key Encryption Keys of a given member.
     * @param memberID 
     * @return an array of Key.
     */
    public Key[] getKEKs(int memberID) {
        Key[] keks = new Key[BITS];
        int[] bits = getBits(memberID);
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == 1) {
                keks[i] = ones[i];
            } else {
                keks[i] = zeros[i];
            }
        }
        return keks;
    }
    
    /**
     * Retrieves the KEKs not known by a given member.
     * @param memberID
     * @return 
     */
    public Key[] getOtherKEKs(int memberID){
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
     * Generates a new DEK.
     * @return the generated DEK.
     */
    public Key refreshDEK() {
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
