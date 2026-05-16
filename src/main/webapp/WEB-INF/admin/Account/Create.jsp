<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.Role"%>
<%@ page import="model.bean.Account"%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>Thêm mới tài khoản người dùng</h2>
    </div>

    <div class="container shadow p-4 bg-white rounded">
        <form action="${pageContext.request.contextPath}/admin/Account/Create" method="post">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Tên đăng nhập</label>
                    <input type="text" name="UserName" id="UserName" class="form-control" placeholder="Nhập tên đăng nhập..." required/>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold">Email</label>
                    <div class="input-group">
                        <input type="text" id="EmailName" class="form-control" placeholder="Email" readonly />
                        <select id="EmailDomain" class="form-select">
                            <option value="gmail.com">@gmail.com</option>
                            <option value="icloud.com">@icloud.com</option>
                        </select>
                    </div>
                    <input type="hidden" name="Email" id="EmailFull" />
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold">Mật khẩu</label>
                    <input type="text" name="Password" id="Password" class="form-control" placeholder="Nhập mật khẩu..." required/>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold">Chọn quyền</label>
                    <div>
                        <%
                            List<Role> roles = (List<Role>) request.getAttribute("roles");
                            if (roles != null) {
                                for (Role r : roles) {
                        %>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="RoleID" id="role_<%= r.getRoleID() %>" value="<%= r.getRoleID() %>" />
                                <label class="form-check-label" for="role_<%= r.getRoleID() %>"><%= r.getRoleName() %></label>
                            </div>
                        <%
                                }
                            }
                        %>
                    </div>
                </div>

                <div class="col-md-6 d-flex align-items-center">
                    <div class="form-check mt-3">
                        <input class="form-check-input" type="checkbox" name="IsActive" id="IsActive" value="true"/>
                        <label class="form-check-label fw-semibold" for="IsActive">Hiện thị</label>
                    </div>
                </div>
            </div>

            <div class="mt-4 d-flex gap-3">
                <a href="${pageContext.request.contextPath}/admin/Account/Index" class="btn btn-warning btn-lg d-flex align-items-center gap-2">
                    <i class="bi bi-arrow-left-circle"></i> Quay lại
                </a>
                <button type="submit" class="btn btn-primary btn-lg d-flex align-items-center gap-2">
                    <i class="bi bi-file-plus-fill"></i> Lưu thông tin
                </button>
            </div>
        </form>
    </div>
</main>

<script>
    const userInput = document.getElementById('UserName');
    const emailName = document.getElementById('EmailName');
    const emailDomain = document.getElementById('EmailDomain');
    const emailFull = document.getElementById('EmailFull');
    const passwordInput = document.getElementById('Password');

    function updateFields() {
        const name = userInput.value.trim();
        const domain = emailDomain.value;
        if (name && domain) {
            emailName.value = name;
            emailFull.value = name + "@" + domain;
            passwordInput.value = name;
        } else {
            emailName.value = "";
            emailFull.value = "";
        }
    }

    updateFields();
    userInput.addEventListener('input', updateFields);
    emailDomain.addEventListener('change', updateFields);
</script>
