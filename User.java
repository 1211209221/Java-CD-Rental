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

    public boolean compare(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 2) {
                    String fileUsername = credentials[0].trim();
                    String filePassword = credentials[1].trim();
                    if (fileUsername.equals(this.username) && filePassword.equals(this.password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}