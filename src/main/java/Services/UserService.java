package Services;

import Entities.User;
import Utils.MyDataBase;
import org.json.JSONArray;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.List;

public class UserService implements UserServiceInterface<User>{
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    // Constantes pour les noms de colonnes
    private static final String COL_ID = "id";
    private static final String COL_EMAIL = "email";
    private static final String COL_ROLE = "role";
    private static final String COL_PASSWORD = "password";
    private static final String COL_LAST_NAME = "lastName";
    private static final String COL_FIRST_NAME = "firstName";
    private static final String COL_PHONE_NUMBER = "phoneNumber";
    private static final String COL_PROFILE_PIC_PATH = "profilePicPath";
    private static final String COL_ADDRESS = "address";
    private static final String COL_GENDER = "gender";
    private static final String COL_COMPANY_NAME = "companyName";
    private static final String COL_MARKET_NAME = "marketName";
    private static final String COL_IMAGE = "image";
    private static final String COL_CREATED_AT = "createdAt";
    private static final String COL_IS_VERIFIED = "isVerified";
    private static final String COL_GOOGLE_ID = "googleId";
    private static final String COL_USER_TYPE = "userType";

    private final Connection cnx;

    public UserService() {
        cnx = MyDataBase.getInstance().getConnection();
    }
    @Override
    public User login(User t) throws SQLException {
       User u  =this.findByEmail(t.getEmail());
       if(u==null )
        return null;
      else {
           if (BCrypt.checkpw(t.getPassword(), u.getPassword())) {
               return u;
           }else{
               u.setId(0);
               return u;
           }
       }
    }


    @Override
    public List<User> getAllusers() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT id, email, role, password, lastName, firstName, phoneNumber, profilePicPath, address, gender, companyName, marketName, image, createdAt, isVerified, googleId, userType FROM user";

        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(req)) {

            while (resultSet.next()) {
                users.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all users", e);
            throw new SQLException("Error getting all users", e);
        }

        return users;
    }

    @Override
    public User findById(int id) throws SQLException {
        String req = "SELECT id, email, role, password, lastName, firstName, phoneNumber, profilePicPath, address, gender, companyName, marketName, image, createdAt, isVerified, googleId, userType FROM user WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return extractUserFromResultSet(resultSet);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding user by ID: " + id, e);
        }
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String req = "SELECT id, email, role, password, lastName, firstName, phoneNumber, profilePicPath, address, gender, companyName, marketName, image, createdAt, isVerified, googleId, userType FROM user WHERE email = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, email);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return extractUserFromResultSet(resultSet);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding user by email: " + email, e);
        }
    }


    /**
     * Méthode utilitaire pour extraire un utilisateur d'un ResultSet
     */
    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt(COL_ID));
        user.setEmail(resultSet.getString(COL_EMAIL));
        user.setRole(resultSet.getString(COL_ROLE));
        user.setPassword(resultSet.getString(COL_PASSWORD));
        user.setLastName(resultSet.getString(COL_LAST_NAME));
        user.setFirstName(resultSet.getString(COL_FIRST_NAME));
        user.setPhoneNumber(resultSet.getInt(COL_PHONE_NUMBER));
        user.setProfilePicPath(resultSet.getString(COL_PROFILE_PIC_PATH));
        user.setAdress(resultSet.getString(COL_ADDRESS));
        user.setGender(resultSet.getString(COL_GENDER));
        user.setCompanyName(resultSet.getString(COL_COMPANY_NAME));
        user.setMarketName(resultSet.getString(COL_MARKET_NAME));
        user.setImage(resultSet.getString(COL_IMAGE));

        // Convertir le timestamp en LocalDateTime
        Timestamp timestamp = resultSet.getTimestamp(COL_CREATED_AT);
        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        } else {
            user.setCreatedAt(LocalDateTime.now());
        }

        user.setVerified(resultSet.getInt(COL_IS_VERIFIED) == 1);
        user.setGoogleId(resultSet.getString(COL_GOOGLE_ID));
        user.setUserType(resultSet.getString(COL_USER_TYPE));

        return user;
    }



}
