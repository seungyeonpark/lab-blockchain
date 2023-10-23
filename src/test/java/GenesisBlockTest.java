import blockchain.main.BlockMain;
import blockchain.main.Blockchain;
import blockchain.main.Wallet;
import org.junit.jupiter.api.Test;

public class GenesisBlockTest {

    @Test
    public void genesisBlock() throws Exception {
        Wallet walletA = new Wallet("A");
        Blockchain.addGenesisBlock(walletA.publicKey, 3000f, "test");
        if (Blockchain.isBlockchainValid(BlockMain.blockchain)) {
            System.out.println("Saved Blockchain is valid");
        }
    }
}
