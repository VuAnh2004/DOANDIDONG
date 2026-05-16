package controller.admin;

import model.DAO.*;
import model.DAO.impl.*;
import model.bean.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;
import java.sql.Date;

@WebServlet("/admin/QLDiem/*")
public class QLDiemController extends HttpServlet {

    private final QLDiemDAO diemDAO = new QLDiemDAOImpl();
    private final QLHocSinhDAO hsDAO = new QLHocSinhDAOImpl();
    private final QLMonHocDAO monHocDAO = new QLMonHocDAOImpl();
    private final QLLopHocDAO lopHocDAO = new QLLopHocDAOImpl();
    private final QLHocKyDAO hocKyDAO = new QLHocKyDAOImpl();
    private final AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();

    private void setMenu(HttpServletRequest req) {
        List<AdminMenu> menus = adminMenuDAO.getActiveMenus();
        req.setAttribute("menus", buildMenuTree(menus, req.getContextPath()));
    }

    private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
        List<AdminMenu> allMenus = new ArrayList<>(flatMenus);
        Map<Integer, AdminMenu> menuMap = new HashMap<>();
        
        for (AdminMenu m : allMenus) {
            menuMap.put((int) m.getAdminMenuID(), m);
            if (m.getIdName() == null || m.getIdName().isEmpty())
                m.setIdName("menu-" + m.getAdminMenuID());

            String controller = m.getControllerName();
            String action = m.getActionName();
            
            if (m.getAdminMenuID() == 1 || ("Home".equalsIgnoreCase(controller) && "Index".equalsIgnoreCase(action))) {
                m.setItemTarget(contextPath + "/admin");
            } else if (controller != null && action != null && !controller.isEmpty() && !action.isEmpty()) {
                m.setItemTarget(contextPath + "/admin/" + controller + "/" + action);
            } else {
                m.setItemTarget("#");
            }
        }

        for (AdminMenu m : allMenus) {
            int parentId = m.getParentLevel();
            if (parentId != 0) {
                AdminMenu parent = menuMap.get(parentId);
                if (parent != null) {
                    if (parent.getSubMenus() == null)
                        parent.setSubMenus(new ArrayList<>());
                    parent.getSubMenus().add(m);
                }
            }
        }

        List<AdminMenu> rootMenus = new ArrayList<>();
        for (AdminMenu m : allMenus) {
            if (m.getParentLevel() == 0)
                rootMenus.add(m);
        }
        return rootMenus;
    }
    
    
    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        setMenu(req);
        req.setAttribute("contentPage", view);
        req.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(req, resp);
    }

    private void loadDropdowns(HttpServletRequest req) {
        req.setAttribute("studentList", hsDAO.getAll());
        req.setAttribute("classList", lopHocDAO.getAll());
        req.setAttribute("subjectList", monHocDAO.getAll());
        req.setAttribute("semesterList", hocKyDAO.getAll());
    }

    private String getAction(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) return "Index";
        path = path.replace("/", "");
        return path.isEmpty() ? "Index" : path.substring(0,1).toUpperCase() + path.substring(1);
    }

    private Integer parseInt(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.parseInt(s); }
        catch(Exception e) { return null; }
    }

    private Double parseDouble(String s) {
        try { return (s == null || s.isBlank()) ? null : Double.parseDouble(s); }
        catch(Exception e) { return null; }
    }

    private double calculateAverage(QLDiem d) {
        double total=0, weight=0;
        if(d.getOralScore1()!=null){ total+=d.getOralScore1(); weight+=1; }
        if(d.getOralScore2()!=null){ total+=d.getOralScore2(); weight+=1; }
        if(d.getOralScore3()!=null){ total+=d.getOralScore3(); weight+=1; }
        if(d.getQuiz15Min1()!=null){ total+=d.getQuiz15Min1(); weight+=1; }
        if(d.getQuiz15Min2()!=null){ total+=d.getQuiz15Min2(); weight+=1; }
        if(d.getMidtermScore()!=null){ total+=d.getMidtermScore()*2; weight+=2; }
        if(d.getFinalScore()!=null){ total+=d.getFinalScore()*3; weight+=3; }
        return weight>0 ? total/weight : 0;
    }

    private String calculateGradeCategory(double avg){
        if(avg>=8) return "Giỏi";
        else if(avg>=6.5) return "Khá";
        else if(avg>=5) return "Trung bình";
        else return "Yếu";
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setMenu(req);
        String action = getAction(req);
        HttpSession session = req.getSession();
        String currentUser = (String) session.getAttribute("UserName"); // lấy username từ session
        Boolean isAdmin = (Boolean) session.getAttribute("IsAdmin"); // giả sử session lưu role
        switch (action) {
            case "Index":
                req.setAttribute("diemList", diemDAO.getAll());
                forward(req, resp, "/WEB-INF/admin/QLDiem/Index.jsp");
                break;
       
            case "Create":
                loadDropdowns(req); 

                List<QLLopHoc> allClasses = lopHocDAO.getAll();

                Map<String, QLLopHoc> uniqueClassMap = new LinkedHashMap<>();
                for (QLLopHoc cls : allClasses) {
                    String className = cls.getClassName() != null ? cls.getClassName() : "";
                    String cohortStr = (cls.getKhoaHoc() != null && cls.getKhoaHoc().getCohort() != null)
                            ? cls.getKhoaHoc().getCohort().toString()
                            : "";

                    String key = className + "-" + cohortStr;
                    if (!key.isEmpty() && !uniqueClassMap.containsKey(key)) {
                        uniqueClassMap.put(key, cls);
                    }
                }

                req.setAttribute("classListUnique", new ArrayList<>(uniqueClassMap.values()));

                String classCohortParam = req.getParameter("classCohort");
                Integer subjectID = parseInt(req.getParameter("subjectID"));
                Integer semesterID = parseInt(req.getParameter("semesterID"));

                if (classCohortParam != null && !classCohortParam.isEmpty()) {
                    String[] parts = classCohortParam.split("-");
                    Integer classID = null;
                    Integer cohort = null;

                    if (parts.length == 2) {
                        classID = parseInt(parts[0]);
                        cohort = parseInt(parts[1]);
                    }

                    if (classID != null && cohort != null) {

                        List<QLHocSinh> studentsWithDuplicates = hsDAO.getByClass(classID, cohort);

                        Map<String, QLHocSinh> uniqueStudentMap = new LinkedHashMap<>();
                        for (QLHocSinh hs : studentsWithDuplicates) {
                            if (!uniqueStudentMap.containsKey(hs.getStudentID())) {
                                uniqueStudentMap.put(hs.getStudentID(), hs);
                            }
                        }

                        List<QLHocSinh> students = new ArrayList<>(uniqueStudentMap.values());
                        req.setAttribute("studentList", students);
                    }

                    req.setAttribute("selectedClassCohort", classCohortParam);
                    req.setAttribute("selectedSubjectID", subjectID);
                    req.setAttribute("selectedSemesterID", semesterID);
                }

                forward(req, resp, "/WEB-INF/admin/QLDiem/Create.jsp");
                break;


            case "Edit":
                Integer editId = parseInt(req.getParameter("gradeID"));
                if (editId != null) {
                    req.setAttribute("diem", diemDAO.getById(editId));
                    loadDropdowns(req);
                    forward(req, resp, "/WEB-INF/admin/QLDiem/Edit.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/QLDiem/Index");
                }
                break;

            case "Delete":
                Integer delId = parseInt(req.getParameter("gradeID"));
                if (delId != null) {
                    req.setAttribute("diem", diemDAO.getById(delId));
                    forward(req, resp, "/WEB-INF/admin/QLDiem/Delete.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/QLDiem/Index");
                }
                break;
           
            default:
                resp.sendRedirect(req.getContextPath() + "/admin/QLDiem/Index");
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = getAction(req);

        switch (action) {
        case "Create":
            req.setCharacterEncoding("UTF-8");

            String[] studentIDs = req.getParameterValues("studentIDs");
            Integer subjectID = parseInt(req.getParameter("subjectID"));
            Integer semesterID = parseInt(req.getParameter("semesterID"));
            String teacherID = req.getParameter("teacherID");

            if(studentIDs != null) {
                for(String studentID : studentIDs) {
                    QLDiem d = new QLDiem();
                    d.setStudentID(studentID);
                    d.setSubjectID(subjectID);
                    d.setSemesterID(semesterID);
                    d.setTeacherID(teacherID);

                    d.setOralScore1(parseDouble(req.getParameter("oralScore1_" + studentID)));
                    d.setOralScore2(parseDouble(req.getParameter("oralScore2_" + studentID)));
                    d.setOralScore3(parseDouble(req.getParameter("oralScore3_" + studentID)));
                    d.setQuiz15Min1(parseDouble(req.getParameter("quiz15Min1_" + studentID)));
                    d.setQuiz15Min2(parseDouble(req.getParameter("quiz15Min2_" + studentID)));
                    d.setMidtermScore(parseDouble(req.getParameter("midtermScore_" + studentID)));
                    d.setFinalScore(parseDouble(req.getParameter("finalScore_" + studentID)));

                    double avg = calculateAverage(d);
                    d.setAverageScore(avg);
                    d.setGradeCategory(calculateGradeCategory(avg));

                    d.setNotes(req.getParameter("notes"));
                    d.setCreateDate(new Date(System.currentTimeMillis()));
                    d.setUpdatedDate(new Date(System.currentTimeMillis()));
                    d.setActive(true);

                    diemDAO.insert(d);
                }
            }

            resp.sendRedirect(req.getContextPath() + "/admin/QLDiem/Index");
            break;


            case "Edit":
                Integer editId2 = parseInt(req.getParameter("gradeID"));
                if (editId2 != null) {
                    QLDiem d2 = diemDAO.getById(editId2);
                    if (d2 != null) {
                        d2.setStudentID(req.getParameter("studentID"));
                        d2.setSubjectID(parseInt(req.getParameter("subjectID")));
                        d2.setSemesterID(parseInt(req.getParameter("semesterID")));
                        d2.setTeacherID(req.getParameter("teacherID"));
                        d2.setOralScore1(parseDouble(req.getParameter("oralScore1")));
                        d2.setOralScore2(parseDouble(req.getParameter("oralScore2")));
                        d2.setOralScore3(parseDouble(req.getParameter("oralScore3")));
                        d2.setQuiz15Min1(parseDouble(req.getParameter("quiz15Min1")));
                        d2.setQuiz15Min2(parseDouble(req.getParameter("quiz15Min2")));
                        d2.setMidtermScore(parseDouble(req.getParameter("midtermScore")));
                        d2.setFinalScore(parseDouble(req.getParameter("finalScore")));
                        
                        double updatedAvg = calculateAverage(d2);
                        d2.setAverageScore(updatedAvg);
                        d2.setGradeCategory(calculateGradeCategory(updatedAvg));
                        
                        d2.setNotes(req.getParameter("notes"));
                        d2.setUpdatedDate(new Date(System.currentTimeMillis()));
                        d2.setActive(req.getParameter("isActive") != null);

                        diemDAO.update(d2);
                    }
                }
                break;

            case "Delete":
                Integer delId2 = parseInt(req.getParameter("gradeID"));
                if (delId2 != null)
                    diemDAO.delete(delId2);
                break;

            case "ToggleStatus":
                Integer toggleId = parseInt(req.getParameter("gradeID"));
                if (toggleId != null) {
                    QLDiem d3 = diemDAO.getById(toggleId);
                    if (d3 != null) {
                        d3.setActive(!d3.isActive());
                        d3.setUpdatedDate(new Date(System.currentTimeMillis()));
                        diemDAO.update(d3);
                    }
                }
                break;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/QLDiem/Index");
    }
}