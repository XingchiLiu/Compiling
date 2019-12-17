package lex;

public class LexParser {

    public void run(){
        String target = "resource/target.txt";
        String outputFile = "resource/tokens.txt";
        WordAnalyser wordAnalyser = new WordAnalyser(target);
        wordAnalyser.analyse();
        wordAnalyser.outPut(outputFile);
    }

}
