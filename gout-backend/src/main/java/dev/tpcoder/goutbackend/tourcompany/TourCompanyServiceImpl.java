package dev.tpcoder.goutbackend.tourcompany;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.tpcoder.goutbackend.common.enumeration.TourCompanyStatus;
import dev.tpcoder.goutbackend.common.exception.EntityNotFound;
import dev.tpcoder.goutbackend.tourcompany.model.RegisterTourCompanyDto;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyLogin;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyWallet;
import dev.tpcoder.goutbackend.tourcompany.repository.TourCompanyLoginRepository;
import dev.tpcoder.goutbackend.tourcompany.repository.TourCompanyRepository;
import dev.tpcoder.goutbackend.tourcompany.repository.TourCompanyWalletRepository;

@Service
public class TourCompanyServiceImpl implements TourCompanyService {

    private final Logger logger = LoggerFactory.getLogger(TourCompanyServiceImpl.class);

    private final TourCompanyRepository tourCompanyRepository;
    private final TourCompanyLoginRepository tourCompanyLoginRepository;
    private final TourCompanyWalletRepository tourCompanyWalletRepository;
    private final PasswordEncoder passwordEncoder;

    public TourCompanyServiceImpl(
            TourCompanyRepository tourCompanyRepository, 
            TourCompanyLoginRepository tourCompanyLoginRepository, 
            PasswordEncoder passwordEncoder, 
            TourCompanyWalletRepository tourCompanyWalletRepository) {
        this.tourCompanyRepository = tourCompanyRepository;
        this.tourCompanyLoginRepository = tourCompanyLoginRepository;
        this.passwordEncoder = passwordEncoder;
        this.tourCompanyWalletRepository = tourCompanyWalletRepository;
    }

    @Override
    @Transactional
    public TourCompany registerTourCompany(RegisterTourCompanyDto payload) {
        logger.debug("[registerTour] newly tour company is registering...");
        var companyName = payload.name();
        var tourCompany = new TourCompany(
                null,
                companyName,
                TourCompanyStatus.WAITING.name());
        var newTourCompany = tourCompanyRepository.save(tourCompany);
        logger.debug("[registerTour] new tour company: {}", newTourCompany);
        createLoginContext(payload, newTourCompany);
        return newTourCompany;
    }

    @Override
    @Transactional
    public TourCompany approvedTourCompany(Integer id) {
        var tourCompany = tourCompanyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format("Tour Company Id: %s not found", id)));
        tourCompany = new TourCompany(id, tourCompany.name(), TourCompanyStatus.APPROVED.name());
        createWallet(tourCompany);
        return tourCompanyRepository.save(tourCompany);
    }

    @Override
    public void createLoginContext(RegisterTourCompanyDto body, TourCompany tourCompany) {
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        var encryptedPassword = passwordEncoder.encode(body.password());
        var tourCompanyLogin = new TourCompanyLogin(
                null,
                tourCompanyReference,
                body.username(),
                encryptedPassword);
        tourCompanyLoginRepository.save(tourCompanyLogin);
        logger.info("Created login context for company id: {}", tourCompany.id());
    }

    private TourCompanyWallet createWallet(TourCompany tourCompany) {
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        BigDecimal initialBalance = new BigDecimal("0.00");
        Instant currentTimestamp = Instant.now();
        var tourCompanyWallet = new TourCompanyWallet(null, tourCompanyReference, currentTimestamp, initialBalance);
        return tourCompanyWalletRepository.save(tourCompanyWallet);
    }
}
