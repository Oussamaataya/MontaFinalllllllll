package Services;

import java.sql.SQLException;
import java.util.List;

public interface UserServiceInterface <entity>{
    public entity login(entity t) throws SQLException;
    public List<entity> getAllusers() throws SQLException;
    public entity findById(int id) throws SQLException;
    public entity findByEmail(String email) throws SQLException;
}
