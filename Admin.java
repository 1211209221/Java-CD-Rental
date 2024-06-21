//subclass of class user
//for login purpose
//will separate later
//so it is readable and understandable


public class Admin extends User{
    public void setData(String username, String password){
        // Use superclass method to set the data
        setUserData(username, password);
    }

    public boolean passFilename(){
        // Pass admin file to superclass to compare and return true or false
        return authenticate("records/admin.txt");
    }

}
