package syntax;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
该类负责栈和读头的初始化以及分析token序列的过程
 */
public class Monitor {
    private PPT ppt;
    private Stack<Symbol> stack;
    private List<Integer> inputTokens;
    private int reader;
    private List<Production> deduceList;//推导列表

    Monitor(PPT ppt){
        this.ppt = ppt;
        this.stack = new Stack<>();
        inputTokens = new ArrayList<>();
        deduceList = new ArrayList<>();
    }

    void initialize(){
        /*
        初始化栈
         */
        Symbol terminal = new Symbol("$");
        Symbol start = new Symbol("S");
        this.stack.push(terminal);
        this.stack.push(start);
        /*
        初始化读头
         */
        this.getTokens();
        reader = 0;
    }

    private void getTokens(){
        File f = new File("src/resource/tokens.txt");
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(f));
            while((line=br.readLine())!=null){
                //如果是个token
                if(line.startsWith("<")){
                    //去除空白
                    if(!line.startsWith("<\\n")&&!line.startsWith("<\\t")){
                        String[] tempArray = line.split(",");
                        inputTokens.add(Integer.parseInt(tempArray[1].substring(1)));//添加code
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void run(){
        System.out.println("--Running--");
        while(!this.stack.empty()){
            Symbol top = this.stack.pop();
            if(top.isVt()){
                if(inputTokens.get(reader)==top.getCode()){
                    reader++;
                }
                else{
                    System.out.println("Error!");
                    break;
                }
            }
            else{
                int productionId = ppt.getProduction(top, inputTokens.get(reader));
                Production production = Production.productionList.get(productionId);
                if(production.right.get(0).getCode()==103){
                    //如果是产生空，直接弹出
                }
                else {
                    for(int i=production.right.size()-1;i>=0;i--){
                        this.stack.push(production.right.get(i));
                    }
                }
                this.deduceList.add(production);
                System.out.println(production);
            }
        }
    }

    void outPut(String filename){
        if(!filename.startsWith("src/")){
            filename = "src/resource/" + filename;
        }
        File f = new File(filename);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            int count = 0;
            for(Production production: this.deduceList){
                bw.write("Step "+String.valueOf(++count) +": " + production.toString()+"\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
