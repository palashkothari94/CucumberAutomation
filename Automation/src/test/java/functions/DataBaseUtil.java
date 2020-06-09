package functions;

/* Purpose		:Database related util class.
 * Developed By	: Brij
 * Modified By	:
 * Modified Date:
 * Reviewed By	:
 * Reviewed Date:
 */

import org.apache.log4j.Level;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseUtil extends Utility{

    // Database
    private Connection dbConnection;
    private Statement dbStatement;
    private ResultSet dbResultSet;

    public DataBaseUtil() {
        //Default constructor
    }


    public void EstablishConnection(String serverName, String dbName, String username, String password) throws Exception {
        String dbUrl = null;

        try {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException e) {
                Environment.loger.log(Level.ERROR, "ClassNotFoundException occured! Check with the dependencies");
            }
            // Create Connection to DB
            dbUrl = "jdbc:sqlserver://"+serverName+";databaseName="+dbName+";user="+username+";password="+password+";";
            dbConnection = DriverManager.getConnection(dbUrl);
            Environment.loger.log(Level.INFO, "Connection successfully established with DB!");
            // Create Statement Object
            dbStatement = dbConnection.createStatement();
        } catch (SQLSyntaxErrorException e) {
            Environment.loger.log(Level.ERROR, "Syntax error! Pass the proper SQL query");
        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured" + e.getMessage());
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            throw e;
        }
    }

    public List<String> GetColumnValues(String query, String ColumnName) {
        List<String> ReturnText = new ArrayList<>();
        try {
            dbResultSet = ExecuteQuery(query);
            while (dbResultSet.next()) {
                ReturnText.add(dbResultSet.getString(ColumnName));
            }
        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured" + e.getMessage());
        }
        return ReturnText;
    }

    public boolean RecordExists(String queryWithCount) {
        boolean exists;
        try {
            dbResultSet = ExecuteQuery(queryWithCount);
            if (dbResultSet.next()) {
                exists = dbResultSet.getBoolean(1);
                if (exists)
                    Environment.loger.log(Level.INFO, "Record exists in DB");
                else
                    Environment.loger.log(Level.INFO, "Record doesn't exists in DB");
                return exists;
            }

        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured" + e.getMessage());
        }
        return false;
    }

    public List<String> GetColumnValues(String query, int ColumnIndex) {
        List<String> ReturnText = new ArrayList<>();
        try {
            dbResultSet = ExecuteQuery(query);
            while (dbResultSet.next()) {
                ReturnText.add(dbResultSet.getString(ColumnIndex));
            }
        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured" + e.getMessage());
        }
        return ReturnText;
    }

    private ResultSet ExecuteQuery(String query) {
        try {
            dbResultSet = dbStatement.executeQuery(query);
        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured" + e.getMessage());
        }
        return dbResultSet;
    }

    public ResultSet ExecuteMultipleQuery(String query) {
        try {
            Statement statement = dbConnection.createStatement();
            dbResultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured", e);
        }
        return dbResultSet;
    }

    public void UpdateQuery(String query) {
        try {
            int ReturnValue = dbStatement.executeUpdate(query);
            if (ReturnValue == 1) {
                Environment.loger.log(Level.INFO, "Query Update Successfully:" + query);
            } else {
                Environment.loger.log(Level.ERROR, "Update not success" + query);
            }
            // dbConnection.commit();
        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured" + e.getMessage());
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
    }

    public void AddBatch(String Query) {
        try {
            dbStatement.addBatch(Query);
        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured" + e.getMessage());
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
    }

    public boolean ExecuteBatch() throws SQLException {
        boolean returnValue = false;
        dbStatement.executeBatch();
        returnValue = true;
        return returnValue;
    }

    public int CallProcedure(String procedure) {// TODO
        CallableStatement cstmt = null;
        int sReturn = -1;
        try {
            cstmt = dbConnection.prepareCall(procedure);
            // cstmt.setString(1, "1965");
            sReturn = cstmt.executeUpdate();
            // cstmt.getString(0);
        } catch (SQLException e) {
            Environment.loger.log(Level.ERROR, "SQLException Exception occured" + e.getMessage());
        } finally {
            try {
                cstmt.close();
            } catch (SQLException e) {
                Environment.loger.log(Level.ERROR, "Unable to close the callprocedure" + e.getMessage());
            }
        }
        return sReturn;
    }

    public void CloseDBConnection() {
        try {
            if (dbConnection != null)
                dbConnection.close();
            if (dbStatement != null)
                dbStatement.close();
            if (dbResultSet != null)
                dbResultSet.close();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured" + e);
        }
    }
}
