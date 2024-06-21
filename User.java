//superclass for all user including admin and customer

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class User {

    private String username, password;

    public void setUserData(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getpassword(){
        return password;
    }

    public boolean authenticate(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}