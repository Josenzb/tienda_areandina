package com.areandina;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataBase {
    public void configureDataBaseConnection();
    public void executeSqlStatement();
    public ResultSet getResulset();
    public void close();
    public void printResultset() throws SQLException;
    public void executeSqlStatementvoid();

}