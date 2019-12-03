import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析器的主类
 */
public class WordAnalyser {

    private char[] buffer;
    private List<Token> tokenList;
    private List<String> annotationList;

    public WordAnalyser(String target){
        readIntoBuffer(target);
        this.annotationList = new ArrayList<>();
        this.tokenList = new ArrayList<>();
    }

    public void analyse(){
        this.getAnnotation();//预处理，读取注释
        this.analyseFALoop();
    }

    private void analyseFALoop(){

        LexSetting setting = LexSetting.getLexSetting();

        int index = 0;
        int state = 0;
        int rowCount = 0;
        int IDCount = 0;
        String keywordCollector = "";
        String digitCollector = "";
        String opCollector = "";
        char divCollector = '\0';
        char wsCollector = '\0';

        while(true){
            char ch = this.buffer[index];

            if(ch=='\n'){
                rowCount++;
            }

            if(ch == '#'){
                break;
            }

            switch (state){

                case 0://状态S
                    if(setting.isAlaph(ch)){
                        keywordCollector += ch;
                        state = 1;
                    }
                    else if(setting.isDigit(ch)){
                        digitCollector += ch;
                        state = 2;
                    }
                    else if(setting.isOp(ch)){
                        opCollector += ch;
                        state = 5;
                    }
                    else if(setting.isDivider(ch)){
                        divCollector = ch;
                        state = 7;//终态输出分界符
                    }
                    else if(setting.isWS(ch)){
                        wsCollector = ch;
                        state = 8;
                    }
                    else if(ch==' '){
                        //do nothing
                    }
                    else{
                        reportError(String.format("undefined char %c", ch), rowCount);
                        state = 0;
                    }
                    break;
                case 1:
                    if(setting.isDigit(ch)||setting.isAlaph(ch)){
                        keywordCollector += ch;
                        state = 1;
                    }
                    else{
                        index--;//回退指针
                        //终止，输出token
                        int catalog = setting.getCatalog_RW(keywordCollector);
                        Token token;
                        if(catalog == -1){//说明不是保留字
                            catalog = 100;
                            token = new Token(keywordCollector, catalog, ++IDCount);
                        }
                        else{
                            token = new Token(keywordCollector, catalog);
                        }
                        this.tokenList.add(token);
                        keywordCollector = "";
                        state = 0;
                    }
                    break;
                case 2:
                    if(setting.isDigit(ch)){
                        digitCollector += ch;
                        state = 2;
                    }
                    else if(ch=='.'){
                        digitCollector += '.';
                        state = 3;
                    }
                    else{//进行输出
                        index--;//回退指针
                        Token token = new Token(digitCollector, 101);
                        this.tokenList.add(token);
                        digitCollector = "";
                        state = 0;
                    }
                    break;
                case 3:
                    if(setting.isDigit(ch)){
                        digitCollector += ch;
                        state = 4;
                    }
                    else{//进行报错
                        index--;
                        this.reportError("not a digit after '.'", rowCount);
                        digitCollector = "";//清洗
                        state = 0;
                    }
                    break;
                case 4:
                    if(setting.isDigit(ch)){
                        digitCollector += ch;
                        state = 4;
                    }
                    else{//进行输出
                        index--;//回退指针
                        Token token = new Token(digitCollector, 102);
                        this.tokenList.add(token);
                        digitCollector = "";
                        state = 0;
                    }
                    break;
                case 5:
                    if(setting.isOp(ch)){
                        opCollector += ch;
                        state = 6;
                    }
                    else{
                        index--;//回退指针
                        int catalog = setting.getCatalog_Op(opCollector);
                        Token token = new Token(opCollector, catalog);
                        this.tokenList.add(token);
                        opCollector = "";
                        state = 0;
                    }
                    break;
                case 6:
                    if(setting.isOp(ch)){
                        //三连op，报错
                        reportError("undefined op", rowCount);
                        state = 0;
                        opCollector = "";
                    }
                    else{
                        index--;//回退，进行输出
                        int catalog = setting.getCatalog_Op(opCollector);
                        if(catalog==-1){
                            reportError("undefined op", rowCount);
                            state = 0;
                            opCollector = "";
                        }
                        else{
                            Token token = new Token(opCollector, catalog);
                            this.tokenList.add(token);
                            state = 0;
                            opCollector = "";
                        }
                    }
                    break;
                case 7:
                    index--;
                    int catalog = setting.getCatalog_Div(divCollector);
                    Token token = new Token(Character.toString(divCollector), catalog);
                    this.tokenList.add(token);
                    state = 0;
                    divCollector = '\0';
                    break;
                case 8:
                    index--;
                    int catalog2 = setting.getCatalog_WS(wsCollector);
                    Token token2 = new Token(setting.wsToString(wsCollector), catalog2);
                    this.tokenList.add(token2);
                    state = 0;
                    wsCollector = '\0';
                    break;
                default:
                    System.out.println("How could that be possible, wrong state!");
            }

            index++;

        }
    }


    private void reportError(String errorMes, int row){
        Token token = new Token(errorMes, -1);
        this.tokenList.add(token);
        System.out.println(String.format("Error in row %d: %s", row, errorMes));
    }

    /*
    预处理，从buffer中获取注释，存到数组中
     */
    private void getAnnotation(){
        int index = 0;
        String newBuffer = "";
        while(true){

            if(index + 1<this.buffer.length){
                if(this.buffer[index]=='/' && this.buffer[index+1]=='/'){
                    //读取注释内容
                    index = index + 2;
                    String annotation = "";
                    while(index<this.buffer.length){
                        char c = this.buffer[index];
                        if(c=='\n'||c=='#'){
                            break;
                        }
                        else{
                            annotation += this.buffer[index];
                            index++;
                        }
                    }
                    this.annotationList.add(annotation);
                }
                else{
                    newBuffer += this.buffer[index];
                    index++;
                }
            }
            else{
                //剩余的全部读进
                for(int i=index;i<this.buffer.length;i++){
                    newBuffer += this.buffer[i];
                }
                break;
            }
        }
        this.buffer = newBuffer.toCharArray();
    }

    public void outPut(String filename){
        filename = "src/" + filename;
        TextIO.outPutTokens(this.tokenList, filename);
    }

    private void readIntoBuffer(String target){
        char[] original = TextIO.getInputChars(target);
        String origin = new String(original);
        origin += '#';
        this.buffer = origin.toCharArray();
    }

}
