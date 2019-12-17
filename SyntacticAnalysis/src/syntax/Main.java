package syntax;

import lex.LexParser;

public class Main {

    public static void main(String[] args){
        /*
        获取token
         */
        LexParser parser = new LexParser();
        parser.run();
        /*
        解析产生式
         */
        Production.readProduction();
        /*
        求first和follow
         */
        Production.computeFirstAndFollow();
        /*
        构造ppt
         */
        PPT ppt = new PPT();
        /*
        初始化栈和读头
         */
        Monitor monitor = new Monitor(ppt);
        monitor.initialize();
        /*
        进行语法分析
         */
        monitor.run();
        /*
        输出到文件里
         */
        String outFile = "result.txt";
        monitor.outPut(outFile);
    }
}
