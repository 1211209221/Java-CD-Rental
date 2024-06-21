//subclass of class user
//for register and login purpose
//will separate later
//so it is readable and understandable

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class Customer extends User{
    
    public void setData(String username, String password){
        // Use superclass method to set the data
        setUserData(username, password);
    }

    //for login
    public boolean passFilename(){
        // Pass customer file to superclass to compare and return true or false
        return authenticate("records/customers.txt");
    }

    //for register
    public boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("records/customers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidPassword(String password) {
        // Password must be at least 8 characters long and include at least one number, one special character, and one capital letter.
        String pattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.matches(pattern, password);
    }
}
