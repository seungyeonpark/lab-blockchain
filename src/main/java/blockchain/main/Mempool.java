package blockchain.main;

import blockchain.util.Config;
import blockchain.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class Mempool {

    public static int MAX_MEMPOOL_SIZE = Config.MAX_MEMPOOL_SIZE;
    private static String MEMPOOL_FILE_NAME = Config.MEMPOOL_FILE_NAME;

    public static void writeMempool(ArrayList<Transaction> transactions) {
        try {
            String jsonOutput = new ObjectMapper().writeValueAsString(transactions);
            FileUtil.writeFile(MEMPOOL_FILE_NAME, jsonOutput, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Transaction> readMempool() {
        try {
            String transactionJson = FileUtil.readFile(MEMPOOL_FILE_NAME);
            ArrayList<Transaction> transactions = new ObjectMapper().convertValue(transactionJson, new TypeReference<>(){});
            return transactions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void resetMempool() {
        try {
            FileUtil.deleteFile(MEMPOOL_FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
