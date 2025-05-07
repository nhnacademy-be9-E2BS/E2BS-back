package com.nhnacademy.back.account.socialauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;

public interface SocialAuthJpaRepository extends JpaRepository<SocialAuth,Long> {
}
