<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.Account"%>

<main id="main" class="main">
	<!-- Tiêu đề trang -->
	<div class="pagetitle mb-4">
		<h2>📋 Danh sách người dùng</h2>
	</div>

	<section class="section dashboard">
		<div class="row">
			<div class="col-12">
				<div class="card shadow-sm border-0">

					<!-- Header -->
					<div
						class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
						<h5 class="mb-0">Quản lý người dùng</h5>
						<div>
							<a href="${pageContext.request.contextPath}/admin/Account/Create"
								class="btn btn-light btn-sm me-2"> <i
								class="bi bi-plus-circle me-1"></i> Thêm mới
							</a>

						</div>
					</div>


					<!-- Danh sách -->
					<div class="card-body">
						<%
						List<Account> list = (List<Account>) request.getAttribute("list");
						%>
						<table
							class="table table-hover table-bordered align-middle datatable bg-white">
							<thead class="table-light">
								<tr class="text-center">
									<th class="text-center">STT</th>
									<th class="text-center">Mã đăng nhập</th>
									<th class="text-center">Email</th>
									<th class="text-center">Ngày sửa</th>
									<th class="text-center">Trạng thái</th>
									<th class="text-center">Chức năng</th>
								</tr>
							</thead>
							<tbody>
								<%
								if (list != null && !list.isEmpty()) {
									int index = 1;
									for (Account acc : list) {
								%>
								<tr class="text-center">
									<td><%=index++%></td>
									<td><%=acc.getUserName()%></td>
									<td><%=acc.getEmail()%></td>
									<td><%=acc.getDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm").format(acc.getDate()) : ""%></td>
									
									<td class="text-center">
                                            <form action="<%= request.getContextPath() %>/admin/Account/ToggleStatus" method="post">
                                                <input type="hidden" name="id" value="<%= acc.getUserID() %>" />
                                                <input type="checkbox" name="IsActive" 
                                                       <%= acc.isActive() ? "checked" : "" %> 
                                                       onchange="this.form.submit()" />
                                            </form>
                                        </td>


									<td><a
										href="<%=request.getContextPath()%>/admin/Account/Edit?id=<%=acc.getUserID()%>"
										class="btn btn-primary btn-sm me-1" title="Chỉnh sửa"> <i
											class="bi bi-pencil"></i>
									</a> <a
										href="<%=request.getContextPath()%>/admin/Account/Delete?id=<%=acc.getUserID()%>"
										class="btn btn-danger btn-sm"
										
										title="Xóa người dùng"> <i class="bi bi-trash"></i>
									</a></td>
								</tr>
								<%
								}
								} else {
								%>
								<tr>
									<td colspan="6" class="text-center text-muted py-3"><i
										class="bi bi-exclamation-circle me-1"></i> Chưa có người dùng
										nào</td>
								</tr>
								<%
								}
								%>
							</tbody>
						</table>
					</div>

				</div>
			</div>
		</div>
	</section>
</main>
