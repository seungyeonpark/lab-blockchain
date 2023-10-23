package blockchain.main;

import blockchain.util.Config;
import blockchain.util.HashUtil;
import blockchain.util.SignatureUtil;

import java.util.ArrayList;

public class Transaction {

    private static float MINIMUM_TRANSACTION = Config.MINIMUM_TRANSACTION;
    public String transactionId;
    public String sender;
    public String recipient;
    public float value;
    public String signature;
    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public Transaction(String from, String to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;

        if (inputs != null) {
            this.inputs = inputs;
        }

        if (inputs == null) {
            transactionId = "0";
        }
    }

    public void generateSignature(String privateKey) throws Exception {
        String data = HashUtil.applySha256(sender + recipient + value);
        signature = SignatureUtil.generateSignature(privateKey, data);
    }

    public boolean verifySignature() throws Exception {
        String data = HashUtil.applySha256(sender + recipient + value);
        return SignatureUtil.verifySignature(sender, data, signature);
    }

    public boolean processTransaction() throws Exception {
        if (!verifySignature()) {
            System.out.println("# Transaction Signature failed to verify");
            return false;
        }

        for (TransactionInput i : inputs) {
            i.UTXO = TransactionUtxo.UTXOs.get(i.transactionOutputId);
        }

        if (getInputsValue() < MINIMUM_TRANSACTION) {
            System.out.println("# Transaction inputs too small: " + getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(recipient, value, transactionId));
        outputs.add(new TransactionOutput(sender, leftOver, transactionId));

        for (TransactionOutput o : outputs) {
            TransactionUtxo.UTXOs.put(o.id, o);
        }

        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                continue;
            }

            TransactionUtxo.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                continue;
            }

            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

    private String calculateHash() throws Exception {
        ++sequence;
        return HashUtil.applySha256(
                sender +
                        recipient +
                        value +
                        sequence
        );
    }
}
