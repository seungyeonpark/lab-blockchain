package blockchain.main;

import java.util.ArrayList;

public class BlockMain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static ArrayList<Transaction> transactions = new ArrayList<>();

    public BlockMain() {
        blockchain = Blockchain.readBlockchain();
        if (blockchain == null) {
            blockchain = new ArrayList<>();
        }

        transactions = Mempool.readMempool();
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
    }
}
