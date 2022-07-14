package com.example.SearchEngine.SiteParse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@PropertySource("classpath:application.properties")
public class DBWriter {
    private static Connection connection;
    private static final String database = "search_engine";
    private static final String user = "root";
    private static final String pass = "trs.20041711";

    private static final StringBuilder insertQuery = new StringBuilder();

    @Value("${spring.application.url}")
    private String url;

    public static void connect()
    {
        if(connection == null)
        {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + database +
                                "?user=" + user + "&password=" + pass);
                createPage();
                createField();
                createLemma();
                createIndex();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createPage() throws SQLException {
        connection.createStatement().execute("DROP TABLE IF EXISTS page");
        connection.createStatement().execute("CREATE TABLE page(" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "path TEXT NOT NULL, " +
                "code INT NOT NULL, " +
                "content MEDIUMTEXT NOT NULL, " +
                "PRIMARY KEY(id), KEY(path (50)))");
    }

    private static void createField() throws SQLException {
        connection.createStatement().execute("DROP TABLE IF EXISTS field");
        connection.createStatement().execute("CREATE TABLE field(" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "name VARCHAR(255) NOT NULL, " +
                "selector VARCHAR(255) NOT NULL, " +
                "weight FLOAT NOT NULL, " +
                "PRIMARY KEY(id))");
        connection.createStatement().execute("INSERT INTO field(name, selector, weight) VALUES" +
                "('title', 'title', 1.0)," +
                "('body', 'body', 0.8)");
    }

    private static void createLemma() throws SQLException {
        connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
        connection.createStatement().execute("CREATE TABLE lemma(" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "lemma VARCHAR(255) NOT NULL, " +
                "frequency INT NOT NULL, " +
                "PRIMARY KEY(id))");
    }

    private static void createIndex() throws SQLException {
        connection.createStatement().execute("DROP TABLE IF EXISTS lindex");
        connection.createStatement().execute("CREATE TABLE lindex(" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "page_id INT NOT NULL, " +
                "lemma_id INT NOT NULL, " +
                "rang FLOAT NOT NULL, " +
                "PRIMARY KEY(id))");
    }

    public static void addPage(String url, int code, String body){
        body = body.replace("'", "\\'");
        if (insertQuery.length() + ("('" + url + "', '" + code + "', '" + body + "')").length()  > 2500000){
            multiInsertToPage();
        }
        try {
            insertQuery.append((insertQuery.length() == 0 ? "" : ",") +
                    "('" + url + "', '" + code + "', '" + body + "')");
        }catch (OutOfMemoryError e){
            System.out.println(insertQuery.length());
        }
    }

    public static void multiInsertToPage() {
        String sql = "INSERT INTO page(path, code, content) " +
                "VALUES" + insertQuery;
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        insertQuery.delete(0, insertQuery.length());
    }

    public static void getAllPage(){
        String sql = "";
    }
}
