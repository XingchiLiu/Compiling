package lex;
/*

 */
public class Token {
    private String lexme;//名称
    private int catalog;//类别码
    private int inner_code;//内部码，用于区分同种token的不同个体，主要用于ID,如果是唯一的则为0。

    public Token(String lexme, int catalog, int inner_code){
        this.lexme = lexme;
        this.catalog = catalog;
        this.inner_code = inner_code;
    }

    public Token(String lexme, int catalog){
        this.lexme = lexme;
        this.catalog = catalog;
        this.inner_code = 0;
    }

    public String toString(){
        //<lexme, catalog, inner_code>
        return String.format("<%s, %d, %d>", this.lexme, this.catalog, this.inner_code);
    }

}
