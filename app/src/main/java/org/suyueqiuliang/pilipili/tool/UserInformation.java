package org.suyueqiuliang.pilipili.tool;

public class UserInformation{
    public UserInformation(String mid, String name, String sign, int coins, String face, int sex, int level, boolean vip,String nickname_color) {
        this.mid = mid;
        this.name = name;
        this.sign = sign;
        this.coins = coins;
        this.face = face;
        this.sex = sex;
        this.level = level;
        this.vip = vip;
        this.nickname_color = nickname_color;
    }
    //mid，用户名，签名，头像url，性别
    public String mid,name,sign,face,nickname_color;
    //硬币，性别（0保密，1男，2女），等级
    public int coins,sex,level;
    public boolean vip;
}