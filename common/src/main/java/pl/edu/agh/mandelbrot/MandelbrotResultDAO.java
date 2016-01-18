package pl.edu.agh.mandelbrot;

import java.sql.*;

public class MandelbrotResultDAO {
    private final Connection connection;
    public static final String CREATE_MANGELBROT_GENERATION_RESULT_TABLE_SQL =
            "CREATE TABLE MANGELBROT_GENERATION_RESULT (" +
            "result_id SERIAL PRIMARY KEY," +
            "width INT," +
            "height INT," +
            "top REAL," +
            "right_ REAL," +
            "bottom REAL," +
            "left_ REAL," +
            "precision_ INT," +
            "ended BOOLEAN," +
            "time_ INT," +
            "image TEXT" +
            ");";
    public static final String DROP_MANGELBROT_GENERATION_RESULT_TABLE_SQL = "DROP TABLE IF EXISTS MANGELBROT_GENERATION_RESULT";
    public static final String INSERT_NEW_MANGELBROT_GENERATION_RESULT_SQL =
            "INSERT INTO MANGELBROT_GENERATION_RESULT(width, height, top, right_, " +
                    "bottom, left_, precision_, ended)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, FALSE ) " +
                    "RETURNING result_id;";
    public static final String UPDATE_MANGELBROT_GENERATION_RESULT_SQL =
            "UPDATE MANGELBROT_GENERATION_RESULT " +
                    "SET time_ = ?," +
                    "ended = TRUE," +
                    "image = ? " +
                    "WHERE result_id = ?";
    public static final String SELECT_IF_MANGELBROT_GENERATION_HAS_ENDED_SQL =
            "SELECT ended FROM MANGELBROT_GENERATION_RESULT WHERE result_id = ?;";
    public static final String SELECT_MANGELBROT_GENERATION_RESULT_SQL =
            "SELECT * FROM MANGELBROT_GENERATION_RESULT WHERE result_id = ?;";

    public MandelbrotResultDAO(Connection connection) {
        this.connection = connection;
    }

    public static MandelbrotResultDAO createDAO(Connection connection) throws SQLException {
        createMandelbrotGenerationResultTable(connection);
        return new MandelbrotResultDAO(connection);
    }

    public int insertGenerationRequest(int width, int height, double top, double right,
                                       double bottom, double left, int precision) throws SQLException {
        PreparedStatement stmt =
                connection.prepareStatement(INSERT_NEW_MANGELBROT_GENERATION_RESULT_SQL);
        stmt.setInt(1, width);
        stmt.setInt(2, height);
        stmt.setDouble(3, top);
        stmt.setDouble(4, right);
        stmt.setDouble(5, bottom);
        stmt.setDouble(6, left);
        stmt.setDouble(7, precision);
        ResultSet lastGenerationIdSet = stmt.executeQuery();
        lastGenerationIdSet.next();
        int id = lastGenerationIdSet.getInt(1);
        stmt.close();
        return id;
    }

    public void insertGenerationResult(int id, int time, String image) throws SQLException {
        PreparedStatement stmt =
                connection.prepareStatement(UPDATE_MANGELBROT_GENERATION_RESULT_SQL);
        stmt.setInt(1, time);
        stmt.setString(2, image);
        stmt.setInt(3, id);
        stmt.execute();
        stmt.close();
    }

    public boolean checkIfGenerationHasEnded(int id) throws SQLException {
        PreparedStatement stmt =
                connection.prepareStatement(SELECT_IF_MANGELBROT_GENERATION_HAS_ENDED_SQL);
        stmt.setInt(1, id);
        ResultSet resultSet = stmt.executeQuery();
        resultSet.next();
        boolean hasEnded = resultSet.getBoolean(1);
        stmt.close();
        return hasEnded;
    }

    public MandelbrotGenerationResult getGenerationResult(int id) throws SQLException {
        PreparedStatement stmt =
                connection.prepareStatement(SELECT_MANGELBROT_GENERATION_RESULT_SQL);
        stmt.setInt(1, id);
        ResultSet resultSet = stmt.executeQuery();
        resultSet.next();

        int genId = resultSet.getInt("result_id");
        int genWidth = resultSet.getInt("width");
        int genHeight = resultSet.getInt("height");
        double genTop = resultSet.getDouble("top");
        double genRight = resultSet.getDouble("right_");
        double genBottom = resultSet.getDouble("bottom");
        double genLeft = resultSet.getDouble("left_");
        int genPrecision = resultSet.getInt("precision_");
        boolean genEnded = resultSet.getBoolean("ended");
        int genTime = resultSet.getInt("time_");
        String genImage = resultSet.getString("image");

        return new MandelbrotGenerationResult(genId, genWidth, genHeight, genTop, genRight, genBottom,
                genLeft, genPrecision, genEnded, genTime, genImage);
    }

    private static void createMandelbrotGenerationResultTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(DROP_MANGELBROT_GENERATION_RESULT_TABLE_SQL);
        statement.executeUpdate(CREATE_MANGELBROT_GENERATION_RESULT_TABLE_SQL);
        statement.close();
    }
}
