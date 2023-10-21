package com.example.CustomerManagement.controllers;


import com.example.CustomerManagement.entities.Token;
import com.example.CustomerManagement.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.example.CustomerManagement.entities.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CustomerController {
    List<Customer> customers = new ArrayList<>();
    @GetMapping("/customers")
    public String showAllCustomers(HttpSession session, Model model) throws IOException {
        User user = (User) session.getAttribute("user");
        if(isUserAvaillable(user)) {
            String loginId = user.getLogin_id();
            String password = user.getPassword();
            String token = getBearerToken(loginId, password);
            String queryUrl_get = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";
            URL url = new URL(queryUrl_get);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", token);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String output;
            String result = "";
            while ((output = reader.readLine()) != null) {
                result = result + output;
                model.addAttribute("result", result);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            customers = objectMapper.readValue(result, typeFactory.constructCollectionType(List.class, Customer.class));
            model.addAttribute("customers", customers);
            return "customers-list";
        }
        else {
            return "login";
        }
    }


    public String getBearerToken(String login_id,String password) throws IOException {
        User user = new User();
        user.setLogin_id(login_id);
        user.setPassword(password);
        ObjectMapper objectMapper= new ObjectMapper();
        String body=objectMapper.writeValueAsString(user);
        String tokenUrl="https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";
        URL url=new URL(tokenUrl);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept","application/json");
        try (DataOutputStream dos=new DataOutputStream(connection.getOutputStream())){
            dos.writeBytes(body);
        }
        BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String output;
        String result = "";
        while((output=reader.readLine())!=null){
            result =result+ output;
            //model.addAttribute("result",result);
        }

        ObjectMapper tokenObjectMapper = new ObjectMapper();
        Token token = tokenObjectMapper.readValue(result,  Token.class);

        return "Bearer "+ token.getAccess_token();
    }

    @GetMapping("/add-customer")
    public String showCustomerForm(HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if(isUserAvaillable(user)) {
            String loginId = user.getLogin_id();
            String password = user.getPassword();
            String token = getBearerToken(loginId, password);
            return "add-customer";
        }else{
            return "redirect:/login";
        }
    }

    @PostMapping("/addCustomer")
    public String addCustomer(HttpSession session,Model model,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String street,
                              @RequestParam String address,
                              @RequestParam String city,
                              @RequestParam String state,
                              @RequestParam String email,
                              @RequestParam String phone) throws IOException {
        User user = (User) session.getAttribute("user");
        if(isUserAvaillable(user))
        {
            String loginId = user.getLogin_id();
            String password = user.getPassword();
            String token = getBearerToken(loginId, password);
            Customer customer = new Customer();
            customer.setFirst_name(firstName);
            customer.setLast_name(lastName);
            customer.setStreet(street);
            customer.setAddress(address);
            customer.setCity(city);
            customer.setState(state);
            customer.setEmail(email);
            customer.setPhone(phone);
            String postUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=create";
            URL url = new URL(postUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", token);
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(customer);
            try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
                dos.writeBytes(body);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String output;
            String result = "";
            while ((output = reader.readLine()) != null) {
                result = result + output;
                model.addAttribute("result", result);

            }
            return "post-response";
        }
        else
        {
            return "login";
        }
    }

    @GetMapping("/update-customer/{uuid}")
    public String showUpdateCustomerForm(HttpSession session,@PathVariable String uuid,Model model) throws IOException {
        User user = (User) session.getAttribute("user");

        if(isUserAvaillable(user))
        {
            String loginId = user.getLogin_id();
            String password = user.getPassword();
            String token = getBearerToken(loginId, password);
            Customer customer = new Customer();

            for (Customer item : customers) {
                if (item.getUuid().equals(uuid)) {
                    customer = item;
                }
            }
            model.addAttribute(customer);
            return "update-customer-form";
        }
        else{
            return "login";
        }
    }
    @PostMapping("/update-customer")
    public String updateCustomer(Model model, @ModelAttribute("customer") Customer customer,HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");

        if(isUserAvaillable(user)) {
            String loginId = user.getLogin_id();
            String password = user.getPassword();
            String token = getBearerToken(loginId, password);
            String id = customer.getUuid();
            String updateUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=update&uuid=" + id;
            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", token);
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(customer);
            try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
                dos.writeBytes(body);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String output;
            String result = "";
            while ((output = reader.readLine()) != null) {
                result = result + output;
                model.addAttribute("result", result);
            }
            return "post-response";
        }
        else{
            return "login";
        }
    }

    @PostMapping("/delete-customer/{uuid}")
    public String DeleteCustomer(@PathVariable String uuid,Model model,HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");

        if(isUserAvaillable(user)) {
            String loginId = user.getLogin_id();
            String password = user.getPassword();
            String token = getBearerToken(loginId, password);
            String deleteUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=delete&uuid=" + uuid;
            URL url = new URL(deleteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", token);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String output;
            String result = "";
            while ((output = reader.readLine()) != null) {
                result = result + output;
                model.addAttribute("result", result);
            }
            return "post-response";
        }
        else{
            return "login";
        }
    }

    private static Boolean isUserAvaillable(User user)
    {
        if(user != null && ( user.getLogin_id()!= null || user.getLogin_id()!= "") && ( user.getPassword()!= null || user.getPassword()!= "") )
        {
            return true;
        }

        return false;
    }
}
