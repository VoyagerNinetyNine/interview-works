package Server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountHandle {
    private final List<AccountDocument> accounts;
    public final String path = "account.txt";
    private boolean isAccountExiting = false;
    private boolean isPasswordRight = false;
    private final AccountDocument userAccount;


    public AccountHandle(String account, String password){
        accounts = new ArrayList<AccountDocument>();
        userAccount = new AccountDocument(account,password);
        String existedaccount,existedpassword;
        try {
            if(!Files.exists(Path.of(path))){
                Files.createFile(Path.of(path));
            }
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            for(String inf : lines){
                String[] stringlist = inf.split(",");
                existedaccount = stringlist[0];
                existedpassword = stringlist[1];
                accounts.add(new AccountDocument(existedaccount,existedpassword));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        accountJudge();
    }

    public void accountJudge(){
        for(AccountDocument accountDocument : accounts){
            if(Objects.equals(accountDocument.getAccount(),userAccount.getAccount())){
                isAccountExiting = true;
                isPasswordRight = Objects.equals(accountDocument.getPassword(), userAccount.getPassword());
                break;
            }
            else{
                isAccountExiting = false;
            }
        }
    }

    public boolean IsAccountExisting(){
        return isAccountExiting;
    }

    public boolean IsPasswordRight(){
        return isPasswordRight;
    }

    public void saveAccount(){
        Path of = Path.of(path);
        try {
            String doc = userAccount.getAccount() + "," + userAccount.getPassword() + "\n";
            Files.write(of,doc.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
