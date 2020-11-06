package org.suyueqiuliang.pilipili.tool;

public class LoginAccess{
    public LoginAccess(boolean access, UserInformation userInformation) {
        this.access = access;
        this.userInformation = userInformation;
    }
    public boolean access;
    public UserInformation userInformation;
}