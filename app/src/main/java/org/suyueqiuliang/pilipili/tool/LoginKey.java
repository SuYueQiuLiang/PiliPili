package org.suyueqiuliang.pilipili.tool;

public class LoginKey{
    public String hash,RSAPublicKey;
    public LoginKey(String hash,String RSAPublicKey){
        this.hash = hash;
        this.RSAPublicKey = RSAPublicKey;
    }
}