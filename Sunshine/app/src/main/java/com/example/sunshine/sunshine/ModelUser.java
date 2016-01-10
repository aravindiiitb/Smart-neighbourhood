package com.example.sunshine.sunshine;

import java.io.Serializable;

/**
 * Created by dell pc on 25/12/2015.
 */
public class ModelUser implements Serializable {
    public String username;
    public String password;
    public String email;
    public int id;
    public boolean is_admin;
    public String occupation;
    public int is_verified;
}
