/**
 * 此类负责设定保留字等，封装了各种判断字符与字符串的方法
 */
public class LexSetting {

    static private LexSetting lexSetting;

    private LexSetting(){}

    public static LexSetting getLexSetting(){
        if(lexSetting==null){
            lexSetting = new LexSetting();
            return lexSetting;
        }
        else{
            return lexSetting;
        }
    }

    /*
    保留字的编号为0-99
     */
    private String[] reserveWord = {
            "int", "char", "float", "double", "String", "boolean",
            "if", "else", "switch", "case", "do", "while", "break", "for",
            "private", "protected", "public", "static", "void",
            "try", "catch", "throw",
            "class", "return"
    };
    /*
    ID的编号是100
     */
    private String ID = "ID";
    /*
    常量的编号为101-120，其中整数为101，小数为102
     */
    private String[] constValue = {"Integer", "Float"};
    /*
    运算符的编号是121-160
     */
    private String[] operator = {
            "+", "-", "*", "/", "=", "+=", "-=", "*=", "/=",
            "==", ">=", "<=", ">", "<", "&", "|", "&&", "||", "!", "!="
    };

    /*
    分界符的编号是161-180
     */
    private char[] divider = {
            '\'', '"', ';', ':', '[', ']', '{', '}', '\\', '(', ')', ',', '.'
    };
    /*
    空白符的编号是181-190
     */
    private char[] ws ={
            '\n', '\r', '\t'
    };

    public int getCatalog_RW(String s){
        int catalog = -1;
        int base = 0;
        for(int i=0;i<reserveWord.length;i++){
            if(reserveWord[i].equals(s)){
                catalog = base + i;
                break;
            }
        }
        return catalog;
    }

    public int getCatalog_Op(String s){
        int catalog = -1;
        int base = 121;
        for(int i=0;i<operator.length;i++){
            if(operator[i].equals(s)){
                catalog = base + i;
                break;
            }
        }
        return catalog;
    }

    public int getCatalog_Div(char c){
        int catalog = -1;
        int base = 161;
        for(int i=0;i<divider.length;i++){
            if(divider[i]==c){
                catalog = base + i;
                break;
            }
        }
        return catalog;
    }

    public int getCatalog_WS(char c){
        int catalog = -1;
        int base = 181;
        for(int i=0;i<ws.length;i++){
            if(ws[i] == c){
                catalog = base + i;
                break;
            }
        }
        return catalog;
    }

    public String wsToString(char ws){
        if(ws=='\t'){
            return "\\t";
        }
        else if(ws=='\n'){
            return "\\n";
        }
        else if(ws=='\r'){
            return "\\r";
        }
        else{
            return "[whitespace]";
        }
    }

    public boolean isDigit(char c){
        return Character.isDigit(c);
    }

    public boolean isAlaph(char c){
        return Character.isLowerCase(c)||Character.isUpperCase(c);
    }

    public boolean isOp(char c){
        boolean flag = false;
        for(int i=0;i<this.operator.length;i++){
            if(c==this.operator[i].charAt(0)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public boolean isDivider(char c){
        boolean flag = false;
        for(int i=0;i<this.divider.length;i++){
            if(c==this.divider[i]){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public boolean isWS(char c){
        boolean flag = false;
        for(int i=0;i<this.ws.length;i++){
            if(c==this.ws[i]){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public boolean isInteger(String s){
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)=='.'){
                return false;
            }
        }
        return true;
    }
}
