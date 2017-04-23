package org.itst.dropdown;

/**
 * Created by Administrator on 2015/5/28.
 */
public class DropdownItemObject {

    public int id;
    public String text;
    public String suffix;

    public DropdownItemObject(int id,String text,int suffix) {
        this.text = text;
        this.id = id;
        this.suffix="("+suffix+")";
    }
    public DropdownItemObject(int id,String text) {
        this.text = text;
        this.id = id;
        this.suffix="";
    }
    

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
