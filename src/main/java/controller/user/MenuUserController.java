package controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DAO.MenuDAO;
import model.DAO.impl.MenuDAOImpl;
import model.bean.Menu;

import java.io.IOException;
import java.util.List;

@WebServlet("/menu")
public class MenuUserController extends HttpServlet {
	private MenuDAO menuDAO = new MenuDAOImpl();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Menu> menus = menuDAO.getUserMenus();
		request.setAttribute("menus", menus);
		request.getRequestDispatcher("/WEB-INF/include/sidebar.jsp").forward(request, response);
	}

}
