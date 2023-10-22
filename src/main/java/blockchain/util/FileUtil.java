package blockchain.util;

import java.io.*;

public class FileUtil {

    private static final String BLOCKCHAIN_DIR = Config.BLOCKCHAIN_DIR;

    public static void writeFile(String fileName, String content, boolean bForceUpdate) {
        String strFile = BLOCKCHAIN_DIR + fileName;

        try {
            File file = new File(strFile);
            if (file.exists() && !bForceUpdate) {
                return;
            }

            FileWriter fileWriter = new FileWriter(strFile);
            fileWriter.write(content);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String fileName) throws Exception {
        String strFile = BLOCKCHAIN_DIR + fileName;

        try {
            File file = new File(strFile);
            if (!file.exists()) {
                return null;
            }

            StringBuffer buff = new StringBuffer();
            char[] chars = new char[(int) file.length()];

            BufferedReader br = new BufferedReader(new FileReader(file));
            br.read(chars);
            buff.append(chars);
            br.close();

            return buff.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void deleteFile(String fileName) {
        String strFile = BLOCKCHAIN_DIR + fileName;

        try {
            File deleteFile = new File(strFile);
            if (deleteFile.exists()) {
                deleteFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
