package blockchain.main;

import blockchain.util.Config;
import blockchain.util.FileUtil;
import blockchain.util.RsaKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public String owner;
    public String privateKey;
    public String publicKey;
    private static final String PRIVATE_KEY_FILE_EXTENSION = Config.PRIVATE_KEY_FILE_EXTENSION;
    private static final String PUBLIC_KEY_FILE_EXTENSION = Config.PUBLIC_KEY_FILE_EXTENSION;

    public Wallet(String walletName) {
        this.owner = walletName;
        keyInitialize();
    }

    public float getBalance() {
        float total = 0;
        Block currentBlock;

        for (Block block : BlockMain.blockchain) {
            for (Transaction transaction : block.transactions) {
                if (transaction.recipient.equals(publicKey)) {
                    total -= transaction.value;
                }
            }
        }

        for (Transaction transaction : BlockMain.transactions) {
            if (transaction.recipient.equals(publicKey)) {
                total += transaction.value;
            }

            if (transaction.sender.equals(publicKey)) {
                total -= transaction.value;
            }
        }

        return total;
    }

    public float getInputBalance() {
        float total = 0;

        for (Block block : BlockMain.blockchain) {
            for (Transaction transaction : block.transactions) {
                if (transaction.sender.equals(publicKey)) {
                    total += transaction.value;
                }
            }
        }

        for (Transaction transaction : BlockMain.transactions) {
            if (transaction.sender.equals(publicKey)) {
                total += transaction.value;
            }
        }

        return total;
    }

    public float getOutputBalance() {
        float total = 0;

        for (Block block : BlockMain.blockchain) {
            for (Transaction transaction : block.transactions) {
                if (transaction.recipient.equals(publicKey)) {
                    total += transaction.value;
                }
            }
        }

        for (Transaction transaction : BlockMain.transactions) {
            if (transaction.recipient.equals(publicKey)) {
                total += transaction.value;
            }
        }

        return total;
    }

    public void sendFunds(String _recipient, float value) throws Exception {
        if (getBalance() < value) {
            System.out.println("# Not enough funds to send transaction. Transaction Discarded");
            return;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();
        addUtxo();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : TransactionUtxo.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);
        if (!newTransaction.processTransaction()) {
            System.out.println("Transaction failed to process. Discarded");
            return;
        }

        BlockMain.transactions.add(newTransaction);
        TransactionUtxo.UTXOs = new HashMap<>();

        Mempool.writeMempool(BlockMain.transactions);
        System.out.println("Transaction writes Mempool");
    }

    public void addUtxo() {
        try {
            HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();

            for (Block block : BlockMain.blockchain) {
                for (Transaction transaction : block.transactions) {
                    for (TransactionOutput output : transaction.outputs) {
                        if (output.recipient.equals(publicKey)) {
                            TransactionUtxo.addUtxo(output.id, output);
                        }
                    }

                    String transactionOutputId = "";
                    if (transaction.inputs.size() > 0) {
                        transactionOutputId = transaction.inputs.get(0).transactionOutputId;
                    }

                    for (Map.Entry<String, TransactionOutput> elem : TransactionUtxo.UTXOs.entrySet()) {
                        if (!elem.getKey().equals(transactionOutputId)) {
                            tempUTXOs.put(elem.getKey(), elem.getValue());
                        }
                    }
                }
            }

            for (Transaction transaction : BlockMain.transactions) {
                for (TransactionOutput output : transaction.outputs) {
                    if (output.recipient.equals(publicKey)) {
                        TransactionUtxo.addUtxo(output.id, output);
                    }
                }

                String transactionOutputId = "";
                if (transaction.inputs.size() > 0) {
                    transactionOutputId = transaction.inputs.get(0).transactionOutputId;
                }

                for (Map.Entry<String, TransactionOutput> item : TransactionUtxo.UTXOs.entrySet()) {
                    if (!item.getKey().equals(transactionOutputId)) {
                        tempUTXOs.put(item.getKey(), item.getValue());
                    }
                }
            }

            TransactionUtxo.UTXOs = tempUTXOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void keyInitialize() {
        String strPivFile = owner + PRIVATE_KEY_FILE_EXTENSION;
        String strPubFile = owner + PUBLIC_KEY_FILE_EXTENSION;

        try {
            this.privateKey = FileUtil.readFile(strPivFile);
            this.publicKey = FileUtil.readFile(strPubFile);
        } catch (Exception e) {
            System.out.println("Error while reading key file !");
        }

        if (privateKey != null && publicKey != null) {
            return;
        }

        RsaKey key = new RsaKey();
        this.privateKey = key.getPrivateKeyString();
        this.publicKey = key.getPublicKeyString();

        FileUtil.writeFile(strPivFile, privateKey, false);
        FileUtil.writeFile(strPubFile, publicKey, false);
    }
}
