package com.hype.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.hype.dto.OrderDTO;


public class ShipmentDAO {
	private BasicDataSource bds;

	public ShipmentDAO() {
		try {
			Context iCtx = new InitialContext();
			Context envCtx = (Context)iCtx.lookup("java:comp/env");
			bds = (BasicDataSource)envCtx.lookup("jdbc/bds");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 배송 정보 화면 보여주기 
	public ArrayList<OrderDTO> selectAll(int start, int end) throws Exception {
		String sql = "select * from (select tbl_order.*, row_number() over(order by seq_order desc) as num from tbl_order)"
				+ " where num between ? and ?";
		
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)) {
			
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);

			ResultSet rs = pstmt.executeQuery();
			ArrayList<OrderDTO> list = new ArrayList<>();
			
			while(rs.next()) {
				int seq_order = rs.getInt("seq_order");
				String user_id = rs.getString("user_id");
				String order_name = rs.getString("order_name");
				String order_phone = rs.getString("order_phone");
				String order_postCode = rs.getString("order_postCode");
				String order_address = rs.getString("order_address");
				String order_msg = rs.getString("order_msg");
				String order_status = rs.getString("order_status");
				list.add(new OrderDTO(seq_order,user_id,order_name,order_phone,order_postCode,order_address,order_msg,order_status));
			}
			return list;
		}
	}
	
	// 배송 정보 수정페이지에 뿌려줄 데이터들  
	public ArrayList<OrderDTO> selectAllBySeq(int seq_order) throws Exception {
		String sql = "select * from tbl_order where seq_order = ?";
		
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)) {
			
			pstmt.setInt(1, seq_order);
			
			ResultSet rs = pstmt.executeQuery();
			ArrayList<OrderDTO> list = new ArrayList<>();
			
			while(rs.next()) {
				String user_id = rs.getString("user_id");
				String order_name = rs.getString("order_name");
				String order_phone = rs.getString("order_phone");
				String order_postCode = rs.getString("order_postCode");
				String order_address = rs.getString("order_address");
				String order_msg = rs.getString("order_msg");
				String order_status = rs.getString("order_status");
				list.add(new OrderDTO(seq_order,user_id,order_name,order_phone,order_postCode,order_address,order_msg,order_status));
			}
			return list;
		}
	}
	
	// 배송 정보 데이터 수정
	public int modify(OrderDTO dto) throws Exception{
		String sql = "update tbl_order set order_name = ?, order_phone = ?, order_postCode = ?, order_address = ?, order_status = ? where seq_order=?";
		
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){

			pstmt.setString(1, dto.getOrder_name());
			pstmt.setString(2, dto.getOrder_phone());
			pstmt.setString(3, dto.getOrder_postCode());
			pstmt.setString(4, dto.getOrder_address());
			pstmt.setString(5, dto.getOrder_status());
			pstmt.setInt(6, dto.getSeq_order());
			int rs = pstmt.executeUpdate();
			return rs;
		}
	}
	
	// 배송 정보 검색어로 찾기 
	public ArrayList<OrderDTO> searchByTitle(String searchKeyword) throws Exception{
		String sql = "select * from tbl_order where order_status like '%'||?||'%' order by 1 desc";
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){

			pstmt.setString(1, searchKeyword);
			
			ResultSet rs = pstmt.executeQuery();
			ArrayList<OrderDTO> list = new ArrayList<>();
			while(rs.next()) {
				int seq_order = rs.getInt("seq_order");
				String user_id = rs.getString("user_id");
				String order_name = rs.getString("order_name");
				String order_phone = rs.getString("order_phone");
				String order_postCode = rs.getString("order_postCode");
				String order_address = rs.getString("order_address");
				String order_msg = rs.getString("order_msg");
				String order_status = rs.getString("order_status");
				list.add(new OrderDTO(seq_order,user_id,order_name,order_phone,order_postCode,order_address,order_msg,order_status));
			}
			return list;
		}
	}
	

	// 페이지네이션 Map
	public HashMap<String, Object> getPageNavi(int curPage) throws Exception{
		String sql = "select count(*) as totalCnt from tbl_order";
		
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){
			
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			
			int totalCnt = rs.getInt("totalCnt"); 
			int recordCntPerPage = 10; 
			int naviCntPerPage = 5; 
			int pageTotalCnt = 0; 
			
			
			if(totalCnt % recordCntPerPage > 0) { 
				pageTotalCnt = totalCnt / recordCntPerPage + 1;
			}else {
				pageTotalCnt = totalCnt / recordCntPerPage;
			}
			
			if(curPage < 1) { 
				curPage = 1; 
			}else if(curPage > pageTotalCnt) { 
				curPage = pageTotalCnt; 
			}					
			
			
			int startNavi = ((curPage-1) / naviCntPerPage) * naviCntPerPage + 1;
			int endNavi = startNavi + naviCntPerPage - 1;
			
			
			if(pageTotalCnt < endNavi) { 
				endNavi = pageTotalCnt; 
			}
			
			
			boolean needPrev = true; 
			boolean needNext = true; 
			
			if(startNavi == 1) {
				needPrev = false;
			}
			if(endNavi == pageTotalCnt) {
				needNext = false;
			}
			
			HashMap<String, Object> map = new HashMap<>();
			map.put("startNavi", startNavi);
			map.put("endNavi", endNavi);
			map.put("needPrev", needPrev);
			map.put("needNext", needNext);
			
			return map;
		}
	}
	

	
}
