    package com.example.recipeapp.model;
    public class User {
        private int id;
        private String email;
        private String username;
        private String role;
        public User(int id, String email, String username, String role) {
            this.id = id;
            this.email = email;
            this.username = username;
            this.role = role;
        }
        public User() {}
    }
