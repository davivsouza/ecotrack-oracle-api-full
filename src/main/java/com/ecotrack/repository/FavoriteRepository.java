package com.ecotrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecotrack.domain.Favorite;
import com.ecotrack.domain.FavoriteId;
import com.ecotrack.domain.UserAccount;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
  List<Favorite> findByUser(UserAccount user);
}
