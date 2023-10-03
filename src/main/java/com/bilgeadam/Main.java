package com.bilgeadam;

import com.bilgeadam.repository.FollowingRepository;
import com.bilgeadam.repository.UserRepository;
import com.bilgeadam.repository.entity.Following;
import com.bilgeadam.repository.entity.User;
import com.bilgeadam.repository.enums.FollowState;


import static com.bilgeadam.utility.Constants.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static String loginUserName;
    public static void main(String[] args) {
        boolean close = false;
        do {
            int secim = 0;
            login();
            System.out.print("Lütfen seçiniz : ");
            secim = new Scanner(System.in).nextInt();
            switch (secim){
                case 0: close = true; break;
                case 1: registerUser(); break;
                case 2: loginUser();break;
            }
        }while (!close);


    }

    private static void login(){
        System.out.println("*********************************************");
        System.out.println("**********      SOSYAL  MEDYA       *********");
        System.out.println("*********************************************");
        System.out.println();
        System.out.println("1- Üye Ol");
        System.out.println("2- Giriş Yap");
        System.out.println("0- KAPAT");
    }

    private static void home(){
        System.out.println("*********************************************");
        System.out.println("************       AKIŞ         *************");
        System.out.println("*********************************************");
        System.out.println();
        System.out.println("1- Kullanıcı Ara");
        System.out.println("2- Kullanıcı Listele");
        System.out.println("3- Postları Gör");
        System.out.println("4- Kullanıcıyı Takip Et");
        System.out.println("5- Takip Ettiklerim");
        System.out.println("6- Takipçilerim");
        System.out.println("0- ÇIKIŞ YAP");
    }

    private static void registerUser(){


        System.out.println("*********************************************");
        System.out.println("************     YENİ ÜYE       *************");
        System.out.println("*********************************************");
        System.out.println();
        String username="",name="";
        System.out.println("Kullanıcı adını giriniz : ");
        username = new Scanner(System.in).nextLine();
        System.out.println("Ad ve soyadınızı giriniz  : ");
        name = new Scanner(System.in).nextLine();
        User user = User.builder()
                .username(username)
                .name(name)
                .followercount(0)
                .followingcount(0)
                .photourl("")
                .baseEntity(getBaseEntity())
                .build();
        new UserRepository().save(user);
    }

    private static void loginUser(){
        boolean close = false;
        System.out.println("Kullanıcı adınızı giriniz : ");
        loginUserName = new Scanner(System.in).nextLine();
        do {
            home();
            System.out.print("Lütfen seçiniz : ");
            int secim = new Scanner(System.in).nextInt();
            switch (secim){
                case 0 : close=true; break;
                case 1 : break;
                case 2 : userList(); break;
                case 3 : break;
                case 4 : following();break;
            }
        }while (!close);

    }

    private static void userList(){
        System.out.println("*********************************************");
        System.out.println("**********   KULLANICI LİSTESİ    ***********");
        System.out.println("*********************************************");
        System.out.println();
        List<User> users = new UserRepository().findAll();
        users.forEach(u->{
            System.out.println("Username : " + u.getUsername());
            System.out.println("Name : " + u.getName());
            System.out.println("-------------------------------------");
        });
    }

    private static void following(){
        System.out.println("*********************************************");
        System.out.println("**********    TAKİP İŞLEMİ        ***********");
        System.out.println("*********************************************");
        System.out.println();
        System.out.println("Hangi kullanıcıyı takip etmek istiyorsunuz? : ");
        String username = new Scanner(System.in).nextLine();
        Long userid = new UserRepository().findByColumnNameAndValue("username",loginUserName).get(0).getId();
        Long followinguserid = new UserRepository().findByColumnNameAndValue("username",username).get(0).getId();
        Following following = Following.builder()
                .userid(userid)//takip eden kisinin idsi.
                .followinguserid(followinguserid)//takip edilen kisinin idsi
                .followState(FollowState.TAKIP_EDIYOR)
                .baseEntity(getBaseEntity())
                .build();
        new FollowingRepository().save(following);
    }



}