<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.Role"%>
<%@ page import="model.bean.Account"%>

<main id="main" class="main">
	<div class="pagetitle mb-4">
		<h2>Sửa thông tin tài khoản</h2>
	</div>

	<div class="container shadow p-4 bg-white rounded">
		<%
		Account acc = (Account) request.getAttribute("account");
		List<Role> roles = (List<Role>) request.getAttribute("roles");
		%>
		<form action="${pageContext.request.contextPath}/admin/Account/Edit"
			method="post">
			<input type="hidden" name="UserID" value="<%=acc.getUserID()%>" />
			<div class="row g-3">
				<div class="col-md-6">
					<label class="form-label fw-semibold">Tên đăng nhập</label> <input
						type="text" name="UserName" class="form-control"
						value="<%=acc.getUserName()%>" required />
				</div>

				<div class="col-md-6">
					<label class="form-label fw-semibold">Email</label> <input
						type="text" name="Email" class="form-control"
						value="<%=acc.getEmail()%>" required />
				</div>

				<div class="col-md-6">
					<label class="form-label fw-semibold">Mật khẩu mới (nếu
						muốn thay đổi)</label> <input type="password" name="Password"
						class="form-control" placeholder="Để trống nếu không đổi" />
				</div>

				<div class="col-md-6">
					<label class="form-label fw-semibold">Chọn quyền</label>
					<div>
						<%
						List<Integer> userRoles = (List<Integer>) request.getAttribute("userRoles");

						if (roles != null && !roles.isEmpty()) {
							for (Role r : roles) {
								boolean isChecked = userRoles != null && userRoles.contains(r.getRoleID());
						%>
						<div class="form-check form-check-inline">
							<input class="form-check-input" type="checkbox" name="RoleIDs"
								id="role_<%=r.getRoleID()%>" value="<%=r.getRoleID()%>"
								<%=isChecked ? "checked" : ""%> /> <label
								class="form-check-label" for="role_<%=r.getRoleID()%>">
								<%=r.getRoleName()%>
							</label>
						</div>
						<%
						}
						} else {
						%>
						<span class="text-muted">Chưa có quyền nào</span>
						<%
						}
						%>
					</div>
				</div>


				<div class="col-md-6">
					<div class="form-check mt-3">
						<input class="form-check-input" type="checkbox" name="IsActive"
							value="true" <%=acc.isActive() ? "checked" : ""%> /> <label
							class="form-check-label fw-semibold">Hiển thị</label>
					</div>
				</div>
			</div>

			<div class="mt-4 d-flex gap-3">
				<a href="${pageContext.request.contextPath}/admin/Account/Index"
					class="btn btn-warning btn-lg"> <i
					class="bi bi-arrow-left-circle"></i> Quay lại
				</a>
				<button type="submit" class="btn btn-primary btn-lg">
					<i class="bi bi-file-plus-fill"></i> Lưu thông tin
				</button>
			</div>
		</form>
	</div>
</main>
l>
