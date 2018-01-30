package application;

import java.sql.*;

import application.Helper.SQLLiteConnection;
public class DoctorLoginModel {
	Connection connection;
	public DoctorLoginModel() {
		connection = SQLLiteConnection.Connecter();
		if(connection==null) {
		System.exit(1);	
		}
	}
	
	public boolean isDBConnected() {
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	} 
	
	public boolean isLogin(String user, String pass) throws SQLException {
		PreparedStatement preparedStatement= null;
		ResultSet resultSet= null;
		String query = "select * from Login_Doctor where Username = ? and Password = ?";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pass);
			
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				return true;
			}else {
				return false;
			}
		}catch(Exception e) {
			return false;
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
	}
}

