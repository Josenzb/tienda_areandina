package com.areandina.mysql;

import com.areandina.DataBase;

import java.sql.*;

import static com.areandina.mysql.MySqlConstants.CONNECTION_STRING;
import static com.areandina.mysql.MySqlConstants.MY_SQL_JDBC_DRIVER;

public class MySqlOperations implements DataBase {
    private Connection connection= null;
    private Statement statement= null;
    private ResultSet resultSet= null;
    private String sqlStatement;
    private String server;
    private String dataBaseName;
    private String user;
    private String password;

    public String getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void configureDataBaseConnection() {
        try {
            Class.forName(MY_SQL_JDBC_DRIVER);
            connection= DriverManager.getConnection(
                    String.format(CONNECTION_STRING,
                            this.server,
                            this.dataBaseName,
                            this.user,
                            this.password)
            );
            statement=connection.createStatement();
        }catch (Exception e){
            close();
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void executeSqlStatement() {
        try {
            configureDataBaseConnection();
            resultSet = statement.executeQuery(sqlStatement);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public int getColumnInt(int columnIndex) throws SQLException {
        if (resultSet != null && resultSet.next()) {
            return resultSet.getInt(columnIndex);
        } else {
            throw new SQLException("No se pudo obtener el valor del ID generado.");
        }
    }


    @Override
    public ResultSet getResulset() {
        return resultSet;
    }

    @Override
    public void close() {
        try{
            if(resultSet !=null){
                resultSet.close();
            }
            if(statement !=null){
                statement.close();
            }
            if(connection !=null){
                connection.close();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void printResultset() throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int totalColumnNumber = resultSetMetaData.getColumnCount();
        while(resultSet.next()) {
            for (int columnNumber =1; columnNumber <= totalColumnNumber; columnNumber++){
                if(columnNumber > 1){
                    System.out.print(",\t");
                }
                String columnValue = resultSet.getString(columnNumber);
                System.out.print(resultSetMetaData.getColumnName(columnNumber)+ ": "+ columnValue);
            }
            System.out.println("");
        }
    }

    @Override
    public void executeSqlStatementvoid() {
        try{
            configureDataBaseConnection();
            statement.execute(sqlStatement);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}

