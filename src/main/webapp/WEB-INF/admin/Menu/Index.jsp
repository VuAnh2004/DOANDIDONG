<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.Menu"%>

<%
// Lấy danh sách menu đã được phân cấp từ Controller
List<Menu> listMenu = (List<Menu>) request.getAttribute("listMenu");
%>
<main id="main" class="main">
	<div class="container-fluid px-4">
		<div class="d-flex justify-content-between align-items-center">
			<h2 class="mt-4">Danh sách Menu</h2>
			<a href="<%=request.getContextPath()%>/admin/Menu/Create"
				class="btn btn-success mt-4"> Thêm Menu </a>
		</div>

		<div class="card mb-4 mt-3">
			<div class="card-body p-0">
				<table class="table table-bordered mb-0 text-center align-middle">
					<thead style="background-color: #212529; color: white;">
						<tr>
							<th style="width: 50px;">STT</th>
							<th>Tên menu</th>
							<th>Cấp bậc</th>
							<th>Mã cha</th>
							<th>Thứ tự</th>
							<th>Hiển thị</th>
							<th style="width: 120px;">Chức năng</th>
						</tr>
					</thead>
					<tbody>
						<%
						int stt = 1;
						if (listMenu != null && !listMenu.isEmpty()) {
							for (Menu item : listMenu) {
						%>
						<tr style="background-color: #f8f9fa;">
							<td><%=stt++%></td>
							<td class="text-start fw-bold">
								<i class="<%=item.getIcon()%> me-2"></i><%=item.getMenuName()%>
							</td>
							<td><%=item.getLevels()%></td>
							<td><%=item.getParentID()%></td>
							<td><%=item.getMenuOrder()%></td>
							<td>
								<form action="<%=request.getContextPath()%>/admin/Menu/ToggleStatus"
									method="post" style="margin: 0;">
									<input type="hidden" name="id" value="<%=item.getMenuID()%>" />
									<input type="checkbox" <%=item.isActive() ? "checked" : ""%>
										onchange="this.form.submit()">
								</form>
							</td>
							<td>
								<div class="d-flex justify-content-center gap-1">
									<a href="<%=request.getContextPath()%>/admin/Menu/Edit?id=<%=item.getMenuID()%>"
										class="btn btn-primary btn-sm"> <i class="bi bi-pencil"></i>
									</a> 
									<a href="<%=request.getContextPath()%>/admin/Menu/Delete?id=<%=item.getMenuID()%>"
										class="btn btn-danger btn-sm"> <i class="bi bi-trash"></i>
									</a>
								</div>
							</td>
						</tr>

						<%
						if (item.getSubMenus() != null && !item.getSubMenus().isEmpty()) {
							for (Menu sub : item.getSubMenus()) {
						%>
						<tr>
							<td><%=stt++%></td>
							<td class="text-start ps-5 text-secondary">
								<i class="bi bi-arrow-return-right me-2"></i><%=sub.getMenuName()%>
							</td>
							<td><%=sub.getLevels()%></td>
							<td><%=sub.getParentID()%></td>
							<td><%=sub.getMenuOrder()%></td>
							<td>
								<% if (item.isActive()) { %>
									<form action="<%=request.getContextPath()%>/admin/Menu/ToggleStatus"
										method="post" style="margin: 0;">
										<input type="hidden" name="id" value="<%=sub.getMenuID()%>" />
										<input type="checkbox" <%=sub.isActive() ? "checked" : ""%>
											onchange="this.form.submit()">
									</form>
								<% } else { %>
									<span class="text-muted" title="Menu cha đang tắt">
										<i class="bi bi-eye-slash"></i>
									</span>
								<% } %>
							</td>
							<td>
								<div class="d-flex justify-content-center gap-1">
									<a href="<%=request.getContextPath()%>/admin/Menu/Edit?id=<%=sub.getMenuID()%>"
										class="btn btn-primary btn-sm"> <i class="bi bi-pencil"></i>
									</a> 
									<a href="<%=request.getContextPath()%>/admin/Menu/Delete?id=<%=sub.getMenuID()%>"
										class="btn btn-danger btn-sm"
										onclick="return confirm('Xóa menu con này?')"> <i class="bi bi-trash"></i>
									</a>
								</div>
							</td>
						</tr>
						<%
							}
						}
						}
						} else {
						%>
						<tr>
							<td colspan="7" class="text-center text-muted py-3">Không có dữ liệu Menu.</td>
						</tr>
						<% } %>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</main>