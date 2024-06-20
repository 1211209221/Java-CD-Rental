//subclass of class user
//for register and login purpose
//will separate later
//so it is readable and understandable

public class Customer extends User{
    
    public void setData(String username, String password){
        // Use superclass method to set the data
        setUserData(username, password);
    }

    public boolean passFilename(){
        // Pass customer file to superclass to compare and return true or false
        return compare("records/users.txt");
    }
}
