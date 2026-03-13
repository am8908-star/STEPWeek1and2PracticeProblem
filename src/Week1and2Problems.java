import java.util.*;

class Transaction {

    int id;
    int amount;
    String merchant;
    String account;
    long time;

    Transaction(int id,int amount,String merchant,String account,long time){
        this.id=id;
        this.amount=amount;
        this.merchant=merchant;
        this.account=account;
        this.time=time;
    }
}

class FraudDetector {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t){
        transactions.add(t);
    }

    public List<List<Transaction>> findTwoSum(int target){

        Map<Integer,Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for(Transaction t: transactions){

            int complement = target - t.amount;

            if(map.containsKey(complement)){
                result.add(Arrays.asList(map.get(complement),t));
            }

            map.put(t.amount,t);
        }

        return result;
    }

    public List<List<Transaction>> findTwoSumWithWindow(int target,long windowMs){

        Map<Integer,Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for(Transaction t: transactions){

            int complement = target - t.amount;

            if(map.containsKey(complement)){
                Transaction prev = map.get(complement);

                if(Math.abs(t.time-prev.time) <= windowMs){
                    result.add(Arrays.asList(prev,t));
                }
            }

            map.put(t.amount,t);
        }

        return result;
    }

    public List<List<Transaction>> findKSum(int k,int target){

        List<List<Transaction>> result = new ArrayList<>();
        backtrack(0,k,target,new ArrayList<>(),result);
        return result;
    }

    private void backtrack(int index,int k,int target,List<Transaction> current,List<List<Transaction>> result){

        if(k==0 && target==0){
            result.add(new ArrayList<>(current));
            return;
        }

        if(k==0 || index>=transactions.size()) return;

        for(int i=index;i<transactions.size();i++){

            Transaction t = transactions.get(i);

            current.add(t);
            backtrack(i+1,k-1,target-t.amount,current,result);
            current.remove(current.size()-1);
        }
    }

    public List<String> detectDuplicates(){

        Map<String,Set<String>> map = new HashMap<>();

        for(Transaction t: transactions){

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key,new HashSet<>());
            map.get(key).add(t.account);
        }

        List<String> result = new ArrayList<>();

        for(String key: map.keySet()){

            if(map.get(key).size()>1){
                result.add("Duplicate: "+key+" accounts="+map.get(key));
            }
        }

        return result;
    }
}

public class FinancialFraudSystem {

    public static void main(String[] args){

        FraudDetector detector = new FraudDetector();

        detector.addTransaction(new Transaction(1,500,"StoreA","acc1",1000));
        detector.addTransaction(new Transaction(2,300,"StoreB","acc2",2000));
        detector.addTransaction(new Transaction(3,200,"StoreC","acc3",3000));
        detector.addTransaction(new Transaction(4,500,"StoreA","acc2",4000));

        System.out.println("Two Sum:");

        for(List<Transaction> pair: detector.findTwoSum(500)){
            System.out.println(pair.get(0).id + " , " + pair.get(1).id);
        }

        System.out.println("\nTwo Sum With 1hr Window:");

        for(List<Transaction> pair: detector.findTwoSumWithWindow(500,3600000)){
            System.out.println(pair.get(0).id + " , " + pair.get(1).id);
        }

        System.out.println("\nK Sum (k=3 target=1000):");

        for(List<Transaction> list: detector.findKSum(3,1000)){
            for(Transaction t:list) System.out.print(t.id+" ");
            System.out.println();
        }

        System.out.println("\nDuplicate Detection:");

        for(String s: detector.detectDuplicates()){
            System.out.println(s);
        }
    }
}