package Controller;

import ControllerHelper.AdminHelper;
import ControllerHelper.BranchHelper;
import ControllerHelper.CommonConstant;
import ControllerHelper.FuntionHelper;
import ControllerHelper.GetUserCredentialsFactory;
import ControllerHelper.InterfaceAdmin;
import ControllerHelper.validationHelper;
import Model.Admin;
import Model.Branch;
import Model.UserCredentials;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.json.JSONObject;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class AdminServlet extends HttpServlet {
    
    /*
     * creating object from AdminHelper with reference to InterfaceAdmin interface
     * a brigde pattern is existed between AdminServlet and InterfaceAdmin
     * the implementations of the methods in the AdminHelper are abstracted
     */
    InterfaceAdmin interfaceAdmin = new AdminHelper();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getServletPath();

        switch (action) {
            case "/adminSignin":
                adminSignin(request, response);
                break;
            case "/adminSignOut":
                adminSignOut(request, response);
                break;
            case "/adminInsert":
                insertAdmin(request, response);
                break;
            case "/adminDelete":
                deleteAdmin(request, response);
                break;
            case "/adminEdit":
                showEditAdminForm(request, response);
                break;
            case "/adminUpdate":
                updateAdmin(request, response);
                break;
            case "/adminProfilePage":
                adminProfilePage(request, response);
                break;
            case "/adminChangePassword":
                adminChangePassword(request, response);
                break;
            case "/adminEditProfile":
                adminEditProfile(request, response);
                break;
            default:
                listAdmin(request, response);
                break;

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void listAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
//        List<Admin> adminList = new AdminHelper().getAllAdmin();
//        request.setAttribute("adminList", adminList);
//        List<Branch> branchList = new BranchHelper().getAllBranch();
//        request.setAttribute("branchList", branchList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("Views/Admin/manage_admin.jsp");
        dispatcher.forward(request, response);
    }

    private void insertAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (validationHelper.isValidEmailAddress(request.getParameter("email")) == false) { // validate email
            
            // create json object
            JSONObject jsonData = new JSONObject();
            jsonData.put("status", "invalidEmail");
            response.setContentType("application/json");
            response.getWriter().print(jsonData.toString());

        } else if (validationHelper.validateNIC(request.getParameter("nic")) == false) { // validate NIC
            
            // create json object
            JSONObject jsonData = new JSONObject();
            jsonData.put("status", "invalidNIC");
            response.setContentType("application/json");
            response.getWriter().print(jsonData.toString());

        } else {
            Admin admin = new Admin(
                    request.getParameter("name"),
                    request.getParameter("nic"),
                    request.getParameter("email"),
                    FuntionHelper.randomPassword(),
                    "1",
                    request.getParameter("branch_id")
            );

            if (interfaceAdmin.insertAdmin(admin) > 0) {
                JSONObject jsonData = new JSONObject();
                jsonData.put("status", "pass");
                response.setContentType("application/json");
                response.getWriter().print(jsonData.toString());
            } else {
                JSONObject jsonData = new JSONObject();
                jsonData.put("status", "fail");
                response.setContentType("application/json");
                response.getWriter().print(jsonData.toString());
            }
        }
    }

    private void deleteAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (interfaceAdmin.deleteAdmin(id) > 0) {
            JSONObject jsonData = new JSONObject();
            jsonData.put("status", "pass");
            response.setContentType("application/json");
            response.getWriter().print(jsonData.toString());
        } else {
            JSONObject jsonData = new JSONObject();
            jsonData.put("status", "fail");
            response.setContentType("application/json");
            response.getWriter().print(jsonData.toString());
        }

    }

    private void showEditAdminForm(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        Admin editAdmin = interfaceAdmin.selectAdmin(id);
        request.setAttribute("editAdmin", editAdmin);
        listAdmin(request, response);
    }

    private void updateAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (new validationHelper().isValidEmailAddress(request.getParameter("email")) == false) {

            JSONObject jsonData = new JSONObject();
            jsonData.put("status", "invalidEmail");
            response.setContentType("application/json");
            response.getWriter().print(jsonData.toString());

        } else if (new validationHelper().validateNIC(request.getParameter("nic")) == false) {

            JSONObject jsonData = new JSONObject();
            jsonData.put("status", "invalidNIC");
            response.setContentType("application/json");
            response.getWriter().print(jsonData.toString());

        } else {
            Admin admin = new Admin(
                    Integer.parseInt(request.getParameter("id")),
                    request.getParameter("name"),
                    request.getParameter("nic"),
                    request.getParameter("email"),
                    request.getParameter("password"),
                    request.getParameter("branch_id")
            );

            if (interfaceAdmin.updateAdmin(admin) > 0) {
                JSONObject jsonData = new JSONObject();
                jsonData.put("status", "pass");
                response.setContentType("application/json");
                response.getWriter().print(jsonData.toString());
            } else {
                JSONObject jsonData = new JSONObject();
                jsonData.put("status", "fail");
                response.setContentType("application/json");
                response.getWriter().print(jsonData.toString());
            }
        }
    }

    private void adminSignin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (new validationHelper().isValidEmailAddress(request.getParameter("email")) == false) {
            JSONObject jsonData = new JSONObject();
            jsonData.put("status", "invalidEmail");
            response.setContentType("application/json");
            response.getWriter().print(jsonData.toString());
        } else {
            
             // create GetUserCredentialsFactory object
             GetUserCredentialsFactory getUserCredentialsFactory = new GetUserCredentialsFactory();
              // get admin type object
             UserCredentials admin = getUserCredentialsFactory.getUserCredentials("admin", request.getParameter("email"), request.getParameter("password"));
             
             
            if (interfaceAdmin.validateAdmin(admin) > 0) {
                // creare session
                HttpSession session = request.getSession();
                // session time out time
                session.setMaxInactiveInterval(1800);
                String id = String.valueOf(interfaceAdmin.validateAdmin(admin));
                // set USER_ID session
                session.setAttribute("USER_ID", id);
                if ("1".equals(id)) {
                    // set manager USER_TYPE session
                    session.setAttribute("USER_TYPE", "manager");
                } else {
                    // set admin USER_TYPE session
                    session.setAttribute("USER_TYPE", "admin");
                }
                
                JSONObject jsonData = new JSONObject();
                jsonData.put("status", "pass");
                response.setContentType("application/json");
                response.getWriter().print(jsonData.toString());
            } else {
              
                JSONObject jsonData = new JSONObject();
                jsonData.put("status", "fail");
                response.setContentType("application/json");
                response.getWriter().print(jsonData.toString());
            }
        }

    }

    private void adminProfilePage(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher("Views/Admin/profile.jsp").forward(request, response);
    }

    private void adminSignOut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        // session remove 
        session.removeAttribute("USER_ID");
        request.getRequestDispatcher("Views/Admin/signin.jsp").forward(request, response);
    }

    private void adminChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        int id = Integer.parseInt(request.getParameter("id"));
        final String curuntPassword = request.getParameter("current_password");
        final String newPassword = request.getParameter("new_password");

        if (interfaceAdmin.selectChangePasswordAdmin(id, curuntPassword) > 0) { // chack curunt password
            if (interfaceAdmin.changePasswordAdmin(id, newPassword) > 0) { // chack query execute

                JSONObject jsonData = new JSONObject();
                jsonData.put("status", "pass");
                response.setContentType("application/json");
                response.getWriter().print(jsonData.toString());
            } else { 
                JSONObject jsonData = new JSONObject();
                jsonData.put("status", "fail");
                response.setContentType("application/json");
                response.getWriter().print(jsonData.toString());
            }
        } else {
            JSONObject jsonData = new JSONObject();
            jsonData.put("status", "invalidCurrent");
            response.setContentType("application/json");
            response.getWriter().print(jsonData.toString());
        }

    }

    private void adminEditProfile(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            /*
             * edit only profile data and image
             */
            
            String fileName = null;

            Part part = request.getPart("image");

            fileName = FuntionHelper.extractFileName(part); // get image name
            part.write(CommonConstant.ADMIN_IMAGE_UPLOAD_DIR + fileName); // image uploard to server

            Admin admin = new Admin(
                    Integer.parseInt(request.getParameter("id")),
                    request.getParameter("name"),
                    request.getParameter("nic"),
                    request.getParameter("email"),
                    fileName
            );

            if (interfaceAdmin.updateProfileImageAndData(admin) > 0) {
                adminProfilePage(request, response);
            } else {
                request.getRequestDispatcher("Views/Admin/fail.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            /*
             * edit only profile data
             */
            Admin admin = new Admin(
                    Integer.parseInt(request.getParameter("id")),
                    request.getParameter("name"),
                    request.getParameter("nic"),
                    request.getParameter("email")
            );

            if (interfaceAdmin.updateProfileData(admin) > 0) {
                adminProfilePage(request, response);
            } else {
                request.getRequestDispatcher("Views/Admin/fail.jsp").forward(request, response);
            }
        }

    }

}
