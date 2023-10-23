package blockchain.main;

import blockchain.util.HashUtil;

public class TransactionOutput {

    public String id;
    public String recipient;
    public float value;
    public String parentTransactionId;

    public TransactionOutput(String recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        try {
            this.id = HashUtil.applySha256(recipient + value + parentTransactionId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while getting hash !");
        }
    }

    public boolean isMine(String publicKey) {
        return  publicKey == recipient;
    }
}
