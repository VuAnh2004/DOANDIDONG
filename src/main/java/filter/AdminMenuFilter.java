
package filter;

import model.DAO.AdminMenuDAO;
import model.DAO.impl.AdminMenuDAOImpl; // Giả định import này là đúng
import model.bean.AdminMenu;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@WebFilter("/admin/*")
public class AdminMenuFilter implements Filter {

    private AdminMenuDAO menuDAO = new AdminMenuDAOImpl();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        List<AdminMenu> menus = menuDAO.getActiveMenus(); 
        
        request.setAttribute("adminMenus", menus);

        chain.doFilter(req, res);
    }
}