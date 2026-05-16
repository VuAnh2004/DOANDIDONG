package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import model.DAO.MenuDAO;
import model.DAO.impl.MenuDAOImpl;
import model.bean.Menu;

import java.io.IOException;
import java.util.List;

@WebFilter("/*")
public class UserMenuFilter implements Filter {
    private MenuDAO menuDAO;


    public void init(FilterConfig filterConfig) {
        menuDAO = new MenuDAOImpl();
    }


    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Lấy menu từ DB, lưu vào request
        List<Menu> menus = menuDAO.getUserMenus();
        request.setAttribute("menus", menus);

        chain.doFilter(request, response);
    }


    public void destroy() {}
}
