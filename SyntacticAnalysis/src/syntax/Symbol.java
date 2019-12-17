package syntax;

import lex.LexSetting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 终结符与非终结符
 */
public class Symbol {
    private String name;
    private int code;
    public static HashMap<String, Symbol> VnTable = new HashMap<>();//非终结符表
    public static HashMap<String, Symbol> VtTable = new HashMap<>();//终结符表
    Symbol(String name){
        this.name = name;
        this.setCode(name);
        if(this.code==-1){
            if(!VnTable.containsKey(name)){
                VnTable.put(name, this);
            }
        }
        else{
            if(!VtTable.containsKey(name)&&this.code!=103){
                VtTable.put(name, this);
            }
        }
//        System.out.println(this.name+" "+String.valueOf(this.code));
    }

    String getName(){
        return this.name;
    }

    int getCode(){
        return this.code;
    }

    public boolean isVt(){
        return this.code!=-1;
    }


    static Symbol getSymbolByName(String name){
        Symbol result = null;
        if(VnTable.containsKey(name)){
            result = VnTable.get(name);
        }
        if(VtTable.containsKey(name)){
            result = VtTable.get(name);
        }
        return result;
    }


    private void setCode(String name){
        LexSetting lex =LexSetting.getLexSetting();
        if(name.equals("ID")){
            this.code = 100;
        }
        else if(name.equals("DECIMAL")){
            this.code = 102;
        }
        else if(name.equals("INTEGER")){
            this.code = 101;
        }
        else if(name.equals("ε")){
            this.code = 103;//终态
        }
        else if(name.equals("$")){
            this.code = 104;
        }
        else{
            //判断运算符
            int catalog = lex.getCatalog_Op(name);
            if(catalog!=-1){
                this.code = catalog;
                return;
            }

            //判断保留字,如int if else
            catalog = lex.getCatalog_RW(name);
            if(catalog!=-1){
                this.code = catalog;
                return;
            }

            //判断分界符，如{} ;
            char c = name.charAt(0);
            if(lex.isDivider(c)){
                this.code = lex.getCatalog_Div(c);
                return;
            }

            this.code = -1;//代表是非终结符
        }

    }

    public boolean isEpsilon(){
        return this.code == 103;
    }

    public static Iterator<Symbol> getVnItr(){
        return VnTable.values().iterator();
    }
}
