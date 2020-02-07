package dao;

import entities.User;
import exceptions.AlreadyExistsException;
import exceptions.NotExistsException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDBDAO implements UserDAO {

    private Connection connection;

    public UserDBDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_db" + "?user=root"
                    + "&password=root" + "&useUnicode=true" + "&useJDBCCompliantTimezoneShift=true"
                    + "&useLegacyDatetimeCode=false" + "&serverTimezone=UTC"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User buildUser(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(1);
        String firstName = resultSet.getString(2);
        String lastName = resultSet.getString(3);
        int age = resultSet.getInt(4);
        LocalDate birthDate = resultSet.getDate(5).toLocalDate();
        return new User(id, firstName, lastName, age, birthDate);
    }

    @Override
    public User getById(long id) throws NotExistsException {

        User user = null;
        String sql = "SELECT * FROM USERS WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                user = buildUser(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user == null) {
            throw new NotExistsException("User with the id: " + id + " does not exists");
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> all = new ArrayList<>();

        String sql = "SELECT * FROM USERS";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                User user = buildUser(resultSet);
                all.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return all;
    }

    @Override
    public List<User> getByFullName(String firstName, String lastName) {
        List<User> byNames = new ArrayList<>();

        String sql = "SELECT * FROM USERS WHERE FIRST_NAME = ? AND LAST_NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                User user = buildUser(resultSet);
                byNames.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return byNames;
    }

    @Override
    public User createUser(User user) throws AlreadyExistsException {
        List<User> byFullName = getByFullName(user.getFirstName(), user.getLastName());

        if (!byFullName.isEmpty()) {
            throw new AlreadyExistsException(
                    "User with the name " +
                            user.getFirstName() + "-" + user.getLastName() +
                            " already exists."
            );
        }

        String sql = "INSERT INTO USERS (FIRST_NAME, LAST_NAME, AGE, BIRTH_DATE) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setInt(3, user.getAge());
            pstmt.setDate(4, Date.valueOf(user.getBirthDate()));

            pstmt.executeUpdate();

            ResultSet resultSet = pstmt.getGeneratedKeys();

            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
