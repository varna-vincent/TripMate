package com.coen268.tripmate.models;

public class UserModel {

        private String tokenId,userName,userEmail;

        public String getTokenId() {
            return tokenId;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserEmail() {
            return userEmail;
        }
        public UserModel(){}
        public UserModel(String tokenId, String userName, String userEmail){
            this.userEmail=userEmail;
            this.userName=userName;
            this.tokenId=tokenId;
        }

    }

