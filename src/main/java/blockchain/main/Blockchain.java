package blockchain.main;

import blockchain.util.Config;
import blockchain.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;

public class Blockchain {

    private static int DIFFICULTY = Config.DIFFICULTY;
    private static String BLOCKCHAIN_FILE_NAME = Config.BLOCKCHAIN_FILE_NAME;

    public static String addGenesisBlock(String targetWalletAddress, float genesisCoin, String miner) throws Exception {

        Wallet coinbase = new Wallet("coinbase");

        Transaction genesisTransaction = new Transaction(coinbase.publicKey, targetWalletAddress, genesisCoin, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));

        TransactionUtxo.addUtxo(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
        Block block = new Block();
        block.addTransaction(genesisTransaction);
        Blockchain.addBlock(block, miner);

        return new ObjectMapper().writeValueAsString(block);
    }

    public static void addBlock(Block newBlock, String miner) throws Exception {
        newBlock.mineBlock(DIFFICULTY);
        BlockMain.blockchain.add(newBlock);
        newBlock.miner = miner;
        newBlock.height = BlockMain.blockchain.size();
        newBlock.transactionsCnt = newBlock.transactions.size();
        writeBlockchain(BlockMain.blockchain);
    }

    public static String mineBlock(String miner) throws Exception {
        Block block = new Block();

        if (!block.mineBlock2(DIFFICULTY)) {
            return "";
        }

        BlockMain.blockchain.add(block);
        block.miner = miner;
        block.height = BlockMain.blockchain.size();
        block.transactionsCnt = block.transactions.size();
        writeBlockchain(BlockMain.blockchain);
        Mempool.resetMempool();
        BlockMain.transactions = new ArrayList<>();

        return new ObjectMapper().writeValueAsString(block);
    }

    public static boolean isBlockchainValid(ArrayList<Block> blockchain) throws Exception {
        Block currentBlock;
        Block previousBlock;
        String hashTarget;

        if (blockchain == null || blockchain.size() == 0) {
            System.out.println("No Blockchain !");
            return false;
        }

        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(blockchain.get(0).transactions.get(0).outputs.get(0).id, blockchain.get(0).transactions.get(0).outputs.get(0));

        for (int i = 1; i < blockchain.size(); ++i) {
            hashTarget = new String(new char[blockchain.get(i).difficulty]).replace('\0', '0');
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println(" # Block(" + i + ") Current hashes not equal ");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println(" # Block(" + i + ") Previous hashes not equal ");
                return false;
            }

            if (!currentBlock.hash.substring(0, DIFFICULTY).equals(hashTarget)) {
                System.out.println(" # Block(" + i + ") This block hasn't been mined ");
                return false;
            }

            TransactionOutput tempOutput;
            for (int j = 0; j < currentBlock.transactions.size(); ++j) {
                Transaction currentTransaction = currentBlock.transactions.get(j);
                if (!currentTransaction.verifySignature()) {
                    System.out.println(" # Block(" + i + ") signature on transaction(" + j + ") is invalid");
                    return false;
                }

                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println(" # Block(" + i + ") Inputs are not equal to outputs on transaction(" + j + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);
                    if (tempOutput == null) {
                        System.out.println(" # Block(" + i + ") Referenced input on transaction(" + j + ") is missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println(" # Block(" + i + ") Referenced input transaction(" + j + ") value is invalid");
                        return false;
                    }
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (!currentTransaction.outputs.get(0).recipient.equals(currentTransaction.recipient)) {
                    System.out.println(" # Block(" + i + ") Transaction(" + j + ") output recipient is not who it should be");
                    return false;
                }

                if (!currentTransaction.outputs.get(1).recipient.equals(currentTransaction.sender)) {
                    System.out.println(" # Block(" + i + ") Transaction(" + j + ") output 'change is sender");
                    return false;
                }
            }
        }
        return true;
    }

    public static void writeBlockchain(ArrayList<Block> blockchain) {
        try {
            String jsonOutput = new ObjectMapper().writeValueAsString(blockchain);
            FileUtil.writeFile(BLOCKCHAIN_FILE_NAME, jsonOutput, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Block> readBlockchain() {
        try {
            if (FileUtil.readFile(BLOCKCHAIN_FILE_NAME) == null) {
                return null;
            }
            String blockchainJson = FileUtil.readFile(BLOCKCHAIN_FILE_NAME);
            ArrayList<Block> blocks = new ObjectMapper().convertValue(blockchainJson, new TypeReference<>(){});
            return blocks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
