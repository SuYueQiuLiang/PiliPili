package org.suyueqiuliang.pilipili.tool;

public class LevelWalletInfo{
    public LevelWalletInfo(int current_level, int current_min, int current_exp, int next_exp,int wallet) {
        this.current_level = current_level;
        this.current_min = current_min;
        this.current_exp = current_exp;
        this.next_exp = next_exp;
        this.wallet = wallet;
    }
    //当前等级，当前等级最低经验，当前经验，当前等级最高经验
    int current_level,current_min,current_exp,next_exp,wallet;
}