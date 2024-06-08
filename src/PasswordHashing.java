import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Base64;

public class PasswordHashing {
    private final String algorithm = "SHA-256";//hashing algorithm of 16 bytes

    //generate hash from password and salt
    public String generateHash(String password, byte[] salt) throws NoSuchAlgorithmException {
        if (salt == null) {
            throw new IllegalArgumentException("Salt cannot be null.");
        }
        
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
        digest.update(salt);
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    //create random salt
    public byte[] createSalt() {
        byte[] saltByte = new byte[16];
        new Random().nextBytes(saltByte);//generate random bytes
        return saltByte;
    }

    //convert byte[] to string
    public String bytestoStringBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    //change string salt to byte[]
    //when salt is stored in database, it is stored as string
    //when we get the salt from database, we need to convert it to byte[]
    public byte[] stringSaltToByte(String salt) {
        try {
            return Base64.getDecoder().decode(salt);
        } catch (IllegalArgumentException e) {
            System.err.println("Error decoding Base64 string: " + e.getMessage());
            e.printStackTrace();
            return null; // Or handle the error according to your application's requirements
        }
    }
    

    
}
/* password + salt + hashing algorithm = hashed password
 * hash = password + salt
 * password provided by uer
 * salt = random string
 * 
 * store hashed password in database not the password user type
 * when user type password, hash it and compare with hashed password in database
 * if the hash match, then the password is correct
 * has cannot be reversed, cannot get password from hash
 * 
 * salting
 * add random string to password before hashing
 * then hash the new string(password + salt)
 * the hashed password is stored in database
 * so even two user with same password will have different hashed password, bcs of different salt
 */
