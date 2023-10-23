package blockchain.main;

import blockchain.util.Config;
import blockchain.util.HashUtil;

import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String version = Config.SOFTWARE_VERSION;
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public long timeStamp;
    public int nonce;
    public int height;
    public int difficulty;
    public String miner;
    public long mineTime;
    public int transactionsCnt;
    public ArrayList<Transaction> transactions = new ArrayList<>();

    public Block() {
        if (BlockMain.blockchain.size() == 0) {
            this.previousHash = "0";
        } else {
            this.previousHash = BlockMain.blockchain.get(BlockMain.blockchain.size() - 1).hash;
        }
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String hash;
        try {
            hash = HashUtil.applySha256(previousHash + timeStamp + nonce + merkleRoot);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return hash;
    }

    public void mineBlock(int difficulty) throws Exception {
        long startTime;
        long endTime;
        this.difficulty = difficulty;

        merkleRoot = getMerkleRoot(transactions);
        String target = getDifficultyString(difficulty);
        startTime = new Date().getTime();
        System.out.println("Start Mining Block ...");
        while(!hash.substring(0, difficulty).equals(target)) {
            ++nonce;
            hash = calculateHash();
        }
        endTime = new Date().getTime();
        this.mineTime = endTime - startTime;

        System.out.println("Finish Mining Block : hash = " + hash);
    }

    public boolean mineBlock2(int difficulty) throws Exception {
        long startTime;
        long endTime;
        this.difficulty = difficulty;

        System.out.println("Start Mining Block ...");
        transactions = BlockMain.transactions;
        if (transactions.size() < Mempool.MAX_MEMPOOL_SIZE) {
            System.out.println("Transaction Mempool size is not enough...");
            return false;
        }

        merkleRoot = getMerkleRoot(transactions);
        String target = getDifficultyString(difficulty);
        startTime = new Date().getTime();
        while (!hash.substring(0, difficulty).equals(target)) {
            ++nonce;
            hash = calculateHash();
        }
        endTime = new Date().getTime();
        mineTime = endTime - startTime;
        System.out.println("Finish Mining Block : hash = " + hash);
        return true;
    }

    public boolean addTransaction(Transaction transaction) throws Exception {
        if (transaction == null) {
            return false;
        }

        if (previousHash != "0") {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction successfully added to Block");
        return true;
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions) throws Exception {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>();

            for (int i = 1; i < previousTreeLayer.size(); ++i) {
                treeLayer.add(HashUtil.applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

    private String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
}
