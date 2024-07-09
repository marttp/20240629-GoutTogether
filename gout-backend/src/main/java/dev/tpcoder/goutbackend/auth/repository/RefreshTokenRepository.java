package dev.tpcoder.goutbackend.auth.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import dev.tpcoder.goutbackend.auth.model.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {

    @Modifying
    @Query("UPDATE refresh_token SET is_expired = :is_expired WHERE usage = :usage AND resource_id = :resource_id")
    void updateRefreshTokenByResource(@Param("usage") String usage, @Param("resource_id") int resourceId,
            @Param("is_expired") boolean isExpired);

    Optional<RefreshToken> findOneByToken(String token);
}
