package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import logiikka.Kirja;

public class DBlukuvinkkiDAO implements LukuvinkkiInterface {
    
    private Database db;
    private List<Kirja> books = new ArrayList<>();
    private String sql1 = "SELECT * FROM kirja where user_username = ?";
    private String sql2 = "SELECT * FROM kirja where author = ?";
    private String sql3 = "SELECT * FROM kirja where title = ?";
    private Connection c = null;
    private ResultSet rs = null;
    private PreparedStatement stmt = null;
    
    /**
     * DBlukuvinkkiDAO class constructor. The database is initialized in the
     * constructor.
     * @param db database given as a parameter
     */
    public DBlukuvinkkiDAO(Database db) {
        this.db = db;
    }

    /**
     * @param kirja
     * @throws SQLException if saving the book object fails
     */
    @Override
    public boolean addBook(Kirja kirja) throws SQLException, Exception {
        c = db.connect();
        try {
            PreparedStatement statement = c.prepareStatement(
                    "INSERT OR REPLACE INTO books (title, author, pageCount) VALUES (?, ?, ?);"
            );

            statement.setString(1, kirja.getOtsikko());
            statement.setString(2, kirja.getKirjailija());
            statement.setInt(3, kirja.getSivut());
            statement.executeUpdate();
            statement.close();
            c.close();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true; //Väliaikainen
    }

    /**
     * @return returns a list of categories created by the given user
     * @throws SQLException when retrieving data from the database fails
     */
    @Override
    public List<Kirja> getAllBooks() throws SQLException {
        c = db.connect();
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT*FROM books");
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                Kirja kirja = new Kirja(rs.getString("title").trim(), rs.getString("author"), rs.getInt("pageCount"));
                books.add(kirja);
            }
            stmt.close();
            rs.close();
        } catch (Throwable t) {
        } finally {
            c.close();
        }
        return books;
    }

    /**
     * Retrieves all the books in the database
     * @param title
     * @return a book by title
     * @throws SQLException if retrieving data from the database fails
     *
     */
    @Override
    public Kirja findOne(String title) throws SQLException {
        c = db.connect();
        PreparedStatement stmt = c.prepareStatement("SELECT*FROM books WHERE title = ?");
        stmt.setString(1, title);
        rs = stmt.executeQuery();
        boolean findOne = rs.next();
        if (!findOne) {
            return null;
        } else {
            Kirja book = new Kirja(rs.getString("title"), rs.getString("author"), rs.getInt("pageCount"));
            stmt.close();
            rs.close();
            c.close();
            return book;
        }
    }
}