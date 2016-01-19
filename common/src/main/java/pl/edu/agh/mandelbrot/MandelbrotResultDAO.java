package pl.edu.agh.mandelbrot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MandelbrotResultDAO {
    private final Connection connection;
    public static final String CREATE_MANDELBROT_GENERATION_RESULT_TABLE_SQL =
            "CREATE TABLE MANDELBROT_GENERATION_RESULT (" +
            "result_id SERIAL PRIMARY KEY," +
            "tag INT," +
            "width INT," +
            "height INT," +
            "top REAL," +
            "right_ REAL," +
            "bottom REAL," +
            "left_ REAL," +
            "precision_ INT," +
            "ended BOOLEAN," +
            "time_ INT," +
            "submit_time BIGINT," +
            "total_time INT," +
            "image TEXT" +
            ");";
    public static final String DROP_MANDELBROT_GENERATION_RESULT_TABLE_SQL = "DROP TABLE IF EXISTS MANDELBROT_GENERATION_RESULT";
    public static final String INSERT_NEW_MANDELBROT_GENERATION_RESULT_SQL =
            "INSERT INTO MANDELBROT_GENERATION_RESULT(tag, width, height, top, right_, " +
                    "bottom, left_, precision_, ended, submit_time)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSE, ? ) " +
                    "RETURNING result_id;";
    public static final String UPDATE_MANDELBROT_GENERATION_RESULT_SQL =
            "UPDATE MANDELBROT_GENERATION_RESULT " +
                    "SET time_ = ?," +
                    "total_time = ?," +
                    "ended = TRUE," +
                    "image = ? " +
                    "WHERE result_id = ? AND tag = ?";
    public static final String SELECT_IF_MANDELBROT_GENERATION_HAS_ENDED_SQL =
            "SELECT COUNT(*) FROM MANDELBROT_GENERATION_RESULT WHERE tag = ? AND NOT ended;";
    public static final String SELECT_MANDELBROT_GENERATION_RESULT_SQL =
            "SELECT * FROM MANDELBROT_GENERATION_RESULT WHERE tag = ? ORDER BY result_id DESC;";

    public MandelbrotResultDAO(Connection connection) {
        this.connection = connection;
    }

    public static MandelbrotResultDAO createDAO(Connection connection) throws SQLException {
        createMandelbrotGenerationResultTable(connection);
        return new MandelbrotResultDAO(connection);
    }

    public int insertGenerationRequest(int tag, long submitTime, int width, int height, double top, double right,
                                       double bottom, double left, int precision) throws SQLException {
        PreparedStatement stmt =
                connection.prepareStatement(INSERT_NEW_MANDELBROT_GENERATION_RESULT_SQL);
        stmt.setInt(1, tag);
        stmt.setInt(2, width);
        stmt.setInt(3, height);
        stmt.setDouble(4, top);
        stmt.setDouble(5, right);
        stmt.setDouble(6, bottom);
        stmt.setDouble(7, left);
        stmt.setDouble(8, precision);
        stmt.setLong(9, submitTime);
        ResultSet lastGenerationIdSet = stmt.executeQuery();
        lastGenerationIdSet.next();
        int id = lastGenerationIdSet.getInt(1);
        stmt.close();
        return id;
    }

    public void insertGenerationResult(int id, int tag, int time, int totalTime, String image) throws SQLException {
        PreparedStatement stmt =
                connection.prepareStatement(UPDATE_MANDELBROT_GENERATION_RESULT_SQL);
        stmt.setInt(1, time);
        stmt.setInt(2, totalTime);
        stmt.setString(3, image);
        stmt.setInt(4, id);
        stmt.setInt(5, tag);
        stmt.execute();
        stmt.close();
    }

    public boolean checkIfGenerationHasEnded(int tag) throws SQLException {
        PreparedStatement stmt =
                connection.prepareStatement(SELECT_IF_MANDELBROT_GENERATION_HAS_ENDED_SQL);
        stmt.setInt(1, tag);
        ResultSet resultSet = stmt.executeQuery();
        resultSet.next();
        boolean hasEnded = resultSet.getInt(1) == 0;
        stmt.close();
        return hasEnded;
    }

    public List<MandelbrotGenerationResult> getGenerationResult(int tag) throws SQLException {
        List<MandelbrotGenerationResult> results = new ArrayList<>();

        PreparedStatement stmt =
                connection.prepareStatement(SELECT_MANDELBROT_GENERATION_RESULT_SQL);
        stmt.setInt(1, tag);
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
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
            int totalTime = resultSet.getInt("total_time");
            String genImage = resultSet.getString("image");

            MandelbrotGenerationResult result = new MandelbrotGenerationResult(genId, tag, genWidth, genHeight, genTop, genRight, genBottom,
                    genLeft, genPrecision, genEnded, totalTime, genTime, genImage);

            results.add(result);
        }

        return results;
    }

    private static void createMandelbrotGenerationResultTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(DROP_MANDELBROT_GENERATION_RESULT_TABLE_SQL);
        statement.executeUpdate(CREATE_MANDELBROT_GENERATION_RESULT_TABLE_SQL);
        statement.close();
    }
}
