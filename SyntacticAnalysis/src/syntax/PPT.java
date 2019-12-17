package syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是一个PPT
 */
public class PPT {
    private List<Integer> VtArray;//以终结符的code表示，不含诶普西隆
    private List<String> VnArray;//所有状态，即非终结符
    private int[][] ppt2DAarray;//存放着产生式编号

    PPT(){
        this.VtArray = new ArrayList<>();
        this.VnArray = new ArrayList<>();
        this.initial();
    }

    private void initial(){
       /*
       填充VtArray
        */
       for(Symbol symbol: Symbol.VtTable.values()){
           this.VtArray.add(symbol.getCode());
       }
       this.VtArray.add(104);
       /*
       填充VnArray
        */
        for(Symbol symbol: Symbol.VnTable.values()){
            this.VnArray.add(symbol.getName());
        }
       this.ppt2DAarray = new int[VnArray.size()][VtArray.size()];
        for(int i=0;i<VnArray.size();i++){
            for(int j=0;j<VtArray.size();j++){
                this.ppt2DAarray[i][j] = -1;
            }
        }

        HashMap<Integer, List<Symbol>> firstMap = Production.firstFromProduction;
        HashMap<Integer, List<Symbol>> followMap = Production.followFromProduction;
        /*
        填first
         */
        for(Map.Entry<Integer, List<Symbol>> entry: firstMap.entrySet()){
            int i,j=0;
            i = VnArray.indexOf(Production.productionList.get(entry.getKey()).left.getName());
//            System.out.println(i);
//            System.out.println(Production.productionList.get(entry.getKey()).left.getName());
            for(Symbol Vt: entry.getValue()){
                //如果不是终态的话
                if(Vt.getCode()!=103){
                    j = VtArray.indexOf(Vt.getCode());
//                    System.out.print(" ");
//                    System.out.print(j);
                    this.ppt2DAarray[i][j] = entry.getKey();
                }
            }
//            System.out.println();
        }

//        System.out.println("--now follow--");

        /*
        填follow
         */
        for(Map.Entry<Integer, List<Symbol>> entry: followMap.entrySet()){
            int i,j;
            i = VnArray.indexOf(Production.productionList.get(entry.getKey()).left.getName());
//            System.out.println(i);
            for(Symbol Vt: entry.getValue()){
                j = VtArray.indexOf(Vt.getCode());
//                System.out.print(" ");
//                System.out.print(j);
                this.ppt2DAarray[i][j] = entry.getKey();
            }
//            System.out.println();
        }

        System.out.println("--Initialize ppt--");
        System.out.printf("%-10s","Table");
        for(int m: this.VtArray){
            System.out.print("\t"+m);
        }
        System.out.println();
        for(int i=0;i< this.VnArray.size();i++){
            System.out.printf("%-10s", this.VnArray.get(i));
            for(int j=0;j<this.VtArray.size();j++){
                System.out.print("\t"+this.ppt2DAarray[i][j]);
            }
            System.out.println();
        }

    }

    public int getProduction(Symbol Vn, int code){
        int i = this.VnArray.indexOf(Vn.getName());
        int j = this.VtArray.indexOf(code);
        return this.ppt2DAarray[i][j];
    }
}
