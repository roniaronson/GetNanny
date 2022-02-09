package com.example.getnanny20;

public class User {
        private String userID;
        private String name;
        private String Email;
        private String password;
        private Post post = new Post().setDateString("");
        private String phoneNumber;

        public User() {
        }

        public String getUserID(){ return this.userID;}

        public void setUserID(String userID){ this.userID = userID;}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Post getPost(){ return post;}

        public void setPost(Post post){ this.post = post; }

        public String getPhoneNumber(){ return this.phoneNumber;}

        public void setPhoneNumber(String phoneNumber){ this.phoneNumber = phoneNumber;}
    }


