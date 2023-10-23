package blockchain.main;

import java.util.HashMap;

public class TransactionUtxo {

    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public static void addUtxo(String id, TransactionOutput transactionOutput) {
        UTXOs.put(id, transactionOutput);
    }

    public static void removeUtxo(String id) {
        UTXOs.remove(id);
    }
}
