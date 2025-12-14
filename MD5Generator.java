import java.security.MessageDigest;

public class MD5Generator {
    public static void main(String[] args) {
        String password = "12345";
        String hashed = encrypterChaineMD5(password);
        System.out.println("Password: " + password);
        System.out.println("MD5 Hash: " + hashed);
    }

    private static String encrypterChaineMD5(String chaine) {
        byte[] chaineBytes = chaine.getBytes();
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(chaineBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer hashString = new StringBuffer();
        for (int i=0; i<hash.length; ++i ) {
            String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length()-1));
            }
            else {
                hashString.append(hex.substring(hex.length()-2));
            }
        }
        return hashString.toString();
    }
}
