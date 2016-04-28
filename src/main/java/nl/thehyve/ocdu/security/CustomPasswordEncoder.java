package nl.thehyve.ocdu.security;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by piotrzakrzewski on 26/04/16.
 */
public class CustomPasswordEncoder implements PasswordEncoder {

    public String encode(CharSequence rawPassword) {
         return getSha1Hexdigest(rawPassword);
    }


    private String getSha1Hexdigest(CharSequence rawPassword) {
        MessageDigest cript = null;
        try {
            cript = MessageDigest.getInstance("SHA-1");
            cript.reset();
            cript.update(rawPassword.toString().getBytes("utf8"));
            String hexBinaryRep = DatatypeConverter.printHexBinary(cript.digest());
            return hexBinaryRep.toLowerCase() ; // OpenClinica expects lowercase representation of the hex digest
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String hexDigest = getSha1Hexdigest(rawPassword);
        return hexDigest.equals(encodedPassword);
    }
}
