
public class Main {

    public static void main(String args[]){
        String target = "target.txt";
        String outputFile = "result.txt";
        WordAnalyser wordAnalyser = new WordAnalyser(target);
        wordAnalyser.analyse();
        wordAnalyser.outPut(outputFile);
    }

}
