package org.suyueqiuliang.pilipili.tool;

public class LoginKey{
    String hash,RSAPublicKey;
    public LoginKey(String hash,String RSAPublicKey){
        this.hash = hash;
        this.RSAPublicKey = RSAPublicKey;
    }
}