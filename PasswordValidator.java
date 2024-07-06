package Server;

import java.util.regex.*;

public class PasswordValidator {
    public static String checkPass(String p) throws Exception {
        if (p.length() < 8) {
            return "0";
        }
        if (!Pattern.compile("[@!#$]").matcher(p).find()) {
            return "1";
        }
        if (!Pattern.compile("[A-Z]").matcher(p).find()) {
            return "2";
        }
        if (!Pattern.compile("[a-z]").matcher(p).find()) {
            return "3";
        }
        if (!Pattern.compile("[0-9]").matcher(p).find()) {
            return "4";
        }
        return "5";
    }
}