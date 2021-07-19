package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<String> getAllCities(){
		String sql = "SELECT DISTINCT city "
				+ "FROM business "
				+ "order BY city ";
		
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				String citta = res.getString("city");
				result.add(citta);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void calcolaVertici(int anno, String citta, Map<String, Business> idMap) {
		String sql = "SELECT * "
				+ "FROM business b, reviews r "
				+ "WHERE r.business_id=b.business_id "
				+ "AND YEAR(r.review_date) = ? "
				+ "AND b.city = ? ";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setString(2, citta);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				
				idMap.put(res.getString("business_id"), business);
				
			}
			res.close();
			st.close();
			conn.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<Adiacenza> calcolaArchi(int anno, String citta, Map<String, Business> idMap){
		String sql = "SELECT b1.id as b1, b2.id AS b2, (b2.media-b1.media) AS differenza "
				+ "FROM (SELECT b.business_id AS id, AVG(r.stars) AS media "
				+ "		FROM reviews r, business b "
				+ "		WHERE b.business_id=r.business_id AND YEAR(r.review_date) = ? AND b.city = ? "
				+ "		GROUP BY b.business_id) AS b1, "
				+ "		(SELECT b.business_id AS id, AVG(r.stars) AS media "
				+ "		FROM reviews r, business b "
				+ "		WHERE b.business_id=r.business_id AND YEAR(r.review_date) = ? AND b.city = ? "
				+ "		GROUP BY b.business_id) AS b2 "
				+ "WHERE b2.id <> b1.id "
				+ "GROUP BY b1.id, b2.id "
				+ "HAVING differenza > 0 ";
		
		
		Connection conn = DBConnect.getConnection();
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setString(2, citta);
			st.setInt(3, anno);
			st.setString(4, citta);
			
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Business b1 = idMap.get(res.getString("b1"));
				Business b2 = idMap.get(res.getString("b2"));
				Adiacenza a = new Adiacenza(b1, b2, res.getDouble("differenza"));
				result.add(a);
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
}
