package Server;

public class AccountDocument {
    private final String account;
    private final String password;

    public AccountDocument(String account, String password){
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword(){
        return password;
    }
}

