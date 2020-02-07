package run;


import dao.UserDAO;
import dao.UserDBDAO;
import entities.User;
import exceptions.AlreadyExistsException;

import java.time.LocalDate;
import java.util.List;


public class Main {

    public static void main(String[] args) {

        UserDAO dao = new UserDBDAO();
        User user = new User(
                "Efrat",
                "Yuasr",
                48,
                LocalDate.of(1979, 5, 5)
        );


        try {
            User after = dao.createUser(user);
            System.out.println(after);
        } catch (AlreadyExistsException e) {
            System.out.println(e.getMessage());
        }



    }
}
