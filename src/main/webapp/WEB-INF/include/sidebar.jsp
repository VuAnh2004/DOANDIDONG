<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, model.bean.Menu"%>
<%
List<Menu> menus = (List<Menu>) request.getAttribute("menus");
if (menus == null) menus = new ArrayList<>();
menus.sort(Comparator.comparingInt(Menu::getMenuOrder));
String currentPath = request.getRequestURI();
%>

<aside id="sidebar" class="sidebar">
    <ul class="sidebar-nav" id="sidebar-nav">
        <% for (Menu menu : menus) {
            List<Menu> subMenus = menu.getSubMenus() != null ? menu.getSubMenus() : new ArrayList<>();
            subMenus.sort(Comparator.comparingInt(Menu::getMenuOrder));

            boolean isParentActive = menu.getControllerName() != null && !menu.getControllerName().equals("#") && currentPath.contains(menu.getControllerName());

            boolean hasActiveChild = false;
            for(Menu child : subMenus) {
                if(child.getControllerName() != null && currentPath.contains(child.getControllerName())) {
                    hasActiveChild = true;
                    break;
                }
            }
            
            boolean showExpanded = isParentActive || hasActiveChild;
        %>
        <li class="nav-item">
            <% if (!subMenus.isEmpty()) { %>
                <a class="nav-link <%= showExpanded ? "" : "collapsed" %> <%= hasActiveChild ? "parent-active" : "" %>" 
                   data-bs-toggle="collapse" 
                   data-bs-target="#collapse-<%=menu.getMenuID()%>" 
                   href="#"
                   aria-expanded="<%=showExpanded%>">
                    <i class="<%=menu.getIcon() != null ? menu.getIcon().trim() : "bi bi-grid"%>"></i>
                    <span><%=menu.getMenuName()%></span>
                    <i class="bi bi-chevron-down ms-auto"></i>
                </a>

                <ul id="collapse-<%=menu.getMenuID()%>" 
                    class="nav-content collapse <%= showExpanded ? "show" : ""%>" 
                    data-bs-parent="#sidebar-nav">
                    <% for (Menu child : subMenus) {
                        boolean isChildActive = child.getControllerName() != null && currentPath.contains(child.getControllerName());
                    %>
                    <li>
                        <a href="<%=request.getContextPath() + "/" + child.getControllerName()%>" 
                           class="<%=isChildActive ? "active" : ""%>">
                            <i class="<%=child.getIcon() != null ? child.getIcon().trim() : "bi bi-circle"%>"></i>
                            <span><%=child.getMenuName()%></span>
                        </a>
                    </li>
                    <% } %>
                </ul>
            <% } else { %>
<%
                    String href = menu.getMenuName().equals("Trang chủ") ? "index.jsp" : (menu.getControllerName() != null ? menu.getControllerName() : "#");
                    boolean isSingleActive = currentPath.contains(href) || (href.equals("index.jsp") && currentPath.endsWith("/"));
                %>
                <a class="nav-link <%= isSingleActive ? "active" : "collapsed" %>" href="<%=href%>">
                    <i class="<%=menu.getIcon() != null ? menu.getIcon().trim() : "bi bi-grid"%>"></i>
                    <span><%=menu.getMenuName()%></span>
                </a>
            <% } %>
        </li>
        <% } %>
    </ul>
</aside>

<style>
/* Sidebar Style */
.sidebar {
    width: 300px;
    background: #fff;
    padding: 20px;
}

.sidebar-nav {
    padding: 0;
    margin: 0;
    list-style: none;
}

.nav-item {
    margin-bottom: 5px;
}

/* Trạng thái mặc định (Chưa chọn) */
.sidebar-nav .nav-link.collapsed {
    background: transparent;
    color: #444;
}

/* Trạng thái SÁNG (Active) cho Menu cấp 1 */
.sidebar-nav .nav-link:not(.collapsed),
.sidebar-nav .nav-link.active {
    background: #f0f3ff; /* Màu xanh nhạt nhẹ */
    color: #4154f1;      /* Chữ xanh đậm */
    border-radius: 8px;
}

/* Menu cấp 1 khi có con đang active (nhưng nó đang đóng/mở) */
.parent-active {
    color: #4154f1 !important;
    font-weight: 600;
}

/* Menu cấp 1 Icon */
.sidebar-nav .nav-link i {
    font-size: 1.1rem;
    margin-right: 10px;
    color: inherit;
}

/* Menu cấp 2 (Submenu) */
.sidebar-nav .nav-content {
    padding: 5px 0 0 0;
    list-style: none;
}

.sidebar-nav .nav-content a {
    display: flex;
    align-items: center;
    font-size: 14px;
    padding: 10px 10px 10px 40px;
    transition: 0.3s;
    color: #444;
    text-decoration: none;
    border-radius: 8px;
}

/* Trạng thái SÁNG (Active) cho Menu cấp 2 */
.sidebar-nav .nav-content a.active {
    background: #f0f3ff;
    color: #4154f1;
    font-weight: 600;
}

.sidebar-nav .nav-content a i {
    font-size: 12px;
    margin-right: 8px;
}

.sidebar-nav .nav-content a:hover {
    color: #4154f1;
    background: #f6f9ff;
}
</style>