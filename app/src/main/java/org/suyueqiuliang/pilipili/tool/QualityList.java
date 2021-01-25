package org.suyueqiuliang.pilipili.tool;

import java.util.ArrayList;

public class QualityList {
    public QualityList(ArrayList<String> qualityList ,ArrayList<Integer> qn) {
        this.qn = qn;
        this.qualityList = qualityList;
    }
    public ArrayList<Integer> qn;
    public ArrayList<String> qualityList;
}
