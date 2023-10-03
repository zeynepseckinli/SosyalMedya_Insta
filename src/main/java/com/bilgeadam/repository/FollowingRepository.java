package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.Following;
import com.bilgeadam.utility.MyFactoryRepository;

public class FollowingRepository extends MyFactoryRepository<Following,Long> {
    public FollowingRepository(){
        super(new Following());
    }
}
