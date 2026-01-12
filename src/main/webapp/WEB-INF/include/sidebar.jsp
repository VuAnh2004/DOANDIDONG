<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*, model.bean.Menu"%>
<%
List<Menu> menus = (List<Menu>) request.getAttribute("menus");
if (menus == null)
    menus = new ArrayList<>();
menus.sort(Comparator.comparingInt(Menu::getMenuOrder));
String currentPath = request.getRequestURI();
%>

<aside id="sidebar" class="sidebar">
    <ul class="sidebar-nav" id="sidebar-nav">
        <% for (Menu menu : menus) {
            List<Menu> subMenus = menu.getSubMenus() != null ? menu.getSubMenus() : new ArrayList<>();
            subMenus.sort(Comparator.comparingInt(Menu::getMenuOrder));
            boolean isActive = menu.getControllerName() != null && currentPath.contains(menu.getControllerName());
        %>
        <li class="nav-item <%=isActive ? "active" : ""%>">
            <% if (!subMenus.isEmpty()) { %>
                <!-- Menu cấp 1 có submenu -->
                <a class="nav-link collapsed" 
                   data-bs-toggle="collapse" 
                   data-bs-target="#collapse-<%=menu.getMenuID()%>" 
                   href="#"
                   aria-expanded="<%=isActive%>">
                    <i class="<%=menu.getIcon() != null ? menu.getIcon().trim() : "fas fa-circle"%>"></i>
                    <span><%=menu.getMenuName()%></span>
                    <i class="bi bi-chevron-down ms-auto"></i>
                </a>

                <ul id="collapse-<%=menu.getMenuID()%>" 
                    class="nav-content collapse <%=isActive ? "show" : ""%>" 
                    data-bs-parent="#sidebar-nav">
                    <% for (Menu child : subMenus) {
                        boolean isChildActive = child.getControllerName() != null && currentPath.contains(child.getControllerName());
                    %>
                    <li class="<%=isChildActive ? "active" : ""%>">
                        <a href="<%=child.getControllerName() != null ? child.getControllerName() : "#" %>">
                            <i class="<%=child.getIcon() != null ? child.getIcon().trim() : "fas fa-circle"%>"></i>
                            <span><%=child.getMenuName()%></span>
                        </a>
                    </li>
                    <% } %>
                </ul>
            <% } else { %>
                <!-- Menu cấp 1 không có submenu -->
                <%
                    // Chỉ menu "Trang chủ" dẫn index.jsp
                    String href = menu.getMenuName().equals("Trang chủ") ? "index.jsp" : (menu.getControllerName() != null ? menu.getControllerName() : "#");
                %>
                <a class="nav-link" href="<%=href%>">
                    <i class="<%=menu.getIcon() != null ? menu.getIcon().trim() : "fas fa-circle"%>"></i>
                    <span><%=menu.getMenuName()%></span>
                </a>
            <% } %>
        </li>
        <% } %>
    </ul>
</aside>

<style>
/* Menu cấp 2 (submenu) */
.sidebar-nav .nav-content li a {
    display: flex;
    align-items: center;
    font-size: 15px;      /* chữ submenu 15px */
    padding-left: 15px;   /* đẩy submenu sang phải */
}

/* Icon submenu */
.sidebar-nav .nav-content li a i {
    margin-right: 10px;   /* khoảng cách icon và chữ */
    font-size: 15px;      /* cỡ icon submenu 15px */
}

/* Icon và chữ menu cấp 1 */
.sidebar-nav > li > a i {
    margin-right: 8px;    /* icon menu cấp 1 */
}

/* Active menu highlight */
.sidebar-nav li.active > a {
    background-color: #e0e0e0;  /* màu nền active */
    color: #000;
}
</style>
