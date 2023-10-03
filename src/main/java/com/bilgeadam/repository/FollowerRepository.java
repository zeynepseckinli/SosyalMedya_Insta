package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.Follower;
import com.bilgeadam.utility.MyFactoryRepository;

public class FollowerRepository extends MyFactoryRepository<Follower,Long> {
    public FollowerRepository(){
        super(new Follower());
    }
}
