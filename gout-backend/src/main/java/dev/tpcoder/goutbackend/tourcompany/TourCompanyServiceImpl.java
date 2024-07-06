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
import dev.tpcoder.goutbackend.tourcompany.dto.RegisterTourCompanyDto;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyLogin;
import dev.tpcoder.goutbackend.tourcompany.repository.TourCompanyLoginRepository;
import dev.tpcoder.goutbackend.tourcompany.repository.TourCompanyRepository;
import dev.tpcoder.goutbackend.wallet.model.TourCompanyWallet;
import dev.tpcoder.goutbackend.wallet.repository.TourCompanyWalletRepository;

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
            TourCompanyWalletRepository tourCompanyWalletRepository,
            PasswordEncoder passwordEncoder) {
        this.tourCompanyRepository = tourCompanyRepository;
        this.tourCompanyLoginRepository = tourCompanyLoginRepository;
        this.tourCompanyWalletRepository = tourCompanyWalletRepository;
        this.passwordEncoder = passwordEncoder;
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
        createCompanyCredential(newTourCompany, payload);
        return newTourCompany;
    }

    @Override
    @Transactional
    public TourCompany approvedTourCompany(Integer id) {
        var tourCompany = tourCompanyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format("Tour Company Id: %s not found", id)));
        tourCompany = new TourCompany(id, tourCompany.name(), TourCompanyStatus.APPROVED.name());
        var updatedTourCompany = tourCompanyRepository.save(tourCompany);
        createCompanyWallet(updatedTourCompany);
        return updatedTourCompany;
    }

    private void createCompanyCredential(TourCompany tourCompany, RegisterTourCompanyDto payload) {
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        var encryptedPassword = passwordEncoder.encode(payload.password());
        var companyCredential = new TourCompanyLogin(null, tourCompanyReference, payload.username(), encryptedPassword);
        tourCompanyLoginRepository.save(companyCredential);
        logger.info("Created credential for company: {}", tourCompany.id());
    }

    private void createCompanyWallet(TourCompany tourCompany) {
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        Instant currentTimestamp = Instant.now();
        BigDecimal initBalance = new BigDecimal("0.00");
        var wallet = new TourCompanyWallet(null, tourCompanyReference, currentTimestamp, initBalance);
        tourCompanyWalletRepository.save(wallet);
        logger.info("Created wallet for company: {}", tourCompany.id());
    }

}
