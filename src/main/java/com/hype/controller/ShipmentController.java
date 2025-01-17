package com.hype.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.hype.dao.ShipmentDAO;
import com.hype.dto.OrderDTO;


@WebServlet("*.sh")
public class ShipmentController extends HttpServlet {
 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}
	
	protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html ; charset=utf-8");
		
		String uri = request.getRequestURI();
		System.out.println("요청 url : " + uri);
		
		if(uri.equals("/shipManage.sh")) { // 왼쪽 네브바에서 배송 정보 변경 클릭시 이 페이지로 이동: 실적적으로 shipManage.sh?curPage=1 임
			System.out.println("shipManage.sh 성공");
			ShipmentDAO dao = new ShipmentDAO();

			// 일단 무조건 curPage는 1로 받아짐 -> 그 후 페이지네이션 이동시 curPage 변경
			int curPage = Integer.parseInt(request.getParameter("curPage"));
			System.out.println(curPage);
			
			try {
				HashMap map = dao.getPageNavi(curPage);
				
//  			페이지네이션 prev, next 유무 판단 
//				System.out.println("현재 페이지 : " + curPage);
//				System.out.println("startNavi : " + map.get("startNavi"));
//				System.out.println("endNavi : " + map.get("endNavi"));
//				System.out.println("needPrev 요? " + map.get("needPrev"));
//				System.out.println("needNext 필요? " + map.get("needNext"));
				
				// tbl_order의 모든 데이터들을 가져오고 forward 해주는 작업
				ArrayList<OrderDTO> list = dao.selectAll(curPage*10-9, curPage*10);
				request.setAttribute("list", list);
				
				// 페이지네이션 HashMap도 forward 해줌
				request.setAttribute("naviMap", map);
				request.getRequestDispatcher("/admin/delivery/shipmentManage.jsp").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else if(uri.equals("/shipModify.sh")) {  // 배송 정보 페이지에서 수정 버튼(연필모양)을 눌렀을 때 이동 페이지
			
			// 배송 정보 페이지에서 수정 버튼(연필모양)을 눌렀을 때 해당 seq_order를 get으로 보낸걸 받아옴
			int seq_order = Integer.parseInt(request.getParameter("seq_order"));
			ShipmentDAO dao = new ShipmentDAO();
			
			try {
				
				// 해당 seq_order를 통해 해당 상품의 데이터를 dao를 통해 list로 받아옴
				ArrayList<OrderDTO> list = dao.selectAllBySeq(seq_order);
				request.setAttribute("list", list);
				
				// 배송 정보 수정페이지로 데이터 뿌리기 
				request.getRequestDispatcher("/admin/delivery/shipmentModify.jsp").forward(request, response);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(uri.equals("/shipmentModifyProc.sh")) { // 배송 정보 수정 페이지에서 수정완료 버튼을 눌렀을 때의 uri
			
			// 배송 정보 수정 페이지의 해당 seq_order를 가져옴
			int seq_order = Integer.parseInt(request.getParameter("seq_order"));
			String order_name = request.getParameter("order_name");
			
			// phone 번호가 나누어져 있어서 하나로 모으는 작업 
			String phone1= request.getParameter("phone1");
			String phone2= request.getParameter("phone2");
			String phone3= request.getParameter("phone3");
			String order_phone = phone1+phone2+phone3;
			
			String order_postCode = request.getParameter("postCode");
			
			// 주소를 하나로 합치는 작업 
			String roadAddr = request.getParameter("roadAddr");
			String jibunAddr = request.getParameter("jibunAddr");
			String detailAddr = request.getParameter("detailAddr");
			String order_address = roadAddr + " " + jibunAddr + " " + detailAddr;
			
			String order_status = request.getParameter("order_status");

			ShipmentDAO dao = new ShipmentDAO();
			try {
				int rs = dao.modify(new OrderDTO(seq_order, null, order_name,order_phone,order_postCode,order_address,null,order_status));
				if(rs > 0) {
					response.sendRedirect("/shipManage.sh"); // 배송 정보 페이지로 이동
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}else if(uri.equals("/searchProc.sh")) { // 배송 정보 페이지에서 검색했을 때의 uri
			String searchKeyword = request.getParameter("searchKeyword"); // 해당 키워드(배송 상태로 검색한 결과)
			ShipmentDAO dao = new ShipmentDAO();
			
			try {
				ArrayList<OrderDTO> list = dao.searchByTitle(searchKeyword);
				
				// ajax 이용
				Gson gson = new Gson();
				String rs = gson.toJson(list);
				System.out.println(rs);
				response.setCharacterEncoding("utf-8");
				response.getWriter().append(rs);	
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
}
