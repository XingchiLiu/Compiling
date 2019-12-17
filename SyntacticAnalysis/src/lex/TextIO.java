package lex;

import java.io.*;
import java.util.List;

public class TextIO {

    public static char[] getInputChars(String filename){
        File f = new File(filename);
        if(!f.exists()){
            f = new File("src/"+filename);
        }
        if(!f.exists()){
            System.out.println("The file " + filename + " doesn't exist!");
            return null;
        }
        char[] result = {};
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new FileReader(f));
            while((line = br.readLine())!=null){
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            result = stringBuilder.toString().toCharArray();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void outPutTokens(List<Token> tokenSequence, String name){
        if(!name.startsWith("src/")){
            name = "src/" + name;
        }
        File f = new File(name);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("lexme, catalog, inner_code\n\n");
            for(int i=0;i<tokenSequence.size();i++){
                bw.write(tokenSequence.get(i).toString()+"\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
