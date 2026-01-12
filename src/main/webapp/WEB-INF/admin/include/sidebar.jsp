<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.bean.AdminMenu"%>
<%@ page import="java.io.Writer"%>
<%@ page import="java.util.List"%>

<%! 
private void renderMenu(List<AdminMenu> menuList, Writer out) throws java.io.IOException {
    if (menuList == null || menuList.isEmpty()) return;

    for (AdminMenu m : menuList) {
        boolean hasSub = m.getSubMenus() != null && !m.getSubMenus().isEmpty();

        out.write("<li class='nav-item'>");

        if (hasSub) {
            out.write("<a class='nav-link collapsed' data-bs-target='#" + m.getIdName()
                    + "' data-bs-toggle='collapse' href='#'>");
            out.write("<i class='" + m.getIcon() + "'></i><span>" + m.getItemName()
                    + "</span><i class='bi bi-chevron-down ms-auto'></i></a>");

            String parentAttr = (m.getParentLevel() == 0) ? " data-bs-parent='#sidebar-nav'" : "";
            out.write("<ul id='" + m.getIdName() + "' class='nav-content collapse'" + parentAttr + ">");

            renderMenu(m.getSubMenus(), out);
            out.write("</ul>");
        } else {
 
            out.write("<a class='nav-link collapsed' href='" + m.getItemTarget() + "'>");
            out.write("<i class='" + m.getIcon() + "'></i><span>" + m.getItemName() + "</span></a>");
        }

        out.write("</li>");
    }
}%>

<aside id="sidebar" class="sidebar">
	<ul class="sidebar-nav" id="sidebar-nav">
		<%
            // Lấy danh sách menu từ request attribute
            List<AdminMenu> menus = (List<AdminMenu>) request.getAttribute("menus");
            if (menus != null && !menus.isEmpty()) {
                renderMenu(menus, out);
            }
        %>
	</ul>
</aside>
