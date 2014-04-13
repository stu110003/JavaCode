package me.simpleproject.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/AskSomething")
public class AskSomethingServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Statement statement = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		long time = new java.util.Date().getTime();
		List<Object> recycleOnFinished = new ArrayList<Object>(); // 程式執行最後關閉資源

		try {
			// 註冊 Class
			// Class.forName("com.mysql.jdbc.Driver");
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			
			// 建立MsSQL連線
			// Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=demo;user=root;password=1234;");
			String Url = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS;databaseName=test;user=sa;password=root;";
			Connection connection = DriverManager.getConnection(Url);
			// 建立MySQL連線
			// Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=UTF-8","root", "");
			// recycleOnFinished.add(connection);

			// 末端新增一筆資料
			if (request.getParameter("add") != null) {
				pst = connection.prepareStatement("insert into events(date1, price, title, yon) values(?, ?, ?, ?)");
				recycleOnFinished.add(pst);
				pst.setDate(1, new Date(time));
				pst.setBigDecimal(2, new BigDecimal(time % 10));
				pst.setString(3, "new title" + (time % 10));
				pst.setBoolean(4, time % 2 == 0 ? true : false);
				pst.executeUpdate();
			}

			// 開頭刪除一筆資料
			if (request.getParameter("remove") != null) {
				statement = connection.createStatement();
				recycleOnFinished.add(statement);
				rs = statement.executeQuery("select min(id) as minid from events ");
				recycleOnFinished.add(rs);
				
				if (rs != null && rs.next()) {
					Long minid = rs.getLong("minid");
					pst = connection.prepareStatement("delete from events where id=? ");
					recycleOnFinished.add(pst);
					pst.setLong(1, minid);
					pst.executeUpdate();
				}
			}

			// 讀出資料庫所有資料
			statement = connection.createStatement();
			recycleOnFinished.add(statement);
			rs = statement.executeQuery(" select * from events order by id ");
			recycleOnFinished.add(rs);
			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("id", rs.getLong("id"));
				row.put("date", rs.getDate("date1"));
				row.put("price", rs.getBigDecimal("price"));
				row.put("title", rs.getString("title"));
				row.put("yon", rs.getString("yon"));
				results.add(row);
			}
			request.setAttribute("rows", results);
		} catch (Exception e) {
			request.setAttribute("errorMessage", "Insert or Query Exception :" + e.toString());
		} finally {
			
			// 關閉占用的資源
			try {
				for (Object resource : recycleOnFinished) {
					if (resource instanceof Statement) {
						((Statement) resource).close();
					} else if (resource instanceof ResultSet) {
						((ResultSet) resource).close();
					} else if (resource instanceof PreparedStatement) {
						((PreparedStatement) resource).close();
					} else if (resource instanceof Connection) {
						((Connection) resource).close();
					}
				}
			} catch (SQLException e) {
				request.setAttribute("errorMessage",
						"Close Exception :" + e.toString());
			}
		}

		// 導向result.jsp
		request.getRequestDispatcher("WEB-INF/pages/result.jsp").forward(request, response);
	}
}
