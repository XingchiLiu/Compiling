package syntax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Production {
    public int id;
    public Symbol left;
    public List<Symbol> right;
    public static List<Production> productionList = new ArrayList<>();//将编号与表达式对应
    public static HashMap<Integer, List<Symbol>> firstFromProduction = new HashMap<>();//由对应production求得的First
    public static HashMap<Integer, List<Symbol>> followFromProduction = new HashMap<>();
    private static HashMap<String, List<Production>> productionTable = new HashMap<>();//通过产生式左边来获取产生式
    private static HashMap<String, List<Symbol>> firstTable = new HashMap<>();
    private static HashMap<String, List<Symbol>> followTable = new HashMap<>();

    Production(String production, int id){
        String[] leftAndRight = production.split("->");
        String[] symbolArray = leftAndRight[1].split(" ");
        Symbol left = new Symbol(leftAndRight[0]);
        List<Symbol> right = new ArrayList<>();
        for(int i=0;i<symbolArray.length;i++){
            right.add(new Symbol(symbolArray[i]));
        }
        this.left = left;
        this.right = right;
        this.id = id;
    }

    String getLeftName(){
        return this.left.getName();
    }

    public static void readProduction(){
        String filename = "src/resource/production.txt";
        File f = new File(filename);
        String line;
        int id = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            System.out.println("---Reading productions---");
            while(null!=(line = br.readLine())){
                Production production = new Production(line, id++);
                productionList.add(production);
                String key = production.getLeftName();
                if (productionTable.containsKey(key)){
                    productionTable.get(key).add(production);
                }
                else {
                    List<Production> productions = new ArrayList<>();
                    productions.add(production);
                    productionTable.put(key, productions);
                }
                System.out.println(production);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void computeFirstAndFollow(){
        //计算first

        Iterator<Symbol> itr = Symbol.getVnItr();
        System.out.println("--Computing first--");
        while(itr.hasNext()){
            Symbol Vn = itr.next();
            List<Symbol> firstList = First(Vn);
            firstTable.put(Vn.getName(), firstList);
            /*
            print
             */
            System.out.print("First of " + Vn.getName() + ": ");
            for(Symbol symbol: firstList){
                System.out.print(symbol.getName()+" ");
            }
            System.out.println();
        }
        //计算first中含有epsilon的Vn的follow
        System.out.println("--Computing follow--");
        for(String name: firstTable.keySet()){
            List<Symbol> firstList = firstTable.get(name);
            boolean hasEpsilon = containsEpsilon(firstList);
            if(hasEpsilon){
                Symbol Vn = Symbol.getSymbolByName(name);
                List<Symbol> followList = Follow(Vn);
                followTable.put(name, followList);
                /*
                print
                 */
                System.out.print("Follow of " + Vn.getName() + ": ");
                for(Symbol symbol: followList){
                    System.out.print(symbol.getName()+" ");
                }
                System.out.println();
            }
        }
    }

    private static boolean containsEpsilon(List<Symbol> list){
        boolean hasEpsilon = false;
        for(Symbol symbol: list){
            if(symbol.isEpsilon()){
                hasEpsilon = true;
                break;
            }
        }
        return hasEpsilon;
    }

    private static List<Symbol> removeEpsilon(List<Symbol> list){
        int i = 0;
        while(true){
            if(i>=list.size()){
                break;
            }
            if(list.get(i).isEpsilon()){
                list.remove(i);
            }
            else{
                i++;
            }
        }
        return list;
    }

    private static List<Symbol> First(Symbol Vn){
        //如果算过了就不重复算了
        if(firstTable.containsKey(Vn.getName())){
            return firstTable.get(Vn.getName());
        }
        List<Symbol> firstList = new ArrayList<>();
        //遍历Vn为左部的表达式
        for(Production production: productionTable.get(Vn.getName())){
            List<Symbol> lineFirst = LineFirst(0, production.right);
            firstFromProduction.put(production.id, lineFirst);
            firstList.addAll(lineFirst);
        }
        //去重
        List<String> nameList = new ArrayList<>();
        List<Symbol> deduplicateList = new ArrayList<>();
        for(Symbol symbol: firstList){
            if(!nameList.contains(symbol.getName())){
                nameList.add(symbol.getName());
                deduplicateList.add(symbol);
            }
        }
        return deduplicateList;
    }

    /*
    求取一行中的位置i的first
     */
    private static List<Symbol> LineFirst(int i, List<Symbol> rightOfProduction){
        List<Symbol> result = new ArrayList<>();
        if(i >= rightOfProduction.size()){
            result.add(new Symbol("ε"));
            return result;
        }

        Symbol symbol = rightOfProduction.get(i);
        //如果是终结符的话
        if(symbol.isVt()){
            result.add(symbol);
            return result;
        }
        //如果不是终结符的话
        List<Symbol> firstOfHead = First(symbol);
        if(containsEpsilon(firstOfHead)){
            result.addAll(removeEpsilon(firstOfHead));
            result.addAll(LineFirst(i+1, rightOfProduction));
        }
        else{
            result.addAll(firstOfHead);
        }
        return result;
    }

    private static List<Symbol> Follow(Symbol Vn){
        //不重复求
        if(followTable.containsKey(Vn.getName())){
            return followTable.get(Vn.getName());
        }
        //求取follow
        List<Symbol> result = new ArrayList<>();
        //follow(S)需要加上$
        if(Vn.getName().equals("S")){
            result.add(new Symbol("$"));
        }
        //所有的表达式
        List<Production> all = new ArrayList<>();
        for(String name: productionTable.keySet()){
            all.addAll(productionTable.get(name));
        }
        //对于所有的产生式来说
        for(Production production: all){
            //如果产生式右部含有Vn
            for(int i=0;i<production.right.size();i++){
                if(production.right.get(i).getName().equals(Vn.getName())){
                    List<Symbol> firstAfterPtr = LineFirst(i+1, production.right);
                    if(containsEpsilon(firstAfterPtr)){
                        result.addAll(removeEpsilon(firstAfterPtr));
                        //如果遇到自己的follow，消除，不然才求取
                        if(!Vn.getName().equals(production.left.getName())){
                            result.addAll(Follow(production.left));
                        }
                    }
                    else{
                        result.addAll(firstAfterPtr);
                    }
                }
            }
        }
        //去重
        List<String> nameList = new ArrayList<>();
        List<Symbol> deduplicateList = new ArrayList<>();
        for(Symbol symbol: result){
            if(!nameList.contains(symbol.getName())){
                nameList.add(symbol.getName());
                deduplicateList.add(symbol);
            }
        }
        //记录followFromProduction
        for(Map.Entry<Integer, List<Symbol>> entry: firstFromProduction.entrySet()){
            if(productionList.get(entry.getKey()).left.getName().equals(Vn.getName())){
                if(containsEpsilon(entry.getValue())){
                    followFromProduction.put(entry.getKey(), deduplicateList);
                }
            }
        }
        return deduplicateList;
    }


    public String toString(){
        String result = "";
        result += this.getLeftName() + "->";
        result += this.right.get(0).getName();
        for(int i=0;i<this.right.size()-1;i++){
            result += " " + this.right.get(1+i).getName();
        }
        return result;
    }
}
